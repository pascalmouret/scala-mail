package scalamail

import javax.mail.internet.{MimeBodyPart, MimeMultipart}


trait MailPart {
  val contentType: ContentType
  def compile: MimeBodyPart
  def getMultipart: MimeMultipart

  implicit def contentTypeToString(ct: ContentType): String = ct.toString
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
  val contentType = ContentType.MixedMultipart
}
final case class AlternativeContainer(override val segments: Seq[MailPart]) extends Container {
  val contentType = ContentType.AlternativeMultipart
}
final case class RelatedContainer(override val segments: Seq[MailPart]) extends Container {
  val contentType = ContentType.RelatedMultipart
}

trait Content extends MailPart {
  val payload: String

  def getMultipart: MimeMultipart = new MimeMultipart(contentType.getSubytpe) {
    addBodyPart(compile)
  }

  def compile: MimeBodyPart = new MimeBodyPart {
    setContent(payload, contentType)
  }
}

final case class TextPart(override val payload: String) extends Content { val contentType = ContentType.Text }
final case class HtmlPart(override val payload: String) extends Content { val contentType = ContentType.Html }
//final case class Attachment(override val payload: Array[Byte], override val contentType: ContentType) extends Content
