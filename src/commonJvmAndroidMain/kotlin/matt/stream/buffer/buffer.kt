package matt.stream.buffer
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.nio.ByteBuffer
import java.nio.channels.FileChannel


fun ByteBuffer.grow(): ByteBuffer {
    val newBuffer = ByteBuffer.allocateDirect(capacity() * 2)
    flip()
    newBuffer.put(this)
    return newBuffer
}

suspend fun FileChannel.readsSameContentAs(
    buffer: ByteBuffer
): Boolean {

    val comparisonBuffer = ByteBuffer.allocate(GENERAL_INITIAL_CAPACITY.coerceAtMost(buffer.remaining))
    do {
        val readView = readInto(comparisonBuffer)
        if (readView == null /*end of stream*/) {
            if (buffer.hasRemaining()) {
                return false
            } else {
                return true
            }
        }
        if (readView == buffer.sliceAndAdvance(readView.remaining)) {
            comparisonBuffer.clear()
        } else {
            return false
        }
    } while (buffer.hasRemaining())
    return true
}


fun ByteBuffer.flippedReadView(): ByteBuffer {
    val view = asReadOnlyBuffer()
    view.flip()
    return view
}


val ByteBuffer.remaining: Int
    get() = remaining()

var ByteBuffer.position: Int
    get() = position()
    set(value) {
        position(value)
    }


var ByteBuffer.limit: Int
    get() = limit()
    set(value) {
        limit(value)
    }


fun ByteBuffer.sliceAndAdvance(
    length: Int
): ByteBuffer {
    check(position() + length <= limit())
    val sli = slice(position(), length)
    position(position() + length)
    return sli
}

suspend fun FileChannel.readInto(buffer: ByteBuffer): ByteBuffer? =
    withContext(IO) {
        val readCount = read(buffer)
        if (readCount == -1 /*End of Stream*/) {
            yield()
            null
        } else {
            if (readCount == 0) {
                yield()
            }
            buffer.slice(buffer.position() - readCount, readCount)
        }
    }

fun FileChannel.reset(): FileChannel {
    position(0)
    return this
}


fun NativeBuffer() = ByteBuffer(direct = true)
fun HeapBuffer() = ByteBuffer(direct = false)
private fun ByteBuffer(direct: Boolean = false): ByteBuffer =
    if (direct) ByteBuffer.allocateDirect(1000)
    else ByteBuffer.allocate(
        GENERAL_INITIAL_CAPACITY
    )

private const val GENERAL_SLICE_UNIT = 1000
private val GENERAL_INITIAL_CAPACITY =
    GENERAL_SLICE_UNIT.also {
        /*Currently they are the same, but if I ever wanted to I could make INITIAL_CAPACITY a higher multiple of SLICE_UNIT*/
        check(it % GENERAL_SLICE_UNIT == 0)
    }


