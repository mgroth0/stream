package matt.stream.redirect

import matt.lang.NEVER
import matt.model.code.output.ActualOutputStreams
import matt.prim.charset.newDefaultCharsetDecoder
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintStream
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.CharsetDecoder


class RedirectionStreams(
    out: (String) -> Unit,
    err: (String) -> Unit
): ActualOutputStreams {
    override val out = RedirectionOutputStream(out)
    override val err= RedirectionOutputStream(err)
}

private val originalOut: PrintStream = System.out
private val originalErr: PrintStream = System.err

fun printlnOriginalOut(any: Any?) = originalOut.println(any)
fun printlnOriginalErr(any: Any?) = originalErr.println(any)

fun printOriginalOut(any: Any?) = originalOut.print(any)
fun printOriginalErr(any: Any?) = originalErr.print(any)

/*absolutely must be called at the very beginning of the main method, otherwise it is possible for the original PrintStream to leak out. Even if I want to disable this at runtime, still run this early and then just redirect back to the originals. That way I always have control where the streams point to and the standard ones which I don't control do not leak out.*/
fun redirectStandardStreams(
    onOut: (String) -> Unit,
    onErr: (String) -> Unit
) {
    System.setOut(PrintStream(RedirectionOutputStream(onOut), true))
    System.setErr(PrintStream(RedirectionOutputStream(onErr), true))
}


class RedirectionOutputStream(
    private val flushChars: (String) -> Unit
) : OutputStream() {
    private val decoder: CharsetDecoder = newDefaultCharsetDecoder()

    private val charBuffer: CharBuffer = CharBuffer.allocate(1000)

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
        byteBuffer: ByteBuffer,
    ) {
        do {
            val result = decoder.decode(byteBuffer, charBuffer, false)
            val shouldContinue = when {
                result.isError     -> {
                    flushChars("ERROR IN RedirectionOutputStream. CANNOT THROW...")
                    false
                }

                result.isOverflow  -> true

                result.isUnderflow -> false
                else               -> NEVER
            }
            charBuffer.flip()
            val s = charBuffer.toString()
            charBuffer.clear()
            flushChars(s)
        } while (shouldContinue)

    }

}


/*absolutely must be called at the very beginning of the main method, otherwise it is possible for the original PrintStream to leak out*/
fun redirectOut(
    duplicate: Boolean = true,
    op: (String) -> Unit
) {
    val old = System.out
    val re = if (duplicate) redirect2Core {
        op(it)
        old.print(it)
    } else redirect2Core(op)
    System.setOut(re)
}

/*absolutely must be called at the very beginning of the main method, otherwise it is possible for the original PrintStream to leak out*/
fun redirectErr(
    duplicate: Boolean = true,
    op: (String) -> Unit
) {
    val old = System.err
    val re = if (duplicate) redirect2Core {
        op(it)
        old.print(it)
    } else redirect2Core(op)
    System.setErr(re)
}


private fun redirect2Core(op: (String) -> Unit) = PrintStream(object : ByteArrayOutputStream() {
    override fun flush() {
        val message = toString()
        if (message.isEmpty()) return
        op(message)
        reset()
    }
}, true)