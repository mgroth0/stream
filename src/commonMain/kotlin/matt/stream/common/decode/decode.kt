package matt.stream.common.decode

import kotlinx.io.bytestring.ByteString
import matt.lang.function.SuspendConsume
import matt.lang.idea.FailableIdea
import kotlin.jvm.JvmInline

suspend fun Decoder.decodeAssumingSuccessful(
    expectMoreInput: Boolean,
    consume: SuspendConsume<CharArray>
) = decode(expectMoreInput = expectMoreInput) {
    consume((it as Success).chars)
}

interface Decoder {
    suspend fun feed(byte: Byte)
    suspend fun decode(expectMoreInput: Boolean, consume: SuspendConsume<DecodeResult>)
    suspend fun reset()
}

sealed interface DecodeResult: FailableIdea
@JvmInline
value class Success(val chars: CharArray): DecodeResult


data class MalformedInput1(
    val expectedMoreInput: Boolean,
    val malformedBytes: ByteString
): DecodeResult

sealed interface FeedResult
data object ThereIsMoreInput: FeedResult
private object ThereIsNoMoreInput

