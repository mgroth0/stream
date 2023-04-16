package matt.stream.piping

import java.io.ByteArrayOutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.PrintStream

fun redirectOut(duplicate: Boolean = true, op: (String) -> Unit) {
    val old = System.out
    val re = if (duplicate) redirect2Core {
        op(it)
        old.print(it)
    } else redirect2Core(op)
    System.setOut(re)
}

fun redirectErr(duplicate: Boolean = true, op: (String) -> Unit) {
    val old = System.err
    val re = if (duplicate) redirect2Core {
        op(it)
        old.print(it)
    } else redirect2Core(op)
    System.setErr(re)
}


fun redirect2Core(op: (String) -> Unit): PrintStream {
    return PrintStream(object : ByteArrayOutputStream() {
        override fun flush() {
            val message = toString()
            if (message.isEmpty()) return
            op(message)
            reset()
        }
    }, true)
}


fun pipedPrintStream(): Pair<PrintStream, PipedInputStream> {
    val PIPE_BUFFER = 2048

    //     -> console
    val inPipe = PipedInputStream(PIPE_BUFFER)
    val outPipe = PipedOutputStream(inPipe)
    val ps = PrintStream(outPipe, true)
    //       <- stdout

    return ps to inPipe
}