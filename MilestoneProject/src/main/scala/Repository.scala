import io.circe.syntax._
import io.circe.parser.decode
import cats.syntax.either._
import io.circe._
import net.liftweb.json._
import net.liftweb.json.Serialization.write
import java.io._
import scala.io.Source
import cats.effect._, org.http4s._, org.http4s.dsl.io._, scala.concurrent.ExecutionContext.Implicits.global

//Implement in finally tagless style and F[_]
//Wrap all effects in IO
//Could use doobie database
trait Repository[A, F[_]] {
  def getAll: F[Seq[A]]
  def get(id: String): F[Option[A]]
  def create(x: A): F[Option[A]]
  def update(x: A): F[Option[A]]
  def delete(x: A): F[Option[A]]
}

object UserSearchRepository {
  def impl =
    new Repository[User, IO] {
      override def getAll: IO[Seq[User]] =
        IO(CirceParse.decodeAllUsers.toSeq)

      override def get(id: String): IO[Option[User]] =
        getAll.flatMap { (allUsers) =>
          IO(allUsers.filter((user: User) => (user.username == id)).headOption)
        }

      override def create(x: User): IO[Option[User]] = get(x.username).flatMap { (checkUser) =>
        checkUser match {
          case Some(user) => IO(None)
          case None =>
            getAll.flatMap(
              (allUsers) => {
                val seqAdd = allUsers :+ x
                val added  = Vector() ++ seqAdd
                CirceParse.encodeAllUsers(Vector() ++ seqAdd)
                IO(Some(x))
              }
            )
        }
      }

      override def update(x: User): IO[Option[User]] = get(x.username).flatMap { checkUser =>
        checkUser match {
          case None => IO(None)
          case Some(user) =>
            for {
              deleted <- delete(x)
              created <- create(x)
            } yield (Some(x))
        }
      }

      override def delete(x: User): IO[Option[User]] = get(x.username).flatMap { (checkUser) =>
        checkUser match {
          case None => IO(None)
          case Some(user) =>
            getAll.flatMap { (all) =>
              val deleted = all.filterNot((newUser: User) => (newUser.username == user.username))
              CirceParse.encodeAllUsers(Vector() ++ deleted)
              IO(Some(x))
            }
        }
      }

      def clear: IO[Unit] = IO {
        implicit val formats = DefaultFormats
        val file             = new File("database.txt")
        val bw               = new BufferedWriter(new FileWriter(file))

        bw.write("")
        bw.close()
      }
    }

}
