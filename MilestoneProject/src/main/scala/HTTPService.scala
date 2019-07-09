import cats.effect._, org.http4s._, org.http4s.dsl.io._, scala.concurrent.ExecutionContext.Implicits.global
import io.circe.parser._
import cats.implicits._
import org.http4s.circe._
import org.http4s.server.blaze._
import org.http4s.implicits._
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode

import org.http4s.client.blaze._
import org.http4s.Uri

import org.http4s.client.dsl.io._
import org.http4s.headers._
import org.http4s.MediaType._
import io.circe._
import io.circe.literal._
import org.json4s._
import org.json4s.native.Serialization._
import org.json4s.native.Serialization

object Main extends StreamApp[IO] {
  val databaseService = HttpService[IO] {
  case GET -> Root / "ping" =>
    Ok("Pong")
  case req @ POST -> Root / "create_user" =>
    val response = for {
      user <- req.as[Json]
      resp <- Ok(user.toString)
    } yield(resp)
    val resOption = parseNewUser(response.flatMap(_.as[String]).unsafeRunSync.toString)
    resOption match {
      case Some(x) => response
      case None => Forbidden()
    }
  case req @ POST -> Root / "change_password" =>
    val response = for {
      user <- req.as[Json]
      resp <- Ok(user.toString)
    } yield(resp)
    val updateOption = parseChangePassword(response.flatMap(_.as[String]).unsafeRunSync.toString)
    updateOption match {
      case Some(x) => response
      case None => Forbidden()
    }



}
  def parseNewUser(jsonString: String): Option[User] = {
    val json: Json = parse(jsonString).getOrElse(Json.Null)
    val cursor: HCursor = json.hcursor
    val username = cursor.downField("username").as[String].getOrElse("null_user")
    val pass = cursor.downField("password").as[String].getOrElse("null_pass")
    UserSearchRepository.create(User(username, pass, Vector()))
  }

  def parseChangePassword(jsonString: String): Option[User] = {
    val json: Json = parse(jsonString).getOrElse(Json.Null)
    val cursor: HCursor = json.hcursor
    val username = cursor.downField("username").as[String].getOrElse("null_user")
    val oldPass = cursor.downField("oldPassword").as[String].getOrElse("null_old_password")
    val newPass = cursor.downField("newPassword").as[String].getOrElse("null_new_password")

    oldPass match {
      case `newPass` => None
      case _ => UserSearchRepository.get(username) match {
        case Some(User(username, oldPass, x)) => UserSearchRepository.update(User(username, newPass, x))
        case _ => None
      }
    }
  }
  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8000, "localhost")
      .mountService(databaseService, "/")
      .serve
}
