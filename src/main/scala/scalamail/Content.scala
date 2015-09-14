package scalamail

import javax.mail.internet.{MimeBodyPart, MimeMultipart}


sealed trait MailPart {
  val contentType: ContentType
  val headers: Seq[(String, String)] = Seq.empty[(String, String)]

  def compile: MimeBodyPart
  def getMultipart: MimeMultipart

  implicit def contentTypeToString(ct: ContentType): String = ct.toString
}

abstract class Container(val contentType: ContentType) extends MailPart {
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

final case class MixedContainer(segments: Seq[MailPart]) extends Container(ContentType.MixedMultipart)
final case class AlternativeContainer(segments: Seq[MailPart]) extends Container(ContentType.AlternativeMultipart)
final case class RelatedContainer(segments: Seq[MailPart]) extends Container(ContentType.RelatedMultipart)

abstract class Content(val contentType: ContentType) extends MailPart {
  val payload: Any

  def getMultipart: MimeMultipart = new MimeMultipart(contentType.getSubytpe) {
    addBodyPart(compile)
  }

  def compile: MimeBodyPart = new MimeBodyPart {
    setContent(payload, contentType)
  }
}

final case class Text(payload: String) extends Content(ContentType.Text)
final case class Html(payload: String) extends Content(ContentType.Html)
