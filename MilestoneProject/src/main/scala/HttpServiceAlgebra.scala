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

//F is most commonly IO, G is most commonly Response
trait HttpService[F[_], G[F[_]]] {
  def validateUser(user: F[UserCreds]): F[G[F]]
  def createUser(user: F[UserCreds]): F[G[F]]
  def checkPassToUpdate(user: F[ChangePassword]): F[G[F]]
  def searchTermsGet: F[G[F]]
  def searchTermsPost(user: F[UserCreds]): F[G[F]]
  def mostCommonSearchGet: F[G[F]]
  def mostCommonSearchPost(user: F[UserCreds]): F[G[IO]]
  def search(user: F[UserCreds], searchString: String): F[G[IO]]
}

object HttpServiceImpl {
  import UserCode._
  def impl(repo: Repository[User, IO], fetch: Fetch[IO]): HttpService[IO, Response] =
    new HttpService[IO, Response] {

      override def validateUser(user: IO[UserCreds]): IO[Response[IO]] = {
        user.flatMap { (u) =>
          {
            repo.get(u.username).flatMap { (userCheck) =>
              userCheck match {
                case Some(x @ User(username, password, _)) if (password == u.password) => Ok(x)
                case _                                                                 => Forbidden()
              }
            }
          }
        }
      }

      override def createUser(user: IO[UserCreds]): IO[Response[IO]] = {
        user.flatMap { (u) =>
          {
            repo.get(u.username).flatMap { (repoResponse) =>
              repoResponse match {
                case Some(x) => Forbidden()
                case None =>
                  val newUser = User(u.username, u.password, Vector())
                  repo.create(newUser).flatMap(createdUser => Ok(newUser))
              }
            }
          }
        }
      }

      override def checkPassToUpdate(user: IO[ChangePassword]): IO[Response[IO]] = {
        user.flatMap { (u) =>
          {
            repo.get(u.username).flatMap { (userCheck) =>
              userCheck match {
                case Some(User(_, u.newPassword, _))           => Forbidden()
                case Some(User(name, u.oldPassword, searches)) => updatePass(User(name, u.newPassword, searches))
                case _                                         => Forbidden()
              }
            }
          }
        }
      }

      def updatePass(user: User): IO[Response[IO]] = {
        repo.update(User(user.username, user.password, user.searches)).flatMap { (updatedUser) =>
          updatedUser match {
            case None    => Forbidden()
            case Some(x) => Ok(x)
          }
        }
      }

      override def searchTermsGet: IO[Response[IO]] = {

        Ok(repo.getAll.flatMap { (getridofIO) =>
          IO(
            getridofIO
              .flatMap(user => user.searches)
              .map(search => search.searchString)
              .toSet
              .asJson
          )
        })
      }

      override def searchTermsPost(user: IO[UserCreds]): IO[Response[IO]] = {
        validateUser(user).flatMap { (validation) =>
          {
            validation match {
              case Ok(x) =>
                user.flatMap { u =>
                  {
                    repo.get(u.username).flatMap { (checkUser) =>
                      checkUser match {
                        case Some(x @ User(username, password, searches)) =>
                          Ok(searches.map((search) => search.searchString).toSet.asJson)
                        case None => Forbidden()
                      }
                    }
                  }
                }
              case _ => Forbidden()
            }
          }
        }
      }

      override def mostCommonSearchGet: IO[Response[IO]] = {
        Ok(
          repo.getAll.flatMap { (all) =>
            IO(Milestone.mostCommonSearchAllUsersFold(Vector() ++ all).asJson)
          }
        )
      }

      override def mostCommonSearchPost(user: IO[UserCreds]): IO[Response[IO]] = {
        validateUser(user).flatMap { validation =>
          {
            validation match {
              case Ok(x) =>
                user.flatMap { u =>
                  {
                    repo.get(u.username).flatMap { checkUser =>
                      checkUser match {
                        case Some(x) => Ok(Milestone.mostFrequentUserSearchFold(x).toSet.asJson)
                        case None    => Forbidden()
                      }
                    }
                  }
                }
            }
          }
        }
      }

      override def search(user: IO[UserCreds], searchString: String): IO[Response[IO]] = {
        validateUser(user).flatMap { (validation) =>
          {
            validation match {
              case Ok(x) =>
                val resultsIO: IO[Vector[Result]] = fetch.fetchResultsIO(searchString)
                user.flatMap { (u) =>
                  {
                    repo.get(u.username).flatMap { (checkUser) =>
                      checkUser match {
                        case Some(x @ User(username, password, searches)) =>
                          resultsIO flatMap { (results) =>
                            {
                              val newSearch: Search = Search(searchString, results)
                              repo
                                .update(User(username, password, searches :+ newSearch))
                                .flatMap(updated => Ok(newSearch))
                            }
                          }
                        case _ => Forbidden()
                      }
                    }
                  }
                }
              case _ => Forbidden()
            }
          }
        }
      }

    }
}
