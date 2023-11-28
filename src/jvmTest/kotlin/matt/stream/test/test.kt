package matt.stream.test


import matt.stream.ReaderEndReason
import matt.stream.ReaderEndReason.TYPE
import matt.stream.ReaderEndReason.TYPE.END_OF_STREAM
import matt.stream.ansi.ANSI_RESET
import matt.stream.ansi.ANSI_RESET_OBJ
import matt.stream.ansi.AnsiBold
import matt.stream.ansi.AnsiColor
import matt.stream.ansi.GenericAnsiCode
import matt.test.assertions.JupiterTestAssertions.assertRunsInOneMinute
import kotlin.test.Test

class StreamTests {

    @Test
    fun initObjects() {
        AnsiBold
    }

    @Test
    fun initEnums() = assertRunsInOneMinute {
        TYPE.entries
        AnsiColor.entries
    }

    @Test
    fun instantiateClasses() = assertRunsInOneMinute {
        ReaderEndReason(END_OF_STREAM, Exception())
        GenericAnsiCode("")
    }

    @Test
    fun initValues() {
        ANSI_RESET
        ANSI_RESET_OBJ
    }
}