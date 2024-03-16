@file:Suppress("ktlint:matt:no-disallowed-imports-thread")
package matt.stream.redirect

import kotlinx.coroutines.runBlocking
import matt.lang.shutdown.ShutdownContext
import matt.lang.shutdown.j.ShutdownExecutorImpl
import matt.lang.sync.common.SimpleReferenceMonitor
import matt.model.code.output.ActualOutputStreams
import matt.stream.common.decode.decodeAssumingSuccessful
import matt.stream.decode.MyCharsetDecoder
import matt.stream.redirect.log.LogFileAndChannel
import java.io.OutputStream
import java.io.PrintStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption

/*

Don't need to strictly enforce log files here, since we aren't talking about the system level output and error streams.

*/
class RedirectionStreams(
    out: (String) -> Unit,
    err: (String) -> Unit
): ActualOutputStreams {
    override val out: OutputStream = RedirectionOutputStream(null, out)
    override val err: OutputStream = RedirectionOutputStream(null, err)
}

private val originalOut: PrintStream = System.out
private val originalErr: PrintStream = System.err

fun printlnOriginalOut(any: Any?) = originalOut.println(any)
fun printlnOriginalErr(any: Any?) = originalErr.println(any)

fun printOriginalOut(any: Any?) = originalOut.print(any)
fun printOriginalErr(any: Any?) = originalErr.print(any)

/*

absolutely must be called at the very beginning of the main method, otherwise it is possible for the original PrintStream to leak out. Even if I want to disable this at runtime, still run this early and then just redirect back to the originals. That way I always have control where the streams point to and the standard ones which I don't control do not leak out.

It is enforced to have a log file.
I do not want to ever in my life regress to dealing with issues of not seeing the output I expect to see.
Also, a log file is good because it can be written to without decoding, so it is resilient to binary data.

One of the main remaining caveats is that merging the err and out to a single file can corrupt the text output. This is very unlikely but possible. Even if not corrupt on a binary level, a more likely scenario is that the text gets mixed up.

Though the inconvenience of having two log files is so high... I cannot quite justify enforcing it.

*/
fun redirectStandardStreams(
    enforcedLogFile: Path,
    onOut: (String) -> Unit,
    onErr: (String) -> Unit
): LogFileAndChannel {
    val logMonitor = SimpleReferenceMonitor()

    val log =
        LogFileAndChannel(
            writingCanBeDisabled = false,
            enforcedLogFile,
            logMonitor,
            shutdownContext = ShutdownExecutorImpl()
        )

    System.setOut(PrintStream(RedirectionOutputStream(log, onOut), true))
    System.setErr(PrintStream(RedirectionOutputStream(log, onErr), true))
    return log
}

context(ShutdownContext)
fun Path.openChannelForLogging(): FileChannel {
    val ch =
        FileChannel.open(
            this,
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        )
    duringShutdown {
        ch.close()
    }
    return ch
}



private class RedirectionOutputStream(
    private val log: LogFileAndChannel?,
    private val flushChars: (String) -> Unit
) : OutputStream() {
    private val decoder = MyCharsetDecoder()

    @Synchronized
    override fun write(b: Int) {
        TODO()
    }


    @Synchronized
    override fun write(b: ByteArray) {
        val byteBuffer = ByteBuffer.wrap(b)
        write(byteBuffer)
    }

    @Synchronized
    override fun write(
        b: ByteArray,
        off: Int,
        len: Int
    ) {
        val byteBuffer = ByteBuffer.wrap(b, off, len)
        write(byteBuffer)
    }


    override fun close() = Unit
    override fun flush() = Unit




    @Synchronized
    private fun write(
        byteBuffer: ByteBuffer
    ) {

        if (log != null) {
            val pos = byteBuffer.position() /*because I use mark inside `write`*/
            log.write(byteBuffer)
            byteBuffer.position(pos)
        }

        runBlocking {

            decoder.feedFrom(byteBuffer)
            try {
                decoder.decodeAssumingSuccessful(expectMoreInput = true) {
                    flushChars(it.concatToString())
                }
            } catch (e: Exception) {
                flushChars("ERROR IN RedirectionOutputStream: $e. CANNOT THROW...")
            }
        }
    }
}
