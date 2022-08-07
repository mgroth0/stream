package matt.stream.encoding.writer

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import matt.stream.encoding.Encoding
import java.io.OutputStream

fun OutputStream.withEncoding(encoding: Encoding) = EncodingOutputStream(encoding, this)

class EncodingOutputStream(private val encoding: Encoding, private val out: OutputStream): OutputStream() {

  init {
	require(out !is EncodingOutputStream)
  }

  @PublishedApi
  internal var isBeingUsedCorrectly = false


  @Suppress("OPT_IN_USAGE") inline fun <reified T> sendJson(o: T) {
	isBeingUsedCorrectly = true
	Json.encodeToStream(o, this)
	delimit()
	isBeingUsedCorrectly = false
  }

  fun sendString(string: String) {
	isBeingUsedCorrectly = true
	write(string.toByteArray())
	delimit()
	isBeingUsedCorrectly = false
  }

  @PublishedApi
  internal fun delimit() {
	require(isBeingUsedCorrectly)
	out.write(encoding.delimiter.code)
	flush()
  }

  override fun write(b: Int) {
	require(isBeingUsedCorrectly)
	return out.write(encoding.encode(b.toChar()).toByteArray())
  }

  override fun close() = out.close()

  override fun flush() = out.flush()

}
