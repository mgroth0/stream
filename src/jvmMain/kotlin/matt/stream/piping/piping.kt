package matt.stream.piping

import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.PrintStream


private const val PIPE_BUFFER = 2048
fun pipedPrintStream(): Pair<PrintStream, PipedInputStream> {
    val inPipe = PipedInputStream(PIPE_BUFFER)
    val outPipe = PipedOutputStream(inPipe)
    val ps = PrintStream(outPipe, true)
    return ps to inPipe
}