package matt.stream.ansi


import matt.lang.anno.Open
import matt.lang.anno.SeeURL
import matt.stream.ansi.ASCII.ESC
import matt.stream.ansi.AnsiColor.BLACK
import matt.stream.ansi.AnsiColor.BLACK_BACKGROUND
import matt.stream.ansi.AnsiColor.BLUE
import matt.stream.ansi.AnsiColor.BLUE_BACKGROUND
import matt.stream.ansi.AnsiColor.CYAN
import matt.stream.ansi.AnsiColor.CYAN_BACKGROUND
import matt.stream.ansi.AnsiColor.GREEN
import matt.stream.ansi.AnsiColor.GREEN_BACKGROUND
import matt.stream.ansi.AnsiColor.PURPLE
import matt.stream.ansi.AnsiColor.PURPLE_BACKGROUND
import matt.stream.ansi.AnsiColor.RED
import matt.stream.ansi.AnsiColor.RED_BACKGROUND
import matt.stream.ansi.AnsiColor.WHITE
import matt.stream.ansi.AnsiColor.WHITE_BACKGROUND
import matt.stream.ansi.AnsiColor.YELLOW
import matt.stream.ansi.AnsiColor.YELLOW_BACKGROUND
import kotlin.jvm.JvmInline


enum class ASCII(val char: Char) {
    ESC('\u001B');

    override fun toString(): String = char.toString()
}

interface AnsiGraphicsMode {
    val code: String

    @Open
    fun wrap(
        s: CharSequence,
        vararg extras: AnsiGraphicsMode
    ) = run {
        arrayOf(code, *extras.map { it.code }.toTypedArray()).joinToString(separator = "") + s + ANSI_RESET
    }

    @Open
    fun wrap(
        c: Char,
        vararg extras: AnsiGraphicsMode
    ): CharSequence = run {
        arrayOf(code, *extras.map { it.code }.toTypedArray()).joinToString(separator = "") + c + ANSI_RESET
    }

    private val identifyingPart get() = code.substringAfter("[").substringBefore("m")

    @Open
    val id
        get(): Int {
            return when (identifyingPart.length) {
                @SeeURL("https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797?permalink_comment_id=4424378#gistcomment-4424378")
                0    -> GenericAnsiCode("$ESC[0m").id

                else -> identifyingPart.toInt()
            }
        }
}

@SeeURL("https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797")
object AnsiBold : AnsiGraphicsMode {
    override val code = "$ESC[1m"
}

@JvmInline
value class GenericAnsiCode(override val code: String) : AnsiGraphicsMode

@SeeURL("https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797")
enum class AnsiColor(override val code: String) : AnsiGraphicsMode {
    BLACK("$ESC[30m"),
    RED("$ESC[31m"),
    GREEN("$ESC[32m"),
    YELLOW("$ESC[33m"),
    BLUE("$ESC[34m"),
    PURPLE("$ESC[35m"),
    CYAN("$ESC[36m"),
    WHITE("$ESC[37m"),

    BLACK_BACKGROUND("$ESC[40m"),
    RED_BACKGROUND("$ESC[41m"),
    GREEN_BACKGROUND("$ESC[42m"),
    YELLOW_BACKGROUND("$ESC[43m"),
    BLUE_BACKGROUND("$ESC[44m"),
    PURPLE_BACKGROUND("$ESC[46m"),
    CYAN_BACKGROUND("$ESC[46m"),
    WHITE_BACKGROUND("$ESC[47m");
}

fun CharSequence.ansiBlack() = BLACK.wrap(this)
fun Char.ansiBlack() = BLACK.wrap(this)
fun CharSequence.ansiRed() = RED.wrap(this)
fun Char.ansiRed() = RED.wrap(this)
fun CharSequence.ansiGreen() = GREEN.wrap(this)
fun Char.ansiGreen() = GREEN.wrap(this)
fun CharSequence.ansiYellow() = YELLOW.wrap(this)
fun Char.ansiYellow() = YELLOW.wrap(this)
fun CharSequence.ansiBlue() = BLUE.wrap(this)
fun Char.ansiBlue() = BLUE.wrap(this)
fun CharSequence.ansiPurple() = PURPLE.wrap(this)
fun Char.ansiPurple() = PURPLE.wrap(this)
fun CharSequence.ansiCyan() = CYAN.wrap(this)
fun Char.ansiCyan() = CYAN.wrap(this)
fun CharSequence.ansiWhite() = WHITE.wrap(this)
fun Char.ansiWhite() = WHITE.wrap(this)

fun CharSequence.ansiBlackBackground() = BLACK_BACKGROUND.wrap(this)
fun Char.ansiBlackBackground() = BLACK_BACKGROUND.wrap(this)
fun CharSequence.ansiRedBackground() = RED_BACKGROUND.wrap(this)
fun Char.ansiRedBackground() = RED_BACKGROUND.wrap(this)
fun CharSequence.ansiGreenBackground() = GREEN_BACKGROUND.wrap(this)
fun Char.ansiGreenBackground() = GREEN_BACKGROUND.wrap(this)
fun CharSequence.ansiYellowBackground() = YELLOW_BACKGROUND.wrap(this)
fun Char.ansiYellowBackground() = YELLOW_BACKGROUND.wrap(this)
fun CharSequence.ansiBlueBackground() = BLUE_BACKGROUND.wrap(this)
fun Char.ansiBlueBackground() = BLUE_BACKGROUND.wrap(this)
fun CharSequence.ansiPurpleBackground() = PURPLE_BACKGROUND.wrap(this)
fun Char.ansiPurpleBackground() = PURPLE_BACKGROUND.wrap(this)
fun CharSequence.ansiCyanBackground() = CYAN_BACKGROUND.wrap(this)
fun Char.ansiCyanBackground() = CYAN_BACKGROUND.wrap(this)
fun CharSequence.ansiWhiteBackground() = WHITE_BACKGROUND.wrap(this)
fun Char.ansiWhiteBackground() = WHITE_BACKGROUND.wrap(this)

@SeeURL("https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797")
/*Note: the Reset color is the reset code that resets all colors and text effects, Use Default color to reset colors only.*/
val ANSI_RESET = "$ESC[0m"
val ANSI_RESET_OBJ = GenericAnsiCode(ANSI_RESET)


