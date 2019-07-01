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


//I changed header to a list of headers, it can easily be a header but this makes more sense to me as 
//http4s already returned custom type Header, which was almost a list
case class HttpResponse(header: List[String], body: String, statusCode: Int)
trait HttpClient {
    val httpClient = Http1Client[IO]().unsafeRunSync
    implicit val formats = Serialization.formats(NoTypeHints)
    //both methods should return HttpRresponse, not Unit
    def executeHttpPost(url: String, values: Map[String,String]): Unit= {
        
        val postUri = Uri.fromString(url)
        val postRequest = POST(
            postUri.valueOr(throw _),
            write(values)
        )
        val res = Ok(httpClient.expect[String](postRequest).unsafeRunSync)
        val body = res.flatMap(_.as[Json]).unsafeRunSync
        val resUnsafe = res.unsafeRunSync
        val header = resUnsafe.headers.toList.map(s => s.toString)
        val status= resUnsafe.status
        
        HttpResponse(header, body.toString, status.toString.substring(0,3).toInt)
    }
    def executeHttpGet(url: String): HttpResponse = {
        val httpClient = Http1Client[IO]().unsafeRunSync
        val getUri = Uri.fromString(url)
        val request = GET(
            getUri.valueOr(throw _)//handles potential error
        )
        val res = Ok(httpClient.expect[String](request).unsafeRunSync)
        val body = res.flatMap(_.as[Json]).unsafeRunSync
        val resUnsafe = res.unsafeRunSync
        val header = resUnsafe.headers.toList.map(s => s.toString)
        val status= resUnsafe.status
       
        HttpResponse(header, body.toString, status.toString.substring(0,3).toInt)
    }
}


object Main extends HttpClient{
    def main(args: Array[String]):Unit = {
        executeHttpGet("https://api.duckduckgo.com/?q=Dogs&format=json")
        executeHttpPost("https://postman-echo.com/post", Map("hello" -> "2", "wow"->"this doesn't work yet"))
    }
    
}
