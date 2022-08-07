package matt.stream.message

import kotlinx.serialization.Serializable
import matt.klib.depinfo.DepInfo


class ActionResult(
  val message: InterAppMessage?
)

val NOTHING_TO_SEND = ActionResult(message = null)


@Serializable
sealed interface InterAppMessage

sealed class InterAppResult: InterAppMessage


@Serializable
object FAIL: InterAppMessage

@Serializable
class Text(val text: String): InterAppMessage



@Serializable
class FileMessage(val file: SFile): InterAppMessage

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
class GoPage(val thing: String): InterAppAction

@Serializable
object GetPage: InterAppAction

@Serializable
object GetFile: InterAppAction

@Serializable
class DepInfoMessage(val depInfo: DepInfo): InterAppMessage

@Serializable
class Freecomp(val path: String): InterAppMessage