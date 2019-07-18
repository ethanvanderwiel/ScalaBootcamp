import io.circe._, io.circe.parser._
import cats.syntax.either._
import cats.effect._


object http extends HttpClient{

    def fetchResultsIO(term: String): IO[Vector[Result]] = {
      createResultsIO(searchForIO(term))
    }

    def searchForIO(term: String): IO[HttpResponse] = {
      val searchURL = s"https://api.duckduckgo.com/?q=${term}&format=json&pretty=1"
        executeHttpGetIO(searchURL)
    }

    def createResultsIO(httpIO: IO[HttpResponse]): IO[Vector[Result]] = {
      httpIO.flatMap {
        (http) => {
          val json: Json = parse(http.body).getOrElse(Json.Null)
          val cursor: HCursor = json.hcursor
          val collection = cursor.downField("RelatedTopics").values.get.map(
            (curr: Json) => {
              val currentCursor: HCursor = curr.hcursor
              currentCursor.downField("Result").as[String]
            }
          )
          val sorted = (collection.collect {
            case Right(value) => value
            case _ => None
            })
            .filter(_ != None)
          /* Uses regex to grab the search title and description. Places both into a new Result object */
          IO(
            Vector() ++ sorted.map(
              (sent) => {
                  val pattern = "(<a ([^\\s]+)>)(.*?)(</a>)(.*?)".r
                  val pattern(aTag, href, term, endTag, desc) = sent
                  Result(term, desc)
              }
            )
          )
        }
      }
    }
}
