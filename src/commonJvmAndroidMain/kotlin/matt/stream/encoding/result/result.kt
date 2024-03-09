package matt.stream.encoding.result

import matt.prim.char.CharString
import matt.stream.common.decode.MalformedInput1


sealed interface ReadSectionResult
sealed interface ReadSectionRawResult: ReadSectionResult
sealed interface ReadSectionParsedResult<out T: Any>: ReadSectionResult
sealed interface ReadSectionBothResult<out T: Any>: ReadSectionRawResult, ReadSectionParsedResult<T>

class ReadSectionRaw(val sect: String): ReadSectionRawResult {
    override fun toString() = "ReadSectionRaw[sect=$sect]"
}

class ReadSectionParsed<T: Any>(val sect: T): ReadSectionParsedResult<T> {
    override fun toString() = "ReadSectionParsed[sect=$sect]"
}

sealed interface RawReadCharResult
data object Escape: RawReadCharResult
sealed interface ReadCharResult: RawReadCharResult

@JvmInline value class ReadChar(val c: Char): ReadCharResult
data object DELIM: ReadCharResult
data object EOF: ReadCharResult, ReadSectionBothResult<Nothing>
data object TIMEOUT: ReadCharResult, ReadSectionBothResult<Nothing>


data object UNREADABLE: ReadSectionParsedResult<Nothing>



data class MalformedInput2(
    val malformedInput: MalformedInput1,
    val priorChars: CharString
): ReadCharResult, ReadSectionBothResult<Nothing>, MessageFailureReason
sealed interface MessageFailureReason

class ConnectionError(val e: Exception): MessageFailureReason



