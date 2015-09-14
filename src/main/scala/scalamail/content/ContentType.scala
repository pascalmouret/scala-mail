package scalamail.content

sealed trait ContentTypeInterface {
  def primary: String
  def subtype: String
  def fullType: String
  override def toString: String = fullType
}

abstract class ContentType(val primary: String, val subtype: String) extends ContentTypeInterface {
  def fullType: String = s"$subtype/$primary"
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

  protected class Custom(override val primary: String, override val subtype: String) extends ContentType(primary, subtype)

  // Containers
  case object MixedMultipart extends ContentType("multipart", "mixed")
  case object AlternativeMultipart extends ContentType("multipart", "alternative")
  case object RelatedMultipart extends ContentType("multipart", "related")

  // Content
  case object Html extends ContentType("text", "html")
  case object Text extends ContentType("text", "plain")
}
