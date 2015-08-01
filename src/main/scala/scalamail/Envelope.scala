package scalamail

import javax.mail.internet.InternetAddress

final case class Envelope(
  from: InternetAddress,
  to: Seq[InternetAddress],
  subject: String,
  text: String,
  cc: Option[Seq[InternetAddress]] = None,
  bcc: Option[Seq[InternetAddress]] = None,
  replyTo: Option[InternetAddress] = None
)
