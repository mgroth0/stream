package matt.stream.encoding.result

import matt.stream.message.InterAppMessage

sealed interface ReadSectionResult
sealed interface ReadSectionRawResult: ReadSectionResult
sealed interface ReadSectionParsedResult: ReadSectionResult
sealed interface ReadSectionBothResult: ReadSectionRawResult, ReadSectionParsedResult

class ReadSectionRaw(val sect: String): ReadSectionRawResult

class ReadSectionParsed(val sect: InterAppMessage): ReadSectionParsedResult

sealed interface ReadCharResult

@JvmInline value class ReadChar(val c: Char): ReadCharResult
object DELIM: ReadCharResult
object EOF: ReadCharResult, ReadSectionBothResult
object TIMEOUT: ReadCharResult, ReadSectionBothResult
