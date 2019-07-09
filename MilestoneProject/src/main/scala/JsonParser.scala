import io.circe._, io.circe.parser._
import cats.syntax.either._
import net.liftweb.json._
import net.liftweb.json.Serialization.write
import java.io._

//This case class is to help jsonlift properly format the json
case class Users(users: Vector[User])
object JsonReadWrite {

    /* Writes the Users object to the json database file. */
    def writeJson(users: Users): Unit = {
        implicit val formats = DefaultFormats
        val jsonString = write(users)
        val file = new File("database.txt")
        val bw = new BufferedWriter(new FileWriter(file))
        bw.write(jsonString)
        bw.close()
    }

    /* Parses the users data from the Json Database */
    def parseUsers(s: String): Seq[User] = s match {
        case "" => Seq()
        case _ =>
            val json: Json = io.circe.parser.parse(s).getOrElse(Json.Null)
            val cursor: HCursor = json.hcursor
            Seq() ++ cursor.downField("users")
                            .values.get.map ((current: Json) => {parseUser(current)})
    }

    /* Parses individual user data from Json Database */
    def parseUser(user: Json): User = {
        val currentCursor: HCursor = user.hcursor
        User(filterRight(currentCursor.downField("username").as[String]),
             filterRight(currentCursor.downField("password").as[String]),
            Vector() ++ currentCursor.downField("searches")
                .values
                .get
                .map((current: Json) => parseSearch(current))
        )
    }

    /* Parses individual search data from the Json Database */
    def parseSearch(search: Json): Search = {
        val currentCursor: HCursor = search.hcursor
        Search(filterRight(currentCursor.downField("searchString").as[String]),
            Vector() ++ currentCursor.downField("results")
                .values
                .get
                .map((current: Json) => parseResult(current))
        )
    }

    /* Parses individual result data from the Json Database*/
    def parseResult(result: Json) = {
        val currentCursor: HCursor = result.hcursor
        Result(filterRight(currentCursor.downField("name").as[String]),
               filterRight(currentCursor.downField("desc").as[String]))
    }


    //I'm unsure if this method is needed or if there is a way to do this.
    def filterRight(i: io.circe.Decoder.Result[String]): String =i match{
        case Right(value) => value
        case _ => ""
    }

}
