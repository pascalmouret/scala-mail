package scalamail.content

import javax.mail.internet.{MimeBodyPart, MimeMultipart}


sealed trait MailPartInterface {
  val contentType: ContentTypeInterface
  val headers: Seq[MailHeaderInterface] = Seq.empty[MailHeaderInterface]

  def compile: MimeBodyPart
  def getMultipart: MimeMultipart

  implicit def contentTypeToString(ct: ContentTypeInterface): String = ct.toString
}

abstract class Container(val contentType: ContentTypeInterface) extends MailPartInterface {
  val segments: Seq[MailPartInterface]

  def getMultipart: MimeMultipart = new MimeMultipart(contentType.subtype) {
    segments foreach { segment =>
      addBodyPart(segment.compile)
    }
  }

  def compile: MimeBodyPart = new MimeBodyPart {
    setContent(getMultipart)
  }
}

final case class MixedContainer(segments: Seq[MailPartInterface]) extends Container(ContentType.MixedMultipart)
final case class AlternativeContainer(segments: Seq[MailPartInterface]) extends Container(ContentType.AlternativeMultipart)
final case class RelatedContainer(segments: Seq[MailPartInterface]) extends Container(ContentType.RelatedMultipart)

abstract class Content(val contentType: ContentTypeInterface) extends MailPartInterface {
  val payload: Any

  def getMultipart: MimeMultipart = new MimeMultipart(contentType.subtype) {
    addBodyPart(compile)
  }

  def compile: MimeBodyPart = new MimeBodyPart {
    setContent(payload, contentType)
  }
}

final case class Text(payload: String) extends Content(ContentType.Text)
final case class Html(payload: String) extends Content(ContentType.Html)
