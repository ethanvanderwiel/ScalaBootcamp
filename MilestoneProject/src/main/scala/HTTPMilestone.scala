import io.circe._, io.circe.parser._
import cats.syntax.either._


object http extends HttpClient{

    def fetchResults(term: String): Vector[Result] = {
        createResults(searchFor(term))
    }
    def searchFor(term: String): HttpResponse = {
        val searchURL = s"https://api.duckduckgo.com/?q=${term}&format=json&pretty=1"
        executeHttpGet(searchURL)
    }


    def createResults(http: HttpResponse): Vector[Result] = {
        val json: Json = parse(http.body).getOrElse(Json.Null)
        val cursor: HCursor = json.hcursor
        /* Grabs related topics array, maps through and creates a new iterable of topics */
        val collection = cursor.downField("RelatedTopics").values.get.map(
            (curr: Json) => {
                val currentCursor: HCursor = curr.hcursor
                currentCursor.downField("Result").as[String]
            }
        )
        /* Sorts through the collection by value, putting None if left.
            Filters out the none */
        val sorted = (collection.collect {
            case Right(value) => value
            case _ => None
            })
            .filter(_ != None)
        /* Uses regex to grab the search title and description. Places both into a new Result object */
        Vector() ++ sorted.map(
            (sent) => {
                val pattern = "(<a ([^\\s]+)>)(.*?)(</a>)(.*?)".r
                val pattern(aTag, href, term, endTag, desc) = sent
                Result(term, desc)
            }
        )

    }




    def main(args: Array[String]) = {
        val res = searchFor("Dogs")
        println(createResults(res))
    }

}
