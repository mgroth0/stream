package matt.stream.message
import kotlinx.serialization.Serializable

@Serializable
class SFile(val path: String) {
  override fun toString() = path
}

@Serializable
class FileList(val files: List<SFile>): List<SFile> by files