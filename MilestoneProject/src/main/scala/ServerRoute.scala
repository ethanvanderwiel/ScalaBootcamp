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

object Server {
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
}
