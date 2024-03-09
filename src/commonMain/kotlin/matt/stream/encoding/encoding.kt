package matt.stream.encoding

import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.append
import kotlinx.io.bytestring.buildByteString
import matt.prim.byte.toInt
import matt.prim.endian.MyByteOrder
import matt.prim.int.toByteArray


class Encoding(
    @PublishedApi
    internal val message: Byte,
    @PublishedApi
    internal val end: Byte
) {

    init {
        val keyBytes = listOf(message, end)
        /*check they are all unique*/
        check(keyBytes.toSet().size == keyBytes.size)
    }

    companion object {
        val DEFAULT =
            Encoding(
                message = 0x0,
                end = 0x1
            )
    }


    @PublishedApi
    internal val byteOrder = MyByteOrder.BIG

    fun encodeMessage(content: ByteString) =
        buildByteString {
            append(message)
            append(content.size.toByteArray(byteOrder))
            append(content)
        }
    fun encodeEnd() = ByteString(end)

    sealed interface StreamItem
    class Message(val content: ByteString): StreamItem
    data object End: StreamItem

    inline fun decodeNext(readByte: () -> Byte): StreamItem {
        return when (val h = readByte()) {
            message -> {
                val len = ByteArray(4) { readByte() }.toInt(byteOrder)
                return Message(
                    buildByteString {
                        repeat(len) {
                            append(readByte())
                        }
                    }
                )
            }
            end -> End
            else -> error("Unexpected Header: $h")
        }
    }
}



