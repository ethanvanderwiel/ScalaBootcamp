import cats.effect._, org.http4s._, org.http4s.dsl.io._, scala.concurrent.ExecutionContext.Implicits.global
import io.circe.parser._
import cats.implicits._
import org.http4s.circe._
import org.http4s.server.blaze._
import org.http4s.implicits._
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode
import org.http4s.client.blaze._
import org.http4s.Uri
import org.http4s.client.dsl.io._
import org.http4s.headers._
import org.http4s.MediaType._
import io.circe._
import io.circe.literal._
import org.json4s._
import org.json4s.native.Serialization._
import org.json4s.native.Serialization
import org.http4s.headers.`Content-Type`
import io.circe.generic.semiauto._
import cats.syntax.either._
import cats.data._


case class UserCreds(username: String, password: String)

object IOMain extends StreamApp[IO] {
  implicit val resultDecoder: Decoder[Result] = deriveDecoder[Result]
  implicit val resultEncoder: Encoder[Result] = deriveEncoder[Result]

  implicit val searchDecoder: Decoder[Search] = deriveDecoder[Search]
  implicit val searchEncoder: Encoder[Search] = deriveEncoder[Search]

  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
  implicit val userEncoder: Encoder[User] = deriveEncoder[User]

  implicit val userCredsDecoder: Decoder[UserCreds] = deriveDecoder[UserCreds]
  implicit val userCredsEncoder: Encoder[UserCreds] = deriveEncoder[UserCreds]

  implicit val resultEntityDecoder: EntityDecoder[IO, Result] = jsonOf
  implicit val resultEntityEncoder: EntityEncoder[IO, Result] = jsonEncoderOf

  implicit val searchEntityDecoder: EntityDecoder[IO, Search] = jsonOf
  implicit val searchEntityEncoder: EntityEncoder[IO, Search] = jsonEncoderOf

  implicit val userEntityDecoder: EntityDecoder[IO, User] = jsonOf
  implicit val userEntityEncoder: EntityEncoder[IO, User] = jsonEncoderOf

  implicit val userCredsEntityDecoder: EntityDecoder[IO, UserCreds] = jsonOf
  implicit val userCredsEntityEncoder: EntityEncoder[IO, UserCreds] = jsonEncoderOf


  val databaseService = HttpService[IO] {
    case GET -> Root / "ping" =>
      Ok("Pong")

    case req @ POST -> Root / "verify_user_cred" =>
      val user = req.as[UserCreds]
      val validation = validateUser(user)
      validation
  }

  def validateUser(user: IO[UserCreds]): IO[Response[IO]] = {
    user.flatMap {
      (u) =>  {
        UserSearchRepository.get(u.username) match {
          case Some(x@User(username, password, _)) if (password == u.password) => Ok(x)
          case _  => Forbidden()
        }
      }
    }
  }



  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(9000, "localhost")
      .mountService(databaseService, "/")
      .serve
}
