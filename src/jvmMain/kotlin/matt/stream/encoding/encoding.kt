package matt.stream.encoding

import matt.klib.lang.err
import java.io.InputStream
import java.net.SocketTimeoutException

fun InputStream.encodingReader(encoding: Encoding) = EncodingReader(encoding, this)

class EncodingReader(
  private val encoding: Encoding,
  private val input: InputStream,
) {
  private val br by lazy { input.bufferedReader() }
  fun char(): ReadCharResult = try {
	when (val c = br.read()) {
	  -1                      -> EOF
	  encoding.delimiter.code -> DELIM
	  encoding.escape.code    -> when (val cc = br.read()) {
		-1                      -> err("received EOF in escape sequence...")
		encoding.delimiter.code -> ResultChar(encoding.delimiter)
		else                    -> err("invalid escape: $cc")
	  }

	  else                    -> ResultChar(c.toChar())
	}
  } catch (e: SocketTimeoutException) {
	TIMEOUT
  } /*add any other timeout exceptions here as well and return TIMEOUT*/
}


sealed interface ReadCharResult

@JvmInline
value class ResultChar(val c: Char): ReadCharResult
object DELIM: ReadCharResult
object EOF: ReadCharResult
object TIMEOUT: ReadCharResult