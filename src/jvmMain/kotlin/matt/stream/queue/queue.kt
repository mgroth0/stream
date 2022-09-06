package matt.stream.queue

import java.util.Queue

fun <E> Queue<E>.pollUntilEnd(): List<E> {
  val list = mutableListOf<E>()
  do {
	val e = poll()
	if (e != null) {
	  list += e
	}
  } while (e != null)
  return list
}


fun <E> Queue<E>.pollSequence() = sequence<E> {
  do {
	val e = poll()
	if (e != null) yield(e)
  } while (e != null)
}