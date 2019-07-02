import io.circe.syntax._
import io.circe.parser.decode
import cats.syntax.either._
import io.circe._
import net.liftweb.json._
import net.liftweb.json.Serialization.write
import java.io._
import scala.io.Source
trait Repository[A] {
  def getAll: Seq[A]
  def get(id: String): Option[A]
  def create(x: A): Option[A]
  def update(x: A): Option[A]
  def delete(x: A): Option[A]
}


case class Users(users: Vector[User])
object Repo {

    def writeJson(users: Users): Unit = {
        implicit val formats = DefaultFormats
        val jsonString = write(users)
        val file = new File("database.txt")
        val bw = new BufferedWriter(new FileWriter(file))
        bw.write(jsonString)
        bw.close()
    }
    

    def main(args: Array[String]):Unit = {
        lazy val searchVector = Vector(
        ("Cats"),
        ("Terriers") ,    
        ("Sports"),
        ("activities"),
        ("Cities"),
        ("Emotions"),
        ("Seasons"),
        ("Examples"),
        ("People"),
        ("Agruments"),
        ("Paper"),
        ("Tired"),
        ("Music"),
        ("New"),
        ("Dancing"),
        ("Pens"),
        ("Love"),
        ("Old"),
        ("done"),
        ("finale")
    )
    lazy val userVector = Vector(
        User("user1", "userpass1", Milestone.makeSearch(Vector(searchVector(0), searchVector(1), searchVector(7)))),
        User("user2", "userpass2", Milestone.makeSearch(Vector(searchVector(1), searchVector(3), searchVector(2))))
    )
       //writeJson(Users(userVector))
       implicit val formats = DefaultFormats
        val m = Source.fromFile("database.txt").getLines.mkString
    //    val json = parse(m)
    //    val ext = json.extract[Users]
    //    println(ext)
        UserParse.userParse(m)
    }
}