package matt.stream.encoding.reader

import matt.lang.err
import matt.lang.require.requireEmpty
import matt.lang.untilIs
import matt.log.HasLogger
import matt.log.logger.Logger
import matt.stream.encoding.Encoding
import matt.stream.encoding.result.DELIM
import matt.stream.encoding.result.EOF
import matt.stream.encoding.result.ReadChar
import matt.stream.encoding.result.ReadCharResult
import matt.stream.encoding.result.ReadSectionRaw
import matt.stream.encoding.result.ReadSectionRawResult
import matt.stream.encoding.result.TIMEOUT
import java.io.Closeable
import java.io.InputStream
import java.net.SocketTimeoutException

@Suppress("unused")
fun InputStream.encodingReader(
    encoding: Encoding,
    log: Logger
) = EncodingReader(encoding, this, log)

open class EncodingReader(
    private val encoding: Encoding,
    input: InputStream,
    log: Logger,
) : HasLogger(log), Closeable {
    private val br by lazy { input.bufferedReader() }

    open fun section(): ReadSectionRawResult = decorate {
        var sect = ""

        untilIs {
            when (val c = char()) {

                is ReadChar  -> sect += c.c

                DELIM        -> ReadSectionRaw(sect)

                EOF, TIMEOUT -> c.also {
                    requireEmpty(sect) {
                        "got $c within a section:\"$sect\""
                    }
                }
            }
        }
    }

    private fun char(): ReadCharResult = try {
        when (val c = br.read()) {
            -1                      -> EOF
            encoding.delimiter.code -> DELIM
            encoding.escape.code    -> when (val cc = br.read()) {
                -1                      -> err("received matt.stream.encoding.result.EOF in escape sequence...")
                encoding.delimiter.code -> ReadChar(encoding.delimiter)
                encoding.escape.code    -> ReadChar(encoding.escape)
                else                    -> err("invalid escape: $cc")
            }

            else                    -> ReadChar(c.toChar())
        }
    } catch (e: SocketTimeoutException) {
        TIMEOUT
    } /*add any other timeout exceptions here as well and return matt.stream.encoding.result.TIMEOUT*/

    override fun close() = br.close()
}
