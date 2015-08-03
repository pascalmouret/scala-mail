package scalamail

import scala.concurrent.Future
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global

import java.util.Properties
import javax.mail.internet.MimeMessage
import javax.mail._


object Mailer {
  def apply(host: String, port: Int): Mailer = new Mailer(MailConfig(host, port))
}

private[scalamail] class Mailer(config: MailConfig) {
  lazy private val session = createSession

  def withCredentials(user: String, password: String): Mailer =
    new Mailer(config.copy(credentials = Some(Credentials(user, password))))

  def withTtls: Mailer =
    new Mailer(config.copy(ttls = true))

  private def createSession: Session = {
    val properties = new Properties {
      put("mail.smtp.host", config.host)
      put("mail.smtp.port", config.port.toString)

      if (config.ttls) {
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.auth", "true")
      }
    }

    val credentials = config.credentials.map { credentials =>
      new Authenticator {
        protected override def getPasswordAuthentication() =
          new PasswordAuthentication(credentials.user, credentials.password)
      }
    }

    Session.getInstance(properties, credentials.orNull)
  }

  private def createMessage(envelope: Envelope): MimeMessage =
    new MimeMessage(session) {
      setFrom(envelope.from)
      envelope.to.foreach(setRecipient(Message.RecipientType.TO, _))
      envelope.cc.foreach(_.foreach(setRecipient(Message.RecipientType.CC, _)))
      envelope.bcc.foreach(_.foreach(setRecipient(Message.RecipientType.BCC, _)))
      envelope.replyTo.foreach(replyTo => setReplyTo(Array(replyTo)))
      setSubject(envelope.subject)
      setText(envelope.text)
    }

  def send(envelope: Envelope): Future[Unit] = Future {
    Transport.send(createMessage(envelope))
  }
}
