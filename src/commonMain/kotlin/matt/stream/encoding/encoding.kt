package matt.stream.encoding

class Encoding(
    val delimiter: Char,
    val escape: Char
) {
    companion object {
        val DEFAULT = Encoding(
            delimiter = 1.toChar(),
            escape = 0.toChar()
        )
        val LINES = Encoding(
            delimiter = '\n',
            escape = 0.toChar()
        )
    }

    fun encode(s: String) = buildString { s.forEach { append(encode(it)) } }

    fun encode(c: Char) = when (c) {
        delimiter, escape -> escape.toString() + c.toString()
        else              -> c.toString()
    }
}


