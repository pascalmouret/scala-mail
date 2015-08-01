package scalamail

import scala.concurrent.ExecutionContext.Implicits.global

import javax.mail.internet.InternetAddress


object Test extends App {
  val mailer = Mailer("mailtrap.io", 25)
    .withTtls
    .withCredentials("22391925cb0819878", "7b47150170056d")

  val message = Envelope(
    new InternetAddress("pascal.mouret@me.com"),
    Seq(new InternetAddress("pascal.mouret@starmind.com")),
    "First Test Mail",
    "Yeah man, this is awesome"
  )

  mailer.send(message) onFailure {
    case t: Throwable => throw t
  }
}
