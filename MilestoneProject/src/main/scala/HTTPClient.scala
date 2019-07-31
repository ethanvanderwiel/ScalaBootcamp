import org.http4s.client.blaze._
import cats._, cats.effect._, cats.implicits._
import org.http4s.Uri
import scala.concurrent.ExecutionContext.Implicits.global
import org.http4s.client.dsl.io._
import org.http4s.headers._
import org.http4s.MediaType._
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.blaze._
import org.http4s.circe._
import io.circe._
import io.circe.literal._
import org.json4s._
import org.json4s.native.Serialization._
import org.json4s.native.Serialization
import cats.data._
import io.circe.generic.semiauto._
import io.circe.parser._, io.circe.syntax._
import scala.annotation.unspecialized
import org.http4s.client._
import org.http4s.client

case class HttpResponse(header: List[String], body: String, statusCode: Int)

trait HttpClient[F[_]] {
  def executeHttpPostIO(url: String, values: Map[String, String]): F[HttpResponse]
  def executeHttpGetIO(url: String): F[HttpResponse]
}

object HttpClient {
  def impl(httpClientIO: IO[Client[IO]]): HttpClient[IO] =
    new HttpClient[IO] {
      override def executeHttpPostIO(url: String, values: Map[String, String]): IO[HttpResponse] = {
        val postRequest = POST(
          Uri.fromString(url).valueOr(throw _),
          values.asJson
        )
        makeReq(postRequest)
      }

      override def executeHttpGetIO(url: String): IO[HttpResponse] =
        makeReq(GET(Uri.fromString(url).valueOr(throw _)))

      def makeReq(req: IO[Request[IO]]): IO[HttpResponse] = {
        httpClientIO flatMap { (client) =>
          {
            val res = Ok(client.expect[String](req))
            for {
              response <- res
              body     <- response.as[String]
            } yield
              HttpResponse(
                response.headers.toList.map(s => s.toString),
                body,
                response.status.toString.substring(0, 3).toInt
              )
          }
        }
      }
    }

}
