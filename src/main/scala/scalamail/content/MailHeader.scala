package scalamail.content

import java.util.Date
import javax.mail.internet.MailDateFormat


trait MailHeaderInterface {
  def name: String
  def content: String
}

final case class MailHeader(
  name: String,
  content: String
) extends MailHeaderInterface

object MailHeader {
  private val mailDateFormat = new MailDateFormat()

  def DateHeader(date: Date): MailHeader =
    MailHeader(HeaderName.Date, mailDateFormat.format(date))
}

object HeaderName {
  val Date = "Date"
}
