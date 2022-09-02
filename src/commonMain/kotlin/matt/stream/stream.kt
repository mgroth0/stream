package matt.stream


import matt.klib.lang.err
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.UNKNOWN
import kotlin.contracts.contract

class StreamClassForDepAnalysis()

fun <T> mutableListsOf(num: Int) = (0 until num).map { mutableListOf<T>() }
fun <T> listsOf(num: Int) = (0 until num).map { listOf<T>() }

inline fun <T> Iterable<T>.first(errorMessage: String, predicate: (T)->Boolean): T {
  for (element in this) if (predicate(element)) return element
  throw NoSuchElementException("Collection contains no element matching the predicate (${errorMessage}).")
}


inline fun <E> MutableList<E>.iterateM(op: MutableListIterator<E>.(E)->Unit) {
  return listIterator().whileHasNext(op)
}

inline fun <E> List<E>.iterateL(op: ListIterator<E>.(E)->Unit) {
  return listIterator().whileHasNext(op)
}

inline fun <E> Iterable<E>.iterate(op: Iterator<E>.(E)->Unit) {
  return iterator().whileHasNext(op)
}

fun <T> Iterable<T>.filterNotIn(vararg matches: T): List<T> {
  return filterTo(ArrayList()) { it !in matches }
}

fun <T> Iterable<T>.filterNot(vararg matches: T) = filterNotIn(*matches)
fun <T> Iterable<T>.except(vararg matches: T) = filterNotIn(*matches)
fun <T> Iterable<T>.besides(vararg matches: T) = filterNotIn(*matches)


fun <T> Iterable<T>.filterIn(vararg matches: T): List<T> {
  return filterTo(ArrayList()) { it in matches }
}

@OptIn(ExperimentalContracts::class)
inline fun <E, I: Iterator<E>> I.whileHasNext(op: I.(E)->Unit) {
  contract {
	callsInPlace(op, UNKNOWN)
  }
  while (hasNext()) {
	val n = next()
	op(n)
  }
}

inline fun <T> Iterable<T>.forEachNested(action: (T, T)->Unit): Unit {
  for (element1 in this) for (element2 in this) action(element1, element2)
}

inline fun <T, R> Iterable<T>.mapNested(converter: (T, T)->R): List<R> {
  val r = mutableListOf<R>()
  for (element1 in this) for (element2 in this) r += converter(element1, element2)
  return r
}

//inline fun <T,R> Iterable<T>.mapNested(action: (T, T)->R): List<R> {
//  for (element1 in this) for (element2 in this) action(element1, element2)
//  listOf<Int>().map {  }
//  return mapTo(ArrayList<R>(collectionSizeOrDefault(10)), {  })
//}

fun <T> Sequence<T>.onEvery(ith: Int, action: (T)->Unit): Sequence<T> {
  return mapIndexed { index, t ->
	if (index%ith == 0) action(t)
	t
  }
}

fun <T> Sequence<T>.onEveryIndexed(ith: Int, action: (Int, T)->Unit): Sequence<T> {
  return mapIndexed { index, t ->
	if (index%ith == 0) action(index, t)
	t
  }
}

inline fun <T> Array<out T>.applyEach(action: T.()->Unit) {
  for (element in this) action.invoke(element)
}

inline fun <T> Iterable<T>.applyEach(action: T.()->Unit) {
  for (element in this) action.invoke(element)
}

inline fun <T> Sequence<T>.applyEach(action: T.()->Unit) {
  for (element in this) action.invoke(element)
}

/*does not duplicate a pairing, even considering other orders. ie if A,B has been found, B,A will not be found*/
inline fun <T> Sequence<T>.forEachPairing(action: Pair<T, T>.()->Unit) {
  val unique = toSet().toList()
  var i = -1
  unique.forEach { a ->
	i += 1
	unique.subList(i + 1, unique.size).forEach { b ->
	  (a to b).action()
	}
  }
}

/*does not duplicate a pairing, even considering other orders. ie if A,B has been found, B,A will not be found*/
inline fun <T> Iterable<T>.forEachPairing(action: Pair<T, T>.()->Unit) {
  asSequence().forEachPairing(action)
}


fun Array<FloatArray>.flatten() = FloatArray(this.map { it.size }.sum()).also { r ->
  var i = 0
  forEach {
	it.forEach {
	  r[i] = it
	  i++
	}
  }
}

@Suppress("SimplifiableCall", "UNCHECKED_CAST") fun <T> Array<T>.filterNotNull(): List<T & Any> =
  filter { it != null } as List<T & Any>
//@Suppress("SimplifiableCall", "UNCHECKED_CAST") fun FloatArray.filterNotNull(): List<Float> = filter { it != null } as List<T & Any>

inline fun <T> Iterable<T>.firstOrErr(msg: String, predicate: (T)->Boolean): T {
  for (element in this) if (predicate(element)) return element
  err(msg)
}


fun <T> Collection<T>.only(): T {
  require(this.size == 1)
  return first()
}

fun <T> Sequence<T>.only() = iterator().only()
fun <T> Iterable<T>.only() = iterator().only()
fun <T> Iterator<T>.only(): T {
  val r = next()
  require(!hasNext())
  return r
}


fun <E> List<E>.sameContentsAnyOrder(list: List<E>): Boolean {
  if (size != list.size) return false
  val tempList = list.toMutableList()
  forEach {
	if (!tempList.remove(it)) return false
  }
  return tempList.isEmpty()
}

/*from is inclusive*/
fun <E> List<E>.subList(from: Int) = subList(from, size)

fun <E> List<E?>.filterNotNull(): List<E> = mapNotNull { it }
fun <E> Sequence<E?>.filterNotNull(): Sequence<E> = mapNotNull { it }