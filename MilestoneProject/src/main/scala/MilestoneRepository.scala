import io.circe.syntax._
import io.circe.parser.decode
import cats.syntax.either._
import io.circe._
import net.liftweb.json._
import net.liftweb.json.Serialization.write
import java.io._
import scala.io.Source


//Implement in finally tagless style and F[_]
//Wrap all effects in IO
//Could use doobie database
trait Repository[A] {
  def getAll: Seq[A]
  def get(id: String): Option[A]
  def create(x: A): Option[A]
  def update(x: A): Option[A]
  def delete(x: A): Option[A]
}

object UserSearchRepository extends Repository[User]{

    /* Gets all of the current users by accessing the json database file and parsing it */
    override def getAll: Seq[User] = {
        CirceParse.decodeAllUsers.toSeq
    }

    /* Gets a single user. Returns Some(user) if user exists, otherwise None */
    override def get(id: String): Option[User] = {
        getAll.filter((user: User) => (user.username == id)).headOption
    }

    /* Creates a new user. If user already exists, returns None. */
    override def create(x: User): Option[User] = get(x.username) match {
        case Some(user) => None
        case None =>val seqAdd = getAll :+ x
                    val added = Vector() ++ seqAdd
                    CirceParse.encodeAllUsers(added)
                    Some(x)
    }

    /* Updates an existing user with new data. Simply deletes the user and creates a new one.
     * Returns none if user doesn't exist
     */
    override def update(x: User): Option[User] = get(x.username) match {
        case None => None
        case Some(user) =>  delete(x)
                            create(x)
                            Some(x)
    }

  /* Deletes a current user. Returns None if given user doesn't exist */
    override def delete(x: User): Option[User] = get(x.username) match {
        case None => None
        case Some(user) => val deleted = Vector() ++ getAll.filterNot((newUser: User) => (newUser.username == user.username))
                            CirceParse.encodeAllUsers(deleted)
                            Some(x)

    }

    /* Clears the databse. Mostly used for testing. */
    def clear = {
        implicit val formats = DefaultFormats
        val file = new File("database.txt")
        val bw = new BufferedWriter(new FileWriter(file))
        bw.write("")
        bw.close()
    }
}
