package matt.stream.encoding

import matt.klib.lang.err
import matt.klib.lang.untilIs
import matt.klib.log.HasLogger
import matt.klib.log.Logger
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream
import java.net.SocketTimeoutException

class EncodingOutputStream(val encoding: Encoding, val out: OutputStream): OutputStream() {

  fun delimit() {
	out.write(encoding.delimiter.code)
  }

  override fun write(b: Int) {
	out.write(encoding.encode(b.toChar()).toByteArray())
  }

  override fun close() {
	out.close()
  }

  override fun flush() {
	out.flush()
  }

}

fun InputStream.encodingReader(encoding: Encoding, log: Logger) = EncodingReader(encoding, this, log)

open class EncodingReader(
  private val encoding: Encoding, input: InputStream, log: Logger
): HasLogger(log), Closeable {
  private val br by lazy { input.bufferedReader() }

  fun section(): ReadSectionResult = decorate {
	var sect = ""
	untilIs {
	  when (val c = char()) {

		is ReadChar  -> sect += c.c

		DELIM        -> ReadSection(sect)

		EOF, TIMEOUT -> c.also {
		  require(sect.isEmpty()) {
			"got $c within a section:\"$sect\""
		  }
		}
	  }
	}
  }

  fun char(): ReadCharResult = try {
	when (val c = br.read()) {
	  -1                      -> EOF
	  encoding.delimiter.code -> DELIM
	  encoding.escape.code    -> when (val cc = br.read()) {
		-1                      -> err("received EOF in escape sequence...")
		encoding.delimiter.code -> ReadChar(encoding.delimiter)
		encoding.escape.code    -> ReadChar(encoding.escape)
		else                    -> err("invalid escape: $cc")
	  }

	  else                    -> ReadChar(c.toChar())
	}
  } catch (e: SocketTimeoutException) {
	TIMEOUT
  } /*add any other timeout exceptions here as well and return TIMEOUT*/

  override fun close() = br.close()
}

sealed interface ReadSectionResult

@JvmInline value class ReadSection(val sect: String): ReadSectionResult

sealed interface ReadCharResult

@JvmInline value class ReadChar(val c: Char): ReadCharResult
object DELIM: ReadCharResult
object EOF: ReadCharResult, ReadSectionResult
object TIMEOUT: ReadCharResult, ReadSectionResult

