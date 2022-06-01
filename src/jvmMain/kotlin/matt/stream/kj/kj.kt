package matt.stream.kj
import java.io.IOException
import java.io.Reader

@Throws(IOException::class)
fun Reader.readWithTimeout(timeoutMillis: Int): String {
  val entTimeMS = System.currentTimeMillis() + timeoutMillis
  var r = ""
  var c: Int
  while (System.currentTimeMillis() < entTimeMS) {
	if (ready()) {
	  c = read()
	  if (c == -1) {
		if (r.isNotEmpty()) return r else throw EndOfStreamException()
	  }
	  r += c.toChar().toString()
	}
  }
  return r
}

@Throws(IOException::class)
fun Reader.readLineWithTimeout(timeoutMillis: Int): String {
  val entTimeMS = System.currentTimeMillis() + timeoutMillis
  var r = ""
  var c: Int
  while (System.currentTimeMillis() < entTimeMS) {
	if (ready()) {
	  c = read()
	  when {
		c == '\n'.code            -> return r
		c == -1 && r.isNotEmpty() -> return r
		c == -1                   -> throw EndOfStreamException()
	  }
	  r += c.toChar().toString()
	}
  }
  return r
}

class EndOfStreamException: Exception()