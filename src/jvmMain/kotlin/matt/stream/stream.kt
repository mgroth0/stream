@file:JvmName("StreamJvmKt")

package matt.stream

import matt.stream.ReaderEndReason.TYPE.END_OF_STREAM
import matt.stream.ReaderEndReason.TYPE.IO_EXCEPTION
import matt.stream.recurse.recurse
import java.io.IOException
import java.io.Reader
import kotlin.reflect.KClass

class ReaderEndReason(val type: TYPE, val exception: Exception? = null) {
  enum class TYPE {
	END_OF_STREAM,
	IO_EXCEPTION
  }
}

fun Reader.forEachChar(op: (String)->Unit): ReaderEndReason {
  var s: String
  var c: Int
  try {
	while (true) {
	  c = read()
	  if (c == -1) {
		return ReaderEndReason(type = END_OF_STREAM)
	  }
	  s = c.toChar().toString()
	  if (s.isNotEmpty()) op(s)
	}
  } catch (e: IOException) {
	return ReaderEndReason(type = IO_EXCEPTION, exception = e)
  }
}

fun Process.forEachOutChar(op: (String)->Unit) = inputStream.bufferedReader().forEachChar {
  op(it)
}

fun Process.forEachErrChar(op: (String)->Unit) = errorStream.bufferedReader().forEachChar {
  op(it)
}


fun <T: Any> KClass<out T>.recurseSealedClasses() = recurse {
  it.sealedSubclasses
}

fun <T: Any> Sequence<KClass<out T>>.objectInstances() = mapNotNull { it.objectInstance }.toList()

fun <E> MutableList<E>.replaceEvery(a: E, b: E) = replaceAll {
  when (it) {
	a    -> b
	else -> it
  }
}