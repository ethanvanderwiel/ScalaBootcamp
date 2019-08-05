import io.circe.parser._, io.circe.syntax._
import io.circe._
import org.http4s.client.blaze._
import cats._, cats.effect._, cats.implicits._
import org.http4s.Uri
import scala.concurrent.ExecutionContext.Implicits.global
import org.http4s.client.dsl._
import org.http4s.headers._
import org.http4s.MediaType._
import cats.effect._
import org.http4s._
import org.http4s.dsl._
import org.http4s.server.blaze._
import org.http4s.circe._
import scala.annotation.unspecialized
import org.http4s.client._
import org.http4s.client

case class HttpResponse(header: List[String], body: String, statusCode: Int)

trait HttpClient[F[_]] {
  def executeHttpPostIO(url: String, values: Map[String, String]): F[HttpResponse]
  def executeHttpGetIO(url: String): F[HttpResponse]
}

object HttpClient {
  def impl[F[_]: Sync](httpClientIO: Client[F]): HttpClient[F] =
    new HttpClient[F] with Http4sDsl[F] with Http4sClientDsl[F] {
      override def executeHttpPostIO(url: String, values: Map[String, String]): F[HttpResponse] = {
        val postRequest = POST[Json](
          values.asJson,
          Uri.fromString(url).valueOr(throw _)
        )
        makeReq(postRequest)
      }

      override def executeHttpGetIO(url: String): F[HttpResponse] =
        makeReq(GET(Uri.fromString(url).valueOr(throw _)))

      def makeReq(req: F[Request[F]]): F[HttpResponse] = {

        val res = Ok(httpClientIO.expect[String](req))
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
