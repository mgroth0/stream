package matt.stream.bytestr

import kotlinx.io.bytestring.ByteString
import java.io.OutputStream

fun OutputStream.write(bytes: ByteString) = write(bytes.toByteArray())
