package matt.stream.kj

import kotlinx.coroutines.delay
import java.io.Reader
import java.lang.System.currentTimeMillis

//@Throws(EndOfStreamException::class)
//fun Reader.readWithTimeout(timeoutMillis: Int): String {
//  val entTimeMS = currentTimeMillis() + timeoutMillis
//  var r = ""
//  var c: Int
//  while (currentTimeMillis() < entTimeMS) {
//	if (ready()) {
//	  c = read()
//	  if (c == -1) {
//		if (r.isNotEmpty()) return r else throw EndOfStreamException()
//	  }
//	  r += c.toChar().toString()
//	}
//  }
//  return r
//}
//
//@Throws(EndOfStreamException::class)
//fun Reader.readLineWithTimeout(timeoutMillis: Int): String {
//  val entTimeMS = currentTimeMillis() + timeoutMillis
//  var r = ""
//  var c: Int
//  while (currentTimeMillis() < entTimeMS) {
//	if (ready()) {
//	  c = read()
//	  when {
//		c == '\n'.code            -> return r
//		c == -1 && r.isNotEmpty() -> return r
//		c == -1                   -> throw EndOfStreamException()
//	  }
//	  r += c.toChar().toString()
//	}
//  }
//  return r
//}

//class EndOfStreamException: Exception()

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun Reader.readLineOrSuspend(suspendMillis: Long): String? {
  var r = ""
  var c: Int
  while (true) {
	if (ready()) {
	  c = read()
	  when {
		c == '\n'.code /*10*/     -> return r
		c == -1 && r.isNotEmpty() -> return r
		c == -1                   -> return null
	  }
	  r += c.toChar().toString()
	} else {
	  delay(suspendMillis)
	}
  }
}