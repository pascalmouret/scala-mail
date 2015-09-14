package scalamail

import javax.mail.internet.InternetAddress

import scalamail.content.{MailHeaderInterface, MailHeader, MailPartInterface}


final case class Envelope(
  from: InternetAddress,
  to: Seq[InternetAddress],
  subject: String,
  content: MailPartInterface,
  cc: Option[Seq[InternetAddress]] = None,
  bcc: Option[Seq[InternetAddress]] = None,
  replyTo: Option[InternetAddress] = None,
  headers: List[MailHeaderInterface] = List.empty[MailHeaderInterface])
{
  def withHeader(header: MailHeaderInterface): Envelope =
    copy(headers = headers :+ header)

  def withHeader(header: String, content: String): Envelope =
    withHeader(MailHeader(header, content))

  def withHeaders(headers: List[MailHeaderInterface]): Envelope =
    copy(headers = this.headers ++ headers)

  def findHeader(name: String): Option[MailHeaderInterface] =
    headers.find(_.name == name)

  def hasHeader(name: String): Boolean =
    findHeader(name).isDefined
}

