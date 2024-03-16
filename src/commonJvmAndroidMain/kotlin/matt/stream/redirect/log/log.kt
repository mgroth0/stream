package matt.stream.redirect.log

import matt.lang.function.Produce
import matt.lang.shutdown.ShutdownContext
import matt.lang.shutdown.j.ShutdownExecutorImpl
import matt.lang.sync.common.SimpleReferenceMonitor
import matt.stream.redirect.openChannelForLogging
import java.nio.ByteBuffer
import java.nio.channels.ClosedChannelException
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.createParentDirectories


class LogFileAndChannel(
    val writingCanBeDisabled: Boolean,
    val path: Path,
    @PublishedApi internal val monitor: SimpleReferenceMonitor,
    shutdownContext: ShutdownContext
) {
    inline fun <R> synchronizedLogOperation(op: Produce<R>): R =
        synchronized(monitor) {
            op()
        }
    private var channelClosed = false
    private fun markChannelDidClose() {
        channelClosed = true
    }

    private val mainOutputChannel =
        lazy {
            synchronizedLogOperation {
                check(!channelClosed)
                path.createParentDirectories()
                val ch = with(ShutdownExecutorImpl()) { path.openChannelForLogging() }
                /*this is bootstrap-level context, so yes just use a regular shutdown hook*/
                shutdownContext.duringShutdown {
                    synchronizedLogOperation {
                        ch.close()
                    }
                }
                ch
            }
        }

    fun closeMainChannel() {
        synchronizedLogOperation {
            if (!channelClosed) {
                check(mainOutputChannel.isInitialized())
                mainOutputChannel.value.close()
            }
            markChannelDidClose()
        }
    }

    fun write(source: ByteBuffer) {
        synchronizedLogOperation<Unit> {
            if (!channelClosed) {
                val channel = mainOutputChannel.value
                try {
                    source.mark()
                    channel.write(source)
                } catch (e: ClosedChannelException) {
                    /*This could happen if I print to standard out inside a shutdown hook which runs after the shutdown hook that closed the channel.*/
                    markChannelDidClose()
                    source.reset()
                    writeToEnforcedLogWithTempAppendChannel(source)
                }
            } else {
                writeToEnforcedLogWithTempAppendChannel(source)
            }
        }
    }

    private fun writeToEnforcedLogWithTempAppendChannel(
        byteBuffer: ByteBuffer
    ) {
        /*Still use ch for synchronization. I do not want multiple of these channels touching the file at the same time.*/
        synchronizedLogOperation {
            path.createParentDirectories()
            val tempChannel =
                FileChannel.open(
                    path,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE
                )
            tempChannel.use {
                tempChannel.write(byteBuffer)
            }
        }
    }
}
