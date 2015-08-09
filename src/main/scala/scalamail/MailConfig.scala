package scalamail

private[scalamail] case class MailConfig(
  host: String,
  port: Int,
  ttls: Boolean = false,
  debug: Boolean = false,
  credentials: Option[Credentials] = None
)

private[scalamail] case class Credentials(
  user: String,
  password: String
)
