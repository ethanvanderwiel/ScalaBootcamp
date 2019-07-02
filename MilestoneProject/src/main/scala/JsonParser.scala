import io.circe._, io.circe.parser._
import cats.syntax.either._

object UserParse {
    def userParse(s: String) = {
        val json: Json = parse(s).getOrElse(Json.Null)
        val cursor: HCursor = json.hcursor
        val collection = cursor.downField("users").values.get.map (
            (curr: Json) => {
                val currentCursor: HCursor = curr.hcursor
                User(filterRight(currentCursor.downField("username").as[String]), 
                    filterRight(currentCursor.downField("password").as[String]),
                    Vector() ++ currentCursor.downField("searches").values.get.map(
                        (curr: Json) => {
                            val currentCursor: HCursor = curr.hcursor
                            Search(filterRight(currentCursor.downField("searchString").as[String]), 
                            Vector() ++ currentCursor.downField("results").values.get.map(
                                (curr: Json) => {
                                    val currentCursor: HCursor = curr.hcursor
                                    Result(filterRight(currentCursor.downField("name").as[String]),
                                            filterRight(currentCursor.downField("desc").as[String]))
                                }
                            )
                            )}
                    )
                )
            }
            )
        println(collection)
    
    }
    def filterRight(i: io.circe.Decoder.Result[String]): String =i match{
        case Right(value) => value
        case _ => ""
    }
}