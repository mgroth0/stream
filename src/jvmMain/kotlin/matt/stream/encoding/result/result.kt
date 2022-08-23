package matt.stream.encoding.result

import matt.klib.lang.toStringBuilder
import matt.stream.message.InterAppMessage

sealed interface ReadSectionResult
sealed interface ReadSectionRawResult: ReadSectionResult
sealed interface ReadSectionParsedResult: ReadSectionResult
sealed interface ReadSectionBothResult: ReadSectionRawResult, ReadSectionParsedResult

class ReadSectionRaw(val sect: String): ReadSectionRawResult {
  override fun toString() = toStringBuilder(::sect)
}

class ReadSectionParsed(val sect: InterAppMessage): ReadSectionParsedResult {
  override fun toString() = toStringBuilder(::sect)
}

sealed interface ReadCharResult

@JvmInline value class ReadChar(val c: Char): ReadCharResult
object DELIM: ReadCharResult
object EOF: ReadCharResult, ReadSectionBothResult
object TIMEOUT: ReadCharResult, ReadSectionBothResult