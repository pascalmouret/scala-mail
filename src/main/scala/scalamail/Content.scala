package scalamail

import javax.mail.internet.{MimeBodyPart, MimeMultipart}


trait ContentType {
  val kind: String

  // TODO: make safe
  def getPrimary: String = kind.split("/")(0)
  def getSubytpe: String = kind.split("/")(1)
}

final case class CustomContentType(override val kind: String) extends ContentType
case object MixedMultipart extends ContentType { val kind = "multipart/mixed" }
case object AlternativeMultipart extends ContentType { val kind = "multipart/alternative" }
case object RelatedMultipart extends ContentType { val kind = "multipart/related" }
case object Html extends ContentType { val kind = "text/html" }
case object Text extends ContentType { val kind = "text/plain" }

trait MailPart {
  val contentType: ContentType
  def compile: MimeBodyPart
  def getMultipart: MimeMultipart
}

trait Container extends MailPart {
  val segments: Seq[MailPart]

  def getMultipart: MimeMultipart = new MimeMultipart(contentType.getSubytpe) {
    segments foreach { segment =>
      addBodyPart(segment.compile)
    }
  }

  def compile: MimeBodyPart = new MimeBodyPart {
    setContent(getMultipart)
  }
}

final case class MixedContainer(override val segments: Seq[MailPart]) extends Container {
  val contentType = MixedMultipart
}
final case class AlternativeContainer(override val segments: Seq[MailPart]) extends Container {
  val contentType = AlternativeMultipart
}
final case class RelatedContainer(override val segments: Seq[MailPart]) extends Container {
  val contentType = RelatedMultipart
}

trait Content extends MailPart {
  val payload: String

  def getMultipart: MimeMultipart = new MimeMultipart(contentType.getSubytpe) {
    addBodyPart(compile)
  }

  def compile: MimeBodyPart = new MimeBodyPart {
    setContent(payload, contentType.kind)
  }
}

final case class TextPart(override val payload: String) extends Content { val contentType = Text }
final case class HtmlPart(override val payload: String) extends Content { val contentType = Html }
//final case class Attachment(override val payload: Array[Byte], override val contentType: ContentType) extends Content
