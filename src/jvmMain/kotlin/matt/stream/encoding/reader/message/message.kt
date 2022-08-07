package matt.stream.encoding.reader.message

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import matt.klib.log.HasLogger
import matt.klib.log.Logger
import matt.stream.encoding.Encoding
import matt.stream.encoding.reader.EncodingReader
import matt.stream.encoding.result.EOF
import matt.stream.encoding.result.ReadSectionParsed
import matt.stream.encoding.result.ReadSectionParsedResult
import matt.stream.encoding.result.ReadSectionRaw
import matt.stream.encoding.result.TIMEOUT
import matt.stream.message.InterAppMessage
import java.io.Closeable
import java.io.InputStream

fun InputStream.messageReader(encoding: Encoding, log: Logger) = MessageReader(encoding, this, log)

open class MessageReader(
  encoding: Encoding,
  input: InputStream,
  log: Logger,
): HasLogger(log), Closeable {

  private val encodingReader by lazy {
	EncodingReader(encoding = encoding, input = input, log = log)
  }

  fun message(): ReadSectionParsedResult = when (val sect = encodingReader.section()) {
	EOF               -> EOF
	TIMEOUT           -> TIMEOUT
	is ReadSectionRaw -> ReadSectionParsed(Json.decodeFromString<InterAppMessage>(sect.sect.apply {
	  println("json:${this}")
	}))
  }

  override fun close() = encodingReader.close()
}


