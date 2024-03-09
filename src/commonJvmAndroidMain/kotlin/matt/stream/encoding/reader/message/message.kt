package matt.stream.encoding.reader.message

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.io.bytestring.decodeToString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import matt.log.j.HasLogger
import matt.log.logger.Logger
import matt.stream.encoding.Encoding
import matt.stream.encoding.reader.SuspendingEncodingReader
import matt.stream.suspendchannels.SuspendingReadChannel
import java.io.Closeable


open class SuspendingMessageReader<T : Any>(
    encoding: Encoding,
    private val readChannel: SuspendingReadChannel,
    private val ser: KSerializer<T>,
    log: Logger
) : HasLogger(log), Closeable {

    private val encodingReader by lazy {
        SuspendingEncodingReader(encoding = encoding, readChannel = readChannel, log = log)
    }

    private var closed = false
    private var job: Job? = null

    context(CoroutineScope)
    @Synchronized
    fun launchMessageChannel(): ReceiveChannel<T> {
        check(!closed)
        check(job == null)
        val sectionChannel = encodingReader.launchNewMessageChannel()
        val channel = Channel<T>(UNLIMITED)
        job =
            launch {
                sectionChannel.consumeEach {
                    it.content
                    val interpreted = Json.decodeFromString(ser, it.content.decodeToString())
                    channel.send(interpreted)
                }
                channel.close()
            }
        return channel
    }

    @Synchronized
    final override fun close() {
        encodingReader.close()
        closed =  true
        job?.cancel()
    }
}
