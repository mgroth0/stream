package matt.stream.loop


fun IntRange.loop() = toList().loop()
fun <T> List<T>.loop() = Loop(this)

class Loop<T>(private val list: List<T>): Iterable<T> {

  override fun iterator() = object: ListIterator<T> {
	var lastIndex: Int? = null
	override fun hasNext() = list.isNotEmpty()
	override fun hasPrevious() = list.isNotEmpty()
	override fun next(): T {
	  lastIndex = nextIndex()
	  return list[lastIndex!!]
	}

	override fun nextIndex() = when {
	  list.isEmpty()             -> throw NoSuchElementException()
	  lastIndex == null          -> 0
	  lastIndex == list.size - 1 -> 0
	  else                       -> lastIndex!! + 1
	}

	override fun previous(): T {
	  lastIndex = previousIndex()
	  return list[lastIndex!!]
	}

	override fun previousIndex() = when {
	  list.isEmpty()    -> throw NoSuchElementException()
	  lastIndex == null -> list.size - 1
	  lastIndex == 0    -> list.size - 1
	  else              -> lastIndex!! - 1
	}
  }
}