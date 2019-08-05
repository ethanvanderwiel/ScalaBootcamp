import cats.effect._, org.http4s._, org.http4s.dsl.io._, scala.concurrent.ExecutionContext.Implicits.global
import io.circe.parser._
import cats.implicits._
import org.http4s.circe._
import org.http4s.server.blaze._
import org.http4s.implicits._
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
import org.http4s.client._
import org.http4s.client
import org.http4s.dsl._
import org.http4s.client.dsl._

//F is most commonly IO, G is most commonly Response
trait HttpService[F[_], G[F[_]]] {
  def validateUser(user: F[UserCreds]): F[G[F]]
  def createUser(user: F[UserCreds]): F[G[F]]
  def checkPassToUpdate(user: F[ChangePassword]): F[G[F]]
  def searchTermsGet: F[G[F]]
  def searchTermsPost(user: F[UserCreds]): F[G[F]]
  def mostCommonSearchGet: F[G[F]]
  def mostCommonSearchPost(user: F[UserCreds]): F[G[F]]
  def search(user: F[UserCreds], searchString: String): F[G[F]]
}

object HttpServiceImpl {
  import UserCode._

  def impl[F[_]](repo: Repository[User, F], fetch: Fetch[F])(implicit F: Sync[F]): HttpService[F, Response] =
    new HttpService[F, Response] with Http4sDsl[F] with Http4sClientDsl[F] {
      implicit val resultEntityDecoderF: EntityDecoder[F, Result] = jsonOf
      implicit val resultEntityEncoderF: EntityEncoder[F, Result] = jsonEncoderOf

      implicit val searchEntityDecoderF: EntityDecoder[F, Search] = jsonOf
      implicit val searchEntityEncoderF: EntityEncoder[F, Search] = jsonEncoderOf

      implicit val userEntityDecoderF: EntityDecoder[F, User] = jsonOf
      implicit val userEntityEncoderF: EntityEncoder[F, User] = jsonEncoderOf

      override def validateUser(user: F[UserCreds]): F[Response[F]] = {
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

      override def createUser(user: F[UserCreds]): F[Response[F]] = {
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

      override def checkPassToUpdate(user: F[ChangePassword]): F[Response[F]] = {
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

      def updatePass(user: User): F[Response[F]] = {
        repo.update(User(user.username, user.password, user.searches)).flatMap { (updatedUser) =>
          updatedUser match {
            case None    => Forbidden()
            case Some(x) => Ok(x)
          }
        }
      }

      override def searchTermsGet: F[Response[F]] = {

        Ok(repo.getAll.flatMap { (getridofIO) =>
          F.delay(
            getridofIO
              .flatMap(user => user.searches)
              .map(search => search.searchString)
              .toSet
              .asJson
          )
        })
      }

      override def searchTermsPost(user: F[UserCreds]): F[Response[F]] = {
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

      override def mostCommonSearchGet: F[Response[F]] = {
        Ok(
          repo.getAll.flatMap { (all) =>
            F.delay(Milestone.mostCommonSearchAllUsersFold(Vector() ++ all).asJson)
          }
        )
      }

      override def mostCommonSearchPost(user: F[UserCreds]): F[Response[F]] = {
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

      override def search(user: F[UserCreds], searchString: String): F[Response[F]] = {
        validateUser(user).flatMap { (validation) =>
          {
            validation match {
              case Ok(x) =>
                val resultsIO: F[Vector[Result]] = fetch.fetchResultsIO(searchString)
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
