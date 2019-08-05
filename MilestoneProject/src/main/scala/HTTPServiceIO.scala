import cats.effect._, org.http4s._, org.http4s.dsl.io._, scala.concurrent.ExecutionContext.Implicits.global
import io.circe.parser._
import cats.implicits._
import org.http4s.circe._
import org.http4s.server.blaze._
import org.http4s.implicits._
import cats.effect.{ExitCode, IO, IOApp}
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
import io.circe.syntax._

case class UserCreds(username: String, password: String)
case class ChangePassword(username: String, oldPassword: String, newPassword: String)

object UserCode {
  implicit val resultDecoder: Decoder[Result] = deriveDecoder[Result]
  implicit val resultEncoder: Encoder[Result] = deriveEncoder[Result]

  implicit val searchDecoder: Decoder[Search] = deriveDecoder[Search]
  implicit val searchEncoder: Encoder[Search] = deriveEncoder[Search]

  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
  implicit val userEncoder: Encoder[User] = deriveEncoder[User]

  implicit val userCredsDecoder: Decoder[UserCreds] = deriveDecoder[UserCreds]
  implicit val userCredsEncoder: Encoder[UserCreds] = deriveEncoder[UserCreds]

  implicit val changePassDecoder: Decoder[ChangePassword] = deriveDecoder[ChangePassword]
  implicit val changePassEncoder: Encoder[ChangePassword] = deriveEncoder[ChangePassword]

  implicit val resultEntityDecoder: EntityDecoder[IO, Result] = jsonOf
  implicit val resultEntityEncoder: EntityEncoder[IO, Result] = jsonEncoderOf

  implicit val searchEntityDecoder: EntityDecoder[IO, Search] = jsonOf
  implicit val searchEntityEncoder: EntityEncoder[IO, Search] = jsonEncoderOf

  implicit val userEntityDecoder: EntityDecoder[IO, User] = jsonOf
  implicit val userEntityEncoder: EntityEncoder[IO, User] = jsonEncoderOf

  implicit val userCredsEntityDecoder: EntityDecoder[IO, UserCreds] = jsonOf
  implicit val userCredsEntityEncoder: EntityEncoder[IO, UserCreds] = jsonEncoderOf

  implicit val changePassEntityDecoder: EntityDecoder[IO, ChangePassword] = jsonOf
  implicit val changePassEntityEncoder: EntityEncoder[IO, ChangePassword] = jsonEncoderOf
}

//Switch to IOApp
object IOMain extends IOApp {
  object q extends QueryParamDecoderMatcher[String]("q")
  import UserCode._

  def databaseService(httpServe: HttpService[IO, Response]) = HttpRoutes.of[IO] {
    case GET -> Root / "ping" =>
      Ok("Pong")

    case req @ POST -> Root / "verify_user_cred" =>
      httpServe.validateUser(req.as[UserCreds])

    case req @ POST -> Root / "create_user" =>
      httpServe.createUser(req.as[UserCreds])

    case req @ POST -> Root / "change_password" =>
      httpServe.checkPassToUpdate(req.as[ChangePassword])

    case GET -> Root / "search_terms" =>
      httpServe.searchTermsGet

    case req @ POST -> Root / "search_terms" =>
      httpServe.searchTermsPost(req.as[UserCreds])

    case GET -> Root / "most_common_search" =>
      httpServe.mostCommonSearchGet

    case req @ POST -> Root / "most_common_search" =>
      httpServe.mostCommonSearchPost(req.as[UserCreds])

    case req @ POST -> Root / "search" :? q(searchString) =>
      httpServe.search(req.as[UserCreds], searchString)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    //Creates algebras
    import transactor.xa
    (for {
      client <- BlazeClientBuilder[IO](scala.concurrent.ExecutionContext.global).stream
      httpclient = HttpClient.impl[IO](client)
      repo       = UserSearchRepository.impl[IO](xa)
      fetch      = Http.impl[IO](httpclient)
      httpServe  = HttpServiceImpl.impl[IO](repo, fetch)
      server <- BlazeServerBuilder[IO]
        .bindHttp(9000, "localhost")
        .withHttpApp(databaseService(httpServe).orNotFound)
        .serve
    } yield (server)).compile.drain.as(ExitCode.Success)
  }
}
