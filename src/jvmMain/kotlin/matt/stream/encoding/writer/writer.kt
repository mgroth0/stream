package matt.stream.encoding.writer

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import matt.klib.log.SystemOutLogger
import matt.klib.log.decorateGlobal
import matt.stream.encoding.Encoding
import matt.stream.message.InterAppMessage
import java.io.OutputStream

fun OutputStream.withEncoding(encoding: Encoding) = EncodingOutputStream(encoding, this)

class EncodingOutputStream(private val encoding: Encoding, private val out: OutputStream): OutputStream() {

  init {
	require(out !is EncodingOutputStream)
  }

  @PublishedApi
  internal var isBeingUsedCorrectly = false


  /*tried to do this as a reified inline so anything could be used instead of InterAppMessage... but it doesn't work. The class discriminator wasn't included in the resulting json...*/
  @Suppress("OPT_IN_USAGE") fun sendJson(o: InterAppMessage) = decorateGlobal(SystemOutLogger) {
	isBeingUsedCorrectly = true
	println("stringJson = ${Json.encodeToString(o)}")
	Json.encodeToStream<InterAppMessage>(o, this)
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