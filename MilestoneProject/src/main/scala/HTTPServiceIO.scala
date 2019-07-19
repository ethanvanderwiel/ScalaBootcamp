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
import io.circe.syntax._

case class UserCreds(username: String, password: String)
case class ChangePassword(username: String, oldPassword: String, newPassword: String)

object IOMain extends StreamApp[IO] {
  object q extends QueryParamDecoderMatcher[String]("q")

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

  val databaseService = HttpService[IO] {
    case GET -> Root / "ping" =>
      Ok("Pong")

    case req @ POST -> Root / "verify_user_cred" =>
      validateUser(req.as[UserCreds])

    case req @ POST -> Root / "create_user" =>
      createUser(req.as[UserCreds])

    case req @ POST -> Root / "change_password" =>
      checkPassToUpdate(req.as[ChangePassword])

    case GET -> Root / "search_terms" =>
      val searchTermsSet = UserSearchRepository.getAll
        .flatMap((user) => user.searches)
        .map((search) => search.searchString)
        .toSet
      Ok(searchTermsSet.asJson)

    case req @ POST -> Root / "search_terms" =>
      val user = req.as[UserCreds]
      validateUser(user).flatMap { (validation) =>
        {
          validation match {
            case Ok(x) =>
              user.flatMap { u =>
                {
                  UserSearchRepository.get(u.username) match {
                    case Some(x @ User(username, password, searches)) =>
                      Ok(searches.map((search) => search.searchString).toSet.asJson)
                    case None => Forbidden()
                  }
                }
              }
            case _ => Forbidden()
          }
        }
      }

    case GET -> Root / "most_common_search" =>
      Ok(Milestone.mostCommonSearchAllUsersFold(Vector() ++ UserSearchRepository.getAll).asJson)

    case req @ POST -> Root / "most_common_search" =>
      val user = req.as[UserCreds]
      validateUser(user).flatMap { validation =>
        {
          validation match {
            case Ok(x) =>
              user.flatMap { u =>
                {
                  UserSearchRepository.get(u.username) match {
                    case Some(x) => Ok(Milestone.mostFrequentUserSearchFold(x).toSet.asJson)
                    case None    => Forbidden()
                  }
                }
              }
          }
        }
      }

    case req @ POST -> Root / "search" :? q(searchString) =>
      val user = req.as[UserCreds]
      validateUser(user).flatMap { (validation) =>
        {
          validation match {
            case Ok(x) =>
              val resultsIO: IO[Vector[Result]] = http.fetchResultsIO(searchString)
              user.flatMap { (u) =>
                {
                  UserSearchRepository.get(u.username) match {
                    case Some(x @ User(username, password, searches)) =>
                      resultsIO flatMap { (results) =>
                        {
                          val newSearch: Search = Search(searchString, results)
                          UserSearchRepository.update(User(username, password, searches :+ newSearch))
                          Ok(newSearch)
                        }
                      }
                    case _ => Forbidden()
                  }
                }
              }
            case _ => Forbidden()
          }
        }
      }
  }

  def validateUser(user: IO[UserCreds]): IO[Response[IO]] = {
    user.flatMap { (u) =>
      {
        UserSearchRepository.get(u.username) match {
          case Some(x @ User(username, password, _)) if (password == u.password) => Ok(x)
          case _                                                                 => Forbidden()
        }
      }
    }
  }

  def createUser(user: IO[UserCreds]): IO[Response[IO]] = {
    user.flatMap { (u) =>
      {
        UserSearchRepository.get(u.username) match {
          case Some(x) => Forbidden()
          case None =>
            val newUser = User(u.username, u.password, Vector())
            UserSearchRepository.create(newUser)
            Ok(newUser)
        }
      }
    }
  }

  def checkPassToUpdate(user: IO[ChangePassword]): IO[Response[IO]] = {
    user.flatMap { (u) =>
      {
        UserSearchRepository.get(u.username) match {
          case None                                      => Forbidden()
          case Some(User(_, u.newPassword, _))           => Forbidden()
          case Some(User(name, u.oldPassword, searches)) => updatePass(User(name, u.newPassword, searches))
          case Some(User(_, _, _))                       => Forbidden()
        }
      }
    }
  }

  def updatePass(user: User): IO[Response[IO]] = {
    UserSearchRepository.update(User(user.username, user.password, user.searches)) match {
      case None    => Forbidden()
      case Some(x) => Ok(user)
    }
  }

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(9000, "localhost")
      .mountService(databaseService, "/")
      .serve
}
