package matt.stream.encoding.reader

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import matt.log.j.HasLogger
import matt.log.logger.Logger
import matt.stream.encoding.Encoding
import matt.stream.encoding.Encoding.End
import matt.stream.encoding.Encoding.Message
import matt.stream.suspendchannels.BytesAvailable
import matt.stream.suspendchannels.ClosedAndNoMoreToRead
import matt.stream.suspendchannels.SuspendingReadChannel
import java.io.Closeable


class SuspendingEncodingReader(
    encoding: Encoding,
    private val readChannel: SuspendingReadChannel,
    log: Logger
): EncodingReaderBase(encoding, log), Closeable {

    private val readFlow =
        flow {
            while (true) {
                val status = readChannel.awaitBytesOrCloseAndCheck() /*makes sense to suspend here!*/
                when (status) {
                    ClosedAndNoMoreToRead -> break
                    BytesAvailable        -> {
                        val item = encoding.decodeNext { readChannel.readByte() }
                        when (item) {
                            is Message -> emit(item)
                            End        -> break
                        }
                    }
                }
            }
        }


    private var job: Job? = null
    private var closed = false

    context(CoroutineScope)
    @Synchronized
    fun launchNewMessageChannel(): ReceiveChannel<Message> {
        check(job == null)
        check(!closed)
        val channel = Channel<Message>(UNLIMITED)
        job =
            launch {
                try {
                    readFlow.collect {
                        channel.send(it)
                    }
                } finally {
                    channel.close()
                }
            }
        return channel
    }


    @Synchronized
    override fun close() {
        job?.cancel()
        closed = true
    }
}




abstract class EncodingReaderBase(
    private val encoding: Encoding,
    log: Logger
): HasLogger(log), EncodingReader

interface EncodingReader
