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

object Main {
    
    def main(args: Array[String]):Unit = {
        val httpClient = Http1Client[IO]().unsafeRunSync
        case class AuthResponse(access_token: String)
        implicit val authResponseEntityDecoder: EntityDecoder[IO, AuthResponse] = null
        val request = GET(
            Uri.uri("https://postman-echo.com/get?foo1=bar1&foo2=bar2")
        )
        val postRequest = POST(
            Uri.uri("https://my-lovely-api.com/oauth2/token"),
            UrlForm(
                "grant_type" -> "client_credentials",
                "client_id" -> "my-awesome-client",
                "client_secret" -> "s3cr3t"
            )
        )
        //println(httpClient.expect[String](request).unsafeRunSync())
        println(httpClient.expect[AuthResponse](postRequest).unsafeRunSync())
    }
    
}
