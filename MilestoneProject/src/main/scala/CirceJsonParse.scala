import io.circe._, io.circe.parser._, io.circe.syntax._, io.circe.generic.semiauto._
import cats.syntax.either._
import java.io._
import scala.io.Source

object CirceParse {
  implicit val resultDecoder: Decoder[Result] = deriveDecoder[Result]
  implicit val resultEncoder: Encoder[Result] = deriveEncoder[Result]
  implicit val searchDecoder: Decoder[Search] = deriveDecoder[Search]
  implicit val searchEncoder: Encoder[Search] = deriveEncoder[Search]
  implicit val userDecoder: Decoder[User]     = deriveDecoder[User]
  implicit val userEncoder: Encoder[User]     = deriveEncoder[User]

  def encodeAllUsers(users: Vector[User]): Json = {
    val jsonString = users.asJson
    val file       = new File("database.txt")
    val bw         = new BufferedWriter(new FileWriter(file))
    bw.write(jsonString.toString)
    bw.close()
    jsonString
  }

  def decodeAllUsers: Vector[User] = {
    val bufferedSource = Source.fromFile("database.txt")
    val stringList     = bufferedSource.getLines.mkString
    bufferedSource.close
    val json: Json = parse(stringList).getOrElse(Json.Null)
    json.as[Vector[User]].getOrElse(Vector())
  }
}
