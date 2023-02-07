package matt.stream.encoding.result



sealed interface ReadSectionResult
sealed interface ReadSectionRawResult: ReadSectionResult
sealed interface ReadSectionParsedResult: ReadSectionResult
sealed interface ReadSectionBothResult: ReadSectionRawResult, ReadSectionParsedResult

class ReadSectionRaw(val sect: String): ReadSectionRawResult {
  override fun toString() = "ReadSectionRaw[sect=$sect]"
}

class ReadSectionParsed<T: Any>(val sect: T): ReadSectionParsedResult {
  override fun toString() = "ReadSectionParsed[sect=$sect]"
}

sealed interface ReadCharResult

@JvmInline value class ReadChar(val c: Char): ReadCharResult
object DELIM: ReadCharResult
object EOF: ReadCharResult, ReadSectionBothResult
object TIMEOUT: ReadCharResult, ReadSectionBothResult
object UNREADABLE: ReadSectionParsedResult
