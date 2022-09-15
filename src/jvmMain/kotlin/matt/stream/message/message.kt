package matt.stream.message

import kotlinx.serialization.Serializable


class ActionResult(
  val message: InterAppMessage?
)

val NOTHING_TO_SEND = ActionResult(message = null)

val YES_ACTION = ActionResult(message = YES)

val ACTION_SUCCESS = ActionResult(message = SUCCESS)
val ACTION_FAIL = ActionResult(message = FAIL)


@Serializable
class IntMessage(val i: Int): InterAppMessage

@Serializable
class DoubleMessage(val d: Double): InterAppMessage

@Serializable
class LongMessage(val l: Long): InterAppMessage


@Serializable
sealed interface InterAppMessage

sealed class InterAppResult: InterAppMessage

@Serializable sealed interface Result: InterAppMessage

@Serializable
object SUCCESS: Result

@Serializable
object FAIL: Result

@Serializable
class Text(val text: String): InterAppMessage

@Serializable
object YES: InterAppMessage


@Serializable
class FileMessage(val file: SFile): InterAppMessage

@Serializable
class FilesMessage(vararg val files: SFile): InterAppMessage {
  constructor(files: List<SFile>): this(*files.toTypedArray())
}

@Serializable
sealed interface InterAppAction: InterAppMessage

@Serializable
object ACTIVATE: InterAppAction

@Serializable
object EXIT: InterAppAction

@Serializable
object ARE_YOU_RUNNING: InterAppAction

@Serializable
object GET_ACTIVE_FILE: InterAppAction

@Serializable
object GET_ALL_OPEN_FILES: InterAppAction

@Serializable
class Go(val id: String): InterAppAction

@Serializable
class Open(val thing: String): InterAppAction

@Serializable
object CLOSE: InterAppAction

@Serializable
class OpenRelative(val thing: String): InterAppAction

@Serializable
object OpenNearestGradleBuildscript: InterAppAction

@Serializable
object OpenNearestBuildJson: InterAppAction

@Serializable
object OpenNearestKotlinDescendant: InterAppAction

@Serializable
class HarvardAuthor(val thing: String): InterAppAction

@Serializable
class HarvardAuthorMeta(val thing: String): InterAppAction

@Serializable
class KJGNav(val thing: String): InterAppAction

@Serializable
class GoPage(val pageIndex: Int): InterAppAction

@Serializable
object GetPageIndex: InterAppAction

@Serializable
object GetFile: InterAppAction

@Serializable
data class PDFFileMessage(val file: String): InterAppMessage

@Serializable
data class PDFPageMessage(val pageNum: Int): InterAppMessage

@Serializable
class ObjMessage<T>(val obj: T): InterAppMessage

@Serializable
class Freecomp(val path: String): InterAppMessage