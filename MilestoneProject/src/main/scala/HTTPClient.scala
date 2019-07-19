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

//I changed header to a list of headers, it can easily be a header but this makes more sense to me as
//http4s already returned custom type Header, which was almost a list
//Use map/flatmap/forcomps to deal with IO, open in tests
case class HttpResponse(header: List[String], body: String, statusCode: Int)

trait HttpClient {

  val httpClientIO = Http1Client[IO]()

  def executeHttpPostIO(url: String, values: Map[String, String]): IO[HttpResponse] = {
    val postRequest = POST(
      Uri.fromString(url).valueOr(throw _),
      values.asJson
    )
    makeReq(postRequest)
  }

  def executeHttpGetIO(url: String): IO[HttpResponse] = {
    val request = GET(
      Uri.fromString(url).valueOr(throw _)
    )
    makeReq(request)
  }

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

  def main(args: Array[String]) = {
    val user1      = Map("username" -> "user1", "password"    -> "userpass1")
    val changePass = Map("username" -> "user1", "oldPassword" -> "userpass1", "newPassword" -> "newuserpass1")

    val user2 = Map("username" -> "user2", "password" -> "userpass2")
    val user3 = Map("username" -> "user3", "password" -> "userpass3")

    //println(executeHttpPostIO("http://localhost:9000/verify_user_cred", newUser).unsafeRunSync)
    println(executeHttpGetIO("http://localhost:9000/ping").unsafeRunSync)
    // executeHttpPostIO("http://localhost:9000/create_user", user1).unsafeRunSync
    // executeHttpPostIO("http://localhost:9000/create_user", user2).unsafeRunSync
    // executeHttpPostIO("http://localhost:9000/create_user", user3).unsafeRunSync
    //println(executeHttpPostIO("http://localhost:9000/change_password", changePass).unsafeRunSync)
    // executeHttpPostIO("http://localhost:9000/search?q=cat", user1).unsafeRunSync
    executeHttpPostIO("http://localhost:9000/search?q=cat", user1).unsafeRunSync
    // executeHttpPostIO("http://localhost:9000/search?q=dog", user1).unsafeRunSync
    // executeHttpPostIO("http://localhost:9000/search?q=house", user2).unsafeRunSync
    // executeHttpPostIO("http://localhost:9000/search?q=cat", user2).unsafeRunSync
    // executeHttpPostIO("http://localhost:9000/search?q=mouse", user3).unsafeRunSync
    // println(executeHttpGetIO("http://localhost:9000/search_terms").unsafeRunSync)
    println(executeHttpPostIO("http://localhost:9000/search_terms", user1).unsafeRunSync)
    println(executeHttpPostIO("http://localhost:9000/most_common_search", user1).unsafeRunSync)
    println(executeHttpGetIO("http://localhost:9000/most_common_search").unsafeRunSync)
  }

}
