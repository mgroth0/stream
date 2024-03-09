package matt.stream.suspendchannels

import kotlinx.io.bytestring.ByteString


interface SuspendingReadChannel {
    suspend fun readByte(): Byte
    suspend fun readUByte(): UByte
    suspend fun readLine(): String?
    suspend fun readAllBytes(): ByteString
    suspend fun readInt(): Int
    suspend fun readLong(): Long
    suspend fun readDouble(): Double
    suspend fun readFloat(): Float
    suspend fun readChar(): Char
    suspend fun readShort(): Short
    suspend fun readNBytes(n: Int): ByteString
    suspend fun readBool(): Boolean
    suspend fun checkNow(): ReadChannelStatus
    suspend fun awaitBytesOrCloseAndCheck(): AwaitedReadChannelStatus
}
interface SuspendingWriteChannel {
    suspend fun writeBytes(bytes: ByteArray)
    suspend fun writeLine(s: String)
    suspend fun writeByte(byte: Byte)
    suspend fun writeBool(bool: Boolean)
    suspend fun writeInt(int: Int)
    suspend fun writeLong(long: Long)
    suspend fun writeDouble(double: Double)
    suspend fun writeShort(short: Short)
    suspend fun writeFloat(float: Float)
}
suspend fun SuspendingWriteChannel.writeBytes(bytes: ByteString) = writeBytes(bytes.toByteArray())

sealed interface ReadChannelStatus
data object OpenButNoBytesAvailable: ReadChannelStatus /*not waiting for more to read*/
sealed interface AwaitedReadChannelStatus: ReadChannelStatus
data object BytesAvailable : AwaitedReadChannelStatus
data object ClosedAndNoMoreToRead : AwaitedReadChannelStatus
