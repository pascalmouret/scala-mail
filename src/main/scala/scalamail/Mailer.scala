package scalamail


import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import java.util.{Date, Properties}
import javax.mail.internet.MimeMessage
import javax.mail._


object Mailer {
  def apply(config: MailConfig): Mailer = new Mailer(config)
  def apply(host: String, port: Int): Mailer = Mailer(MailConfig(host, port))
}

private[scalamail] class Mailer(config: MailConfig) {
  lazy private val session = createSession

  def withCredentials(user: String, password: String): Mailer =
    Mailer(config.copy(credentials = Some(Credentials(user, password))))

  def withTtls: Mailer =
    Mailer(config.copy(ttls = true))

  def withDebug: Mailer =
    Mailer(config.copy(debug = true))

  private def createSession: Session = {
    val properties = new Properties {
      put("mail.smtp.host", config.host)
      put("mail.smtp.port", config.port.toString)

      if (config.ttls) {
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.auth", "true")
      }
    }

    val credentials = config.credentials map { credentials =>
      new Authenticator {
        protected override def getPasswordAuthentication() =
          new PasswordAuthentication(credentials.user, credentials.password)
      }
    }

    val session = Session.getInstance(properties, credentials.orNull)
    if (config.debug) session.setDebug(true)
    session
  }

  def send(envelope: Envelope): Future[Unit] = Future {
    Transport.send(MessageBuilder(envelope, session))
  }
}
