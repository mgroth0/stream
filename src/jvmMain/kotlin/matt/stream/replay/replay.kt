package matt.stream.replay

import matt.lang.sync.SimpleReferenceMonitor
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
            TODO("Not yet implemented")
        }

        override fun available(): Int {
            return super.available()
        }

        override fun close() {
            super.close()
        }

        override fun markSupported(): Boolean {
            return super.markSupported()
        }

        override fun readAllBytes(): ByteArray {
            return super.readAllBytes()
        }

        override fun read(b: ByteArray): Int {
            return super.read(b)
        }

        override fun readNBytes(len: Int): ByteArray {
            return super.readNBytes(len)
        }

        override fun read(
            b: ByteArray,
            off: Int,
            len: Int
        ): Int {
            return super.read(b, off, len)
        }

        override fun mark(readlimit: Int) {
            super.mark(readlimit)
        }

        override fun readNBytes(
            b: ByteArray?,
            off: Int,
            len: Int
        ): Int {
            return super.readNBytes(b, off, len)
        }

        override fun skip(n: Long): Long {
            return super.skip(n)
        }

        override fun skipNBytes(n: Long) {
            super.skipNBytes(n)
        }

        override fun transferTo(out: OutputStream?): Long {
            return super.transferTo(out)
        }

        override fun reset() {
            super.reset()
        }

    }

}



