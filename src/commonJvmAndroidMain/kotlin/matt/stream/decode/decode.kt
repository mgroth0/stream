package matt.stream.decode

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import matt.lang.charset.j.DEFAULT_CHARSET
import matt.lang.common.NEVER
import matt.lang.function.SuspendConsume
import matt.prim.bytestr.toByteString
import matt.prim.charset.newDefaultCharsetDecoder
import matt.stream.common.decode.DecodeResult
import matt.stream.common.decode.Decoder
import matt.stream.common.decode.FeedResult
import matt.stream.common.decode.MalformedInput1
import matt.stream.common.decode.Success
import matt.stream.common.decode.ThereIsMoreInput
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.channels.ReadableByteChannel
import java.nio.charset.Charset
import java.nio.charset.CharsetDecoder


class MyCharsetDecoder private constructor(
    private val javaCharsetDecoder: CharsetDecoder = newDefaultCharsetDecoder()
): Decoder {

    constructor(charset: Charset = DEFAULT_CHARSET): this(charset.newDecoder())

    private var corrupted = false

    override suspend fun feed(byte: Byte): Unit =
        mutex.withLock {
            check(!corrupted)
            byteBuffer.put(byte)
        }

    suspend fun feedFrom(inputBuffer: ByteBuffer): Unit =
        mutex.withLock {
            check(!corrupted)
            byteBuffer.put(inputBuffer)
        }

    suspend fun feedFrom(channel: ReadableByteChannel): FeedResult =
        mutex.withLock {
            check(!corrupted)
            when (val numBytesRead = channel.read(byteBuffer)) {
                -1   -> ThereIsMoreInput
                else -> {
                    check(numBytesRead >= 0)
                    ThereIsMoreInput
                }
            }
        }


    private val mutex = Mutex()

    override suspend fun decode(expectMoreInput: Boolean, consume: SuspendConsume<DecodeResult>): Unit =
        mutex.withLock {
            check(!corrupted)
            byteBuffer.flip()
            do {
                val decodeResult = javaCharsetDecoder.decode(byteBuffer, charBuffer, !expectMoreInput)
                val shouldContinue =
                    when {
                        decodeResult.isError -> {
                            reset()
                            corrupted = true
                            if (decodeResult.isMalformed) {
                                val malformedInputLength = decodeResult.length()
                                val malformedBytes = ByteArray(malformedInputLength)
                                byteBuffer.get(malformedBytes)
                                consume(
                                    MalformedInput1(
                                        expectedMoreInput = expectMoreInput,
                                        malformedBytes =  malformedBytes.toByteString()
                                    )
                                )
                                return
                            }
                            throw Exception("decode error: $decodeResult")
                        }
                        decodeResult.isOverflow -> true
                        decodeResult.isUnderflow -> false
                        else -> NEVER
                    }
                /*some of these operations might be excessive/redundant/not necessary if 0 characters were read. Optimization opportunity I guess.*/
                charBuffer.flip()
                val numChars = charBuffer.remaining()
                charBuffer.get(charArray, 0, numChars)
                charBuffer.clear()
                if (numChars > 0) {
                    /*should remove copying to optimize*/
                    consume(Success(charArray.copyOfRange(0, numChars)))
                }
            } while (shouldContinue)
            byteBuffer.clear()
        }

    override suspend fun reset(): Unit =
        mutex.withLock {
            check(!corrupted)
            charBuffer.clear()
            byteBuffer.clear()
        }

    private val charBuffer: CharBuffer = CharBuffer.allocate(BUFF_SIZE)
    private val byteBuffer: ByteBuffer = ByteBuffer.allocate(BUFF_SIZE)
    private val charArray = CharArray(BUFF_SIZE)

    private companion object {
        /*got an overflow once... and in a huge rush
        const val BUFF_SIZE = 1000*/
        const val BUFF_SIZE = 10_000
    }
}
