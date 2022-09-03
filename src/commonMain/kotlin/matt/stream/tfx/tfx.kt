package matt.stream.tfx



/**
 * [forEach] with Map.Entree as receiver.
 */
inline fun <K, V> Map<K, V>.withEach(action: Map.Entry<K, V>.()->Unit) = forEach(action)

/**
 * [forEach] with the element as receiver.
 */
inline fun <T> Iterable<T>.withEach(action: T.()->Unit) = forEach(action)

/**
 * [forEach] with the element as receiver.
 */
inline fun <T> Sequence<T>.withEach(action: T.()->Unit) = forEach(action)

/**
 * [forEach] with the element as receiver.
 */
inline fun <T> Array<T>.withEach(action: T.()->Unit) = forEach(action)

/**
 * [map] with Map.Entree as receiver.
 */
inline fun <K, V, R> Map<K, V>.mapEach(action: Map.Entry<K, V>.()->R) = map(action)

/**
 * [map] with the element as receiver.
 */
inline fun <T, R> Iterable<T>.mapEach(action: T.()->R) = map(action)

/**
 * [map] with the element as receiver.
 */
fun <T, R> Sequence<T>.mapEach(action: T.()->R) = map(action)

/**
 * [map] with the element as receiver.
 */
inline fun <T, R> Array<T>.mapEach(action: T.()->R) = map(action)

/**
 * [mapTo] with Map.Entree as receiver.
 */
inline fun <K, V, R, C: MutableCollection<in R>> Map<K, V>.mapEachTo(destination: C, action: Map.Entry<K, V>.()->R) =
	mapTo(destination, action)

/**
 * [mapTo] with the element as receiver.
 */
inline fun <T, R, C: MutableCollection<in R>> Iterable<T>.mapEachTo(destination: C, action: T.()->R) =
	mapTo(destination, action)

/**
 * [mapTo] with the element as receiver.
 */
fun <T, R, C: MutableCollection<in R>> Sequence<T>.mapEachTo(destination: C, action: T.()->R) =
	mapTo(destination, action)

/**
 * [mapTo] with the element as receiver.
 */
fun <T, R, C: MutableCollection<in R>> Array<T>.mapEachTo(destination: C, action: T.()->R) = mapTo(destination, action)

