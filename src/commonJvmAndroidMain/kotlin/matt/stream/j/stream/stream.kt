package matt.stream.j.stream

import matt.lang.safeconvert.verifyToByte
import matt.prim.byte.toDouble
import matt.prim.byte.toInt
import matt.prim.byte.toLong
import matt.prim.byte.toShort
import matt.prim.double.JAVA_DOES_CANONICALIZE_NAN
import matt.prim.double.toByteArray
import matt.prim.endian.MyByteOrder
import matt.prim.int.toByteArray
import matt.prim.long.toByteArray
import java.io.InputStream
import java.io.OutputStream

fun InputStream.readBool() =
    when (val b = read()) {
        1 -> true
        0 -> false
        else -> error("expected 0 or 1 for bool but got $b")
    }

fun InputStream.readShort(order: MyByteOrder) = readNBytes(2).toShort(order)

fun InputStream.readInt(order: MyByteOrder) = readNBytes(4).toInt(order)

fun InputStream.readLong(order: MyByteOrder) = readNBytes(8).toLong(order)

fun InputStream.readByte() = read().verifyToByte()

fun InputStream.readDouble(order: MyByteOrder) = readNBytes(8).toDouble(order)

fun OutputStream.writeBool(bool: Boolean): Unit = if (bool) write(1) else write(0)

fun OutputStream.writeInt(
    int: Int,
    order: MyByteOrder
): Unit = write(int.toByteArray(order))

fun OutputStream.writeLong(
    long: Long,
    order: MyByteOrder
): Unit = write(long.toByteArray(order))

fun OutputStream.writeDouble(
    double: Double,
    order: MyByteOrder
): Unit = write(double.toByteArray(order, canonicalizeNaN = JAVA_DOES_CANONICALIZE_NAN))

fun OutputStream.writeByte(byte: Byte): Unit = write(byte.toInt())
