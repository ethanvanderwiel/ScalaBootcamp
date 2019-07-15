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



//I changed header to a list of headers, it can easily be a header but this makes more sense to me as
//http4s already returned custom type Header, which was almost a list
//Use map/flatmap/forcomps to deal with IO, open in tests
case class HttpResponse(header: List[String], body: String, statusCode: Int)
trait HttpClient{
    val httpClient = Http1Client[IO]().unsafeRunSync
    implicit val formats = Serialization.formats(NoTypeHints)


    def executeHttpPost(url: String, values: Map[String,String]): HttpResponse = {

        val postUri = Uri.fromString(url)
        val rawJson = write(values)
        val postRequest = POST(
            postUri.valueOr(throw _),
            rawJson
        )
        val res = Ok(httpClient.expect[String](postRequest).unsafeRunSync)
        val body = res.flatMap(_.as[String])
        val resUnsafe = res.unsafeRunSync
        val header = resUnsafe.headers.toList.map(s => s.toString)
        val status= resUnsafe.status

        HttpResponse(header, body.toString, status.toString.substring(0,3).toInt)
    }
    def executeHttpGet(url: String): HttpResponse = {
        val getUri = Uri.fromString(url)
        val request = GET(
            getUri.valueOr(throw _)//handles potential error
        )
        val res = Ok(httpClient.expect[String](request).unsafeRunSync)
        val body = res.flatMap(_.as[String])
        val resUnsafe = res.unsafeRunSync
        val header = resUnsafe.headers.toList.map(s => s.toString)
        val status= resUnsafe.status

        HttpResponse(header, body.toString, status.toString.substring(0,3).toInt)
    }

    def executeHttpPostIO(url: String, values: Map[String,String]): IO[HttpResponse] = {
        val postUri = Uri.fromString(url)
        val rawJson = write(values)
        val postRequest = POST(
            postUri.valueOr(throw _),
            rawJson
        )
        val res = Ok(httpClient.expect[String](postRequest))
        for {
          response <- res
          body <- response.as[String]
        } yield HttpResponse(response.headers.toList.map(s => s.toString),
                   body, response.status.toString.substring(0,3).toInt)
    }

    def executeHttpGetIO(url: String): IO[HttpResponse] = {
      val request = GET(
        Uri.fromString(url).valueOr(throw _)
      )
      val res = Ok(httpClient.expect[String](request))
      for {
        response <- res
        body <- response.as[String]
      } yield HttpResponse(response.headers.toList.map(s => s.toString),
                   body, response.status.toString.substring(0,3).toInt)
    }

    def main(args: Array[String]) = {
      val newUser = Map("username"-> "user1", "password" -> "userpass1")
      val changePass = Map("username"-> "user1", "oldPassword" -> "userpass1", "newPassword" -> "newuserpass1")
      //println(executeHttpPostIO("http://localhost:9000/verify_user_cred", newUser).unsafeRunSync)
      println(executeHttpGetIO("http://localhost:9000/ping").unsafeRunSync)
      //println(executeHttpPostIO("http://localhost:9000/create_user", newUser).unsafeRunSync)
      //println(executeHttpPostIO("http://localhost:9000/change_password", changePass).unsafeRunSync)
    }

}

