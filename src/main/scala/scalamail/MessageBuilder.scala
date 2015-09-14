package scalamail

import java.util.Date
import javax.mail.{Session, Message}
import javax.mail.internet.MimeMessage

import scalamail.content._

private[scalamail] object MessageBuilder {
  def apply(envelope: Envelope, session: Session): MimeMessage = {
    envelope
      .setDate()
      .createMessage(session)
  }

  implicit class RichEnvelope(envelope: Envelope) {
    def setDate(): Envelope = {
      if (!envelope.hasHeader(HeaderName.Date)) {
        envelope.withHeader(MailHeader.DateHeader(new Date()))
      }
      envelope
    }

    def createMessage(session: Session): MimeMessage =
      new MimeMessage(session) {
        setFrom(envelope.from)
        envelope.to.foreach(setRecipient(Message.RecipientType.TO, _))
        envelope.cc.foreach(_.foreach(setRecipient(Message.RecipientType.CC, _)))
        envelope.bcc.foreach(_.foreach(setRecipient(Message.RecipientType.BCC, _)))
        envelope.replyTo.foreach(replyTo => setReplyTo(Array(replyTo)))
        setSubject(envelope.subject)
        setSentDate(new Date())
        envelope.content match {
          case t: Text => setText(t.payload)
          case p: MailPartInterface => setContent(p.getMultipart)
        }
      }
  }
}
