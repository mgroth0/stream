package matt.stream.encoding.reader.message

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import matt.log.HasLogger
import matt.log.logger.Logger
import matt.log.mem.LogMemory
import matt.log.warn.warn
import matt.model.code.errreport.ThrowReport
import matt.stream.encoding.Encoding
import matt.stream.encoding.reader.EncodingReader
import matt.stream.encoding.result.EOF
import matt.stream.encoding.result.ReadSectionParsed
import matt.stream.encoding.result.ReadSectionParsedResult
import matt.stream.encoding.result.ReadSectionRaw
import matt.stream.encoding.result.TIMEOUT
import matt.stream.encoding.result.UNREADABLE
import java.io.Closeable
import java.io.InputStream
import kotlin.reflect.KClass

inline fun <reified T : Any> InputStream.messageReader(encoding: Encoding, log: Logger) =
    MessageReader<T>(encoding, this, T::class, log)

@OptIn(InternalSerializationApi::class)
open class MessageReader<T : Any>(
    encoding: Encoding,
    input: InputStream,
    val cls: KClass<T>,
    log: Logger,
) : HasLogger(log), Closeable {

    private val encodingReader by lazy {
        EncodingReader(encoding = encoding, input = input, log = log)
    }

    fun message(): ReadSectionParsedResult = when (val sect = encodingReader.section()) {
        EOF               -> EOF
        TIMEOUT           -> TIMEOUT
        is ReadSectionRaw -> {
            try {
                ReadSectionParsed(Json.decodeFromString(cls.serializer(), sect.sect.apply {
                    println("json:${this}")
                }))
            } catch (e: SerializationException) {
                warn("could not read json message")
                ThrowReport(Thread.currentThread(), e).print()
                UNREADABLE
            }
        }
    }

    override fun close() = encodingReader.close()
}


