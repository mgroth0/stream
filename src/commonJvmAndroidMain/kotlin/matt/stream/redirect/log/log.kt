package matt.stream.redirect.log

import matt.lang.function.Produce
import matt.lang.sync.common.SimpleReferenceMonitor
import java.nio.ByteBuffer
import java.nio.channels.ClosedChannelException
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.createParentDirectories


class LogFileAndChannel(
    val autoWritesStandardStreams: Boolean,
    val writingCanBeDisabled: Boolean,
    val path: Path,
    private val outputStream: Lazy<FileChannel>,
    @PublishedApi internal val monitor: SimpleReferenceMonitor
) {
    inline fun <R> synchronizedLogOperation(op: Produce<R>): R =
        synchronized(monitor) {
            op()
        }
    private var channelClosed = false
    private fun markChannelDidClose() {
        channelClosed = true
    }

    fun write(source: ByteBuffer) {
        synchronizedLogOperation<Unit> {
            if (!channelClosed) {
                val channel = outputStream.value
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
