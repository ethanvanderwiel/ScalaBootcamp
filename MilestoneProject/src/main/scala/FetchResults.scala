import io.circe._, io.circe.parser._
import cats.syntax.either._
import cats.effect._
import cats.syntax._
import cats.{Applicative, Monoid, Traverse}
import cats.syntax.functor._, cats.syntax.flatMap._

trait Fetch[F[_]] {
  def fetchResultsIO(term: String): F[Vector[Result]]
  def searchForIO(term: String): F[HttpResponse]
  def createResultsIO(httpIO: F[HttpResponse]): F[Vector[Result]]
}

object Http {
  def impl[F[_]](httpClient: HttpClient[F])(implicit F: Sync[F]): Fetch[F] =
    new Fetch[F] {
      override def fetchResultsIO(term: String): F[Vector[Result]] =
        createResultsIO(searchForIO(term))

      override def searchForIO(term: String): F[HttpResponse] = {
        val searchURL = s"https://api.duckduckgo.com/?q=${term}&format=json&pretty=1"
        httpClient.executeHttpGetIO(searchURL)
      }

      override def createResultsIO(httpIO: F[HttpResponse]): F[Vector[Result]] = {
        httpIO.flatMap { (http) =>
          {
            val json: Json      = parse(http.body).getOrElse(Json.Null)
            val cursor: HCursor = json.hcursor
            val collection = cursor
              .downField("RelatedTopics")
              .values
              .get
              .map(
                (curr: Json) => {
                  val currentCursor: HCursor = curr.hcursor
                  currentCursor.downField("Result").as[String]
                }
              )
            val sorted = (collection
              .collect {
                case Right(value) => value
                case _            => None
              })
              .filter(_ != None)
            /* Uses regex to grab the search title and description. Places both into a new Result object */
            F.delay(
              Vector() ++ sorted.map(
                (sent) => {
                  val pattern                                 = "(<a ([^\\s]+)>)(.*?)(</a>)(.*?)".r
                  val pattern(aTag, href, term, endTag, desc) = sent
                  Result(term, desc)
                }
              )
            )
          }
        }
      }
    }
}
