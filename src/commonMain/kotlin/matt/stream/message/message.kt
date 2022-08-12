package matt.stream.message
import kotlinx.serialization.Serializable

@Serializable
class SFile(val path: String) {
  override fun toString() = path
}