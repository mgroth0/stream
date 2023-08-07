package matt.stream.test


import matt.stream.ReaderEndReason
import matt.stream.ReaderEndReason.TYPE
import matt.stream.ReaderEndReason.TYPE.END_OF_STREAM
import matt.test.JupiterTestAssertions.assertRunsInOneMinute
import kotlin.test.Test

class StreamTests {
    @Test
    fun initEnums() = assertRunsInOneMinute {
        TYPE.entries
    }

    @Test
    fun instantiateClasses() = assertRunsInOneMinute {
        ReaderEndReason(END_OF_STREAM, Exception())
    }
}