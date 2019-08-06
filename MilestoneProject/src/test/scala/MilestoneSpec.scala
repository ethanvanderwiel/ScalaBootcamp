import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterEach
import org.specs2.specification.BeforeEach
import org.http4s.client.blaze._
import org.http4s.Uri
import org.http4s.client.dsl.io._
import cats.effect._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats._
import cats.effect.{ContextShift, IO, Sync}
import cats.data._
import cats.implicits._
import fs2.Stream
import cats.{Applicative, Monoid, Traverse}

object MilestoneSpec extends Specification {
  sequential
  lazy val searchVector = Vector(
    ("Cats"),
    ("Terriers"),
    ("Sports"),
    ("activities"),
    ("Cities"),
    ("Emotions"),
    ("Seasons"),
    ("Examples"),
    ("People"),
    ("Agruments"),
    ("Paper"),
    ("Tired"),
    ("Music"),
    ("New"),
    ("Dancing"),
    ("Pens"),
    ("Love"),
    ("Old"),
    ("done"),
    ("finale")
  )
  lazy val userVector = Vector(
    User("user1", "userpass1", Vector()),
    User("user2", "userpass2", Vector()),
    User("user3", "userpass3", Vector()),
    User("user4", "userpass4", Vector()),
    User("user5", "userpass5", Vector()),
    User("user6", "userpass6", Vector()),
    User("user7", "userpass7", Vector()),
    User("user8", "userpass8", Vector()),
    User("user9", "userpass9", Vector()),
    User("user10", "userpass10", Vector())
  )
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:test",
    "postgres",
    ""
  )
  val repo = UserSearchRepository.impl[IO](xa)
  "UserSearchRepository" should {
    "clear" in {
      val ioWrapped = for {
        x      <- repo.clear
        finish <- repo.getAll
      } yield (finish must beEqualTo(Seq()))

      ioWrapped.unsafeRunSync
    }
    "create" in {
      repo.create(userVector(0)).map(created => created must beEqualTo(Some(userVector(0)))).unsafeRunSync
    }
    "get all" in {
      val ioWrapped = for {
        x      <- repo.clear
        x      <- repo.create(userVector(0))
        x      <- repo.create(userVector(1))
        finish <- repo.getAll
      } yield (finish must beEqualTo(Seq(userVector(0), userVector(1))))

      ioWrapped.unsafeRunSync
    }
    "delete" in {
      val ioWrapped = for {
        x      <- repo.clear
        x      <- repo.create(userVector(0))
        x      <- repo.create(userVector(1))
        x      <- repo.delete(userVector(0))
        finish <- repo.getAll
      } yield (finish must beEqualTo(Seq(userVector(1))))

      ioWrapped.unsafeRunSync
    }
    "delete nonexistent" in {
      val ioWrapped = for {
        x      <- repo.clear
        x      <- repo.create(userVector(0))
        x      <- repo.create(userVector(1))
        x      <- repo.delete(userVector(2))
        finish <- repo.getAll
      } yield (finish must beEqualTo(Seq(userVector(0), userVector(1))))

      ioWrapped.unsafeRunSync
    }
    "create existent" in {
      val ioWrapped = for {
        x      <- repo.clear
        x      <- repo.create(userVector(0))
        x      <- repo.create(userVector(1))
        x      <- repo.create(userVector(0))
        finish <- repo.getAll
      } yield (finish must beEqualTo(Seq(userVector(0), userVector(1))))

      ioWrapped.unsafeRunSync()
    }
  }

}
