package matt.stream.replay

import matt.lang.sync.common.SimpleReferenceMonitor
import java.io.InputStream
import java.io.OutputStream


class InputStreamReplayer(
    private val inputStream: InputStream
) {

    init {
        TODO()
    }

    private val monitor = SimpleReferenceMonitor()

    fun nextInputStream(): InputStream = IStream()

    private inner class IStream : InputStream() {

        override fun read(): Int {
            TODO()
        }

        override fun available(): Int = super.available()

        override fun close() {
            super.close()
        }

        override fun markSupported(): Boolean = super.markSupported()

        override fun readAllBytes(): ByteArray = super.readAllBytes()

        override fun read(b: ByteArray): Int = super.read(b)

        override fun readNBytes(len: Int): ByteArray = super.readNBytes(len)

        override fun read(
            b: ByteArray,
            off: Int,
            len: Int
        ): Int = super.read(b, off, len)

        override fun mark(readlimit: Int) {
            super.mark(readlimit)
        }

        override fun readNBytes(
            b: ByteArray?,
            off: Int,
            len: Int
        ): Int = super.readNBytes(b, off, len)

        override fun skip(n: Long): Long = super.skip(n)

        override fun skipNBytes(n: Long) {
            super.skipNBytes(n)
        }

        override fun transferTo(out: OutputStream?): Long = super.transferTo(out)

        override fun reset() {
            super.reset()
        }
    }
}



