// import org.specs2.mutable.Specification
// import org.specs2.specification.BeforeAfterEach
// import org.specs2.specification.BeforeEach
// import org.http4s.client.blaze._
// import org.http4s.Uri
// import org.http4s.client.dsl.io._
// import cats.effect._
// import doobie._
// import doobie.implicits._
// import doobie.util.ExecutionContexts
// import cats._
// import cats.effect.{ContextShift, IO, Sync}
// import cats.data._
// import cats.implicits._
// import fs2.Stream
// import cats.{Applicative, Monoid, Traverse}

// object IOSpec extends Specification {
//   sequential
//   lazy val searchVector = Vector(
//     ("Cats"),
//     ("Terriers"),
//     ("Sports"),
//     ("activities"),
//     ("Cities"),
//     ("Emotions"),
//     ("Seasons"),
//     ("Examples"),
//     ("People"),
//     ("Agruments"),
//     ("Paper"),
//     ("Tired"),
//     ("Music"),
//     ("New"),
//     ("Dancing"),
//     ("Pens"),
//     ("Love"),
//     ("Old"),
//     ("done"),
//     ("finale")
//   )
//   implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

//   // A transactor that gets connections from java.sql.DriverManager and executes blocking operations
//   // on an our synchronous EC. See the chapter on connection handling for more info.
//   val xa = Transactor.fromDriverManager[IO](
//     "org.postgresql.Driver",     // driver classname
//     "jdbc:postgresql:world",     // connect URL (driver-specific)
//     "postgres",                  // user
//     "",                          // password
//     ExecutionContexts.synchronous // just for testing
//   )
//   val client = BlazeClientBuilder[IO](scala.concurrent.ExecutionContext.global).stream
//       val httpclient = HttpClient.impl(client)
//       val repo       = UserSearchRepository.impl[IO](xa)
//       val fetch      = Http.impl(httpclient)
//       val httpServe  = HttpServiceImpl.impl(repo, fetch)
//   "HttpServiceClient" should {
//     "ping" in {
//       httpclient
//         .executeHttpGetIO("http://localhost:9000/ping")
//         .map(res => res.body must beEqualTo("Pong"))
//         .unsafeRunSync
//     }
//     "create_user" in {
//       val newUser = Map("username" -> "user1", "password" -> "pass1")
//       val ioWrapped = for {
//         x      <- repo.clear
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/create_user", newUser)
//         finish <- repo.getAll
//       } yield
//         (finish.headOption
//           .getOrElse() must beEqualTo(User("user1", "pass1", Vector())))

//       ioWrapped.unsafeRunSync
//     }
//     "change_password" in {
//       val newUser    = Map("username" -> "user1", "password"    -> "pass1")
//       val newUser2   = Map("username" -> "user2", "password"    -> "pass2")
//       val changePass = Map("username" -> "user2", "oldPassword" -> "pass2", "newPassword" -> "newPass2")
//       val ioWrapped = for {
//         clear      <- repo.clear
//         created    <- httpclient.executeHttpPostIO("http://localhost:9000/create_user", newUser)
//         createdTwo <- httpclient.executeHttpPostIO("http://localhost:9000/create_user", newUser2)
//         post       <- httpclient.executeHttpPostIO("http://localhost:9000/change_password", changePass)
//         finish     <- repo.get("user2")
//       } yield
//         (finish must beEqualTo(
//           Some(User("user2", "newPass2", Vector()))
//         ))

//       ioWrapped.unsafeRunSync
//     }

//     "search" in {

//       val newUser = Map("username" -> "user1", "password" -> "pass1")
//       val ioWrapped = for {
//         clear  <- repo.clear
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/create_user", newUser)
//         finish <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=cat", newUser)
//       } yield
//         (finish.body must beEqualTo(
//           """{"searchString":"cat","results":[{"name":"Cat","desc":" A small, typically furry, carnivorous mammal. They are often called house cats when kept as..."},{"name":"Cat Stevens","desc":"A British singer-songwriter and multi-instrumentalist."},{"name":"Computed axial tomography","desc":"A CT scan, also known as computed tomography scan, makes use of computer-processed combinations..."}]}"""
//         ))

//       ioWrapped.unsafeRunSync
//     }
//     "search_terms GET" in {
//       val newUser  = Map("username" -> "user1", "password" -> "pass1")
//       val newUser2 = Map("username" -> "user2", "password" -> "pass2")

//       val ioWrapped = for {
//         x <- repo.clear
//         x <- httpclient.executeHttpPostIO("http://localhost:9000/create_user", newUser)
//         x <- httpclient.executeHttpPostIO("http://localhost:9000/create_user", newUser2)

//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=cat", newUser)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=house", newUser)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=bird", newUser2)
//         finish <- httpclient.executeHttpGetIO("http://localhost:9000/search_terms")
//       } yield
//         (finish.body must beEqualTo(
//           """["cat","house","bird"]"""
//         ))

//       ioWrapped.unsafeRunSync

//     }
//     "search_terms POST" in {
//       val newUser  = Map("username" -> "user1", "password" -> "pass1")
//       val newUser2 = Map("username" -> "user2", "password" -> "pass2")

//       val ioWrapped = for {
//         x <- repo.clear
//         x <- httpclient.executeHttpPostIO("http://localhost:9000/create_user", newUser)
//         x <- httpclient.executeHttpPostIO("http://localhost:9000/create_user", newUser2)

//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=cat", newUser)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=house", newUser)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=bird", newUser2)
//         finish <- httpclient.executeHttpPostIO("http://localhost:9000/search_terms", newUser)
//       } yield
//         (finish.body must beEqualTo(
//           """["cat","house"]"""
//         ))

//       ioWrapped.unsafeRunSync
//     }

//     "most_common_search GET" in {
//       val newUser  = Map("username" -> "user1", "password" -> "pass1")
//       val newUser2 = Map("username" -> "user2", "password" -> "pass2")
//       val ioWrapped = for {
//         x <- repo.clear
//         x <- httpclient.executeHttpPostIO("http://localhost:9000/create_user", newUser)
//         x <- httpclient.executeHttpPostIO("http://localhost:9000/create_user", newUser2)

//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=cat", newUser)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=house", newUser)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=bird", newUser2)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=cat", newUser2)
//         finish <- httpclient.executeHttpGetIO("http://localhost:9000/most_common_search")
//       } yield
//         (finish.body must beEqualTo(
//           """["cat"]"""
//         ))

//       ioWrapped.unsafeRunSync
//     }
//     "multi most_common_search GET" in {
//       val newUser  = Map("username" -> "user1", "password" -> "pass1")
//       val newUser2 = Map("username" -> "user2", "password" -> "pass2")
//       val ioWrapped = for {
//         x <- repo.clear
//         x <- httpclient.executeHttpPostIO("http://localhost:9000/create_user", newUser)
//         x <- httpclient.executeHttpPostIO("http://localhost:9000/create_user", newUser2)

//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=cat", newUser)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=house", newUser)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=bird", newUser2)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=cat", newUser2)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=house", newUser2)
//         finish <- httpclient.executeHttpGetIO("http://localhost:9000/most_common_search")
//       } yield
//         (finish.body must beEqualTo(
//           """["cat","house"]"""
//         ))
//       ioWrapped.unsafeRunSync
//     }
//     "most_common_search POST" in {
//       val newUser  = Map("username" -> "user1", "password" -> "pass1")
//       val newUser2 = Map("username" -> "user2", "password" -> "pass2")
//       val ioWrapped = for {
//         x <- repo.clear
//         x <- httpclient.executeHttpPostIO("http://localhost:9000/create_user", newUser)
//         x <- httpclient.executeHttpPostIO("http://localhost:9000/create_user", newUser2)

//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=cat", newUser)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=house", newUser)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=house", newUser)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=bird", newUser2)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=cat", newUser2)
//         x      <- httpclient.executeHttpPostIO("http://localhost:9000/search?q=house", newUser2)
//         finish <- httpclient.executeHttpPostIO("http://localhost:9000/most_common_search", newUser)
//       } yield
//         (finish.body must beEqualTo(
//           """["house"]"""
//         ))
//       ioWrapped.unsafeRunSync
//     }
//   }

// }
