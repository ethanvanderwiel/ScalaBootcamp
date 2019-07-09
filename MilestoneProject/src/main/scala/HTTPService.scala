import cats.effect._, org.http4s._, org.http4s.dsl.io._, scala.concurrent.ExecutionContext.Implicits.global
import cats.implicits._
// import cats.implicits._

import org.http4s.server.blaze._
// import org.http4s.server.blaze._

import org.http4s.implicits._


import fs2.{Stream, StreamApp}
// import fs2.{Stream, StreamApp}

import fs2.StreamApp.ExitCode
// import fs2.StreamApp.ExitCode

import org.http4s.server.blaze._
// import org.http4s.server.blaze._

object Main extends StreamApp[IO] {
  val databaseService = HttpService[IO] {
  case GET -> Root / "ping" =>
    Ok("Pong")
  case GET -> Root / "create_user" =>
    Ok("")

}
  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8000, "localhost")
      .mountService(databaseService, "/")
      .serve
}
