package scalamail

abstract class ContentType {
  val primary: String
  val subtype: String

  def getPrimary: String = primary
  def getSubytpe: String = subtype
  override def toString: String = s"$subtype/$primary"
}

object ContentType {
  def apply(primary: String, subtype: String): ContentType = new Custom(primary, subtype)

  def fromString(mimeType: String): Option[ContentType] = {
    try {
      val split = mimeType.split("/")
      Some(apply(split(0), split(1)))
    } catch {
      case t: Throwable => None
    }
  }

  protected class Custom(override val primary: String, override val subtype: String) extends ContentType

  // Containers
  case object MixedMultipart extends Custom("multipart", "mixed")
  case object AlternativeMultipart extends Custom("multipart", "alternative")
  case object RelatedMultipart extends Custom("multipart", "related")

  // Content
  case object Html extends Custom("text", "html")
  case object Text extends Custom("text", "plain")
}
