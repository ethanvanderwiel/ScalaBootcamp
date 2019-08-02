import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats._
import cats.effect.{ContextShift, IO, Sync}
import cats.data._
import cats.implicits._
import fs2.Stream
import cats.{Applicative, Monoid, Traverse}

object transactor {
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

// A transactor that gets connections from java.sql.DriverManager and executes blocking operations
// on an our synchronous EC. See the chapter on connection handling for more info.
val xa = Transactor.fromDriverManager[IO](
  "org.postgresql.Driver",     // driver classname
  "jdbc:postgresql:test",     // connect URL (driver-specific)
  "postgres",                  // user
  "",                          // password
  ExecutionContexts.synchronous // just for testing
)
}


trait Repository[A, F[_]] {
  def getAll: F[List[A]]
  def get(id: String): F[Option[A]]
  def create(x: A): F[Option[A]]
  def update(x: A): F[Option[A]]
  def delete(x: A): F[Option[A]]
}

object UserSearchRepository {
  def impl[F[_]](xa: Transactor[F])(implicit F: Sync[F]) =
    new Repository[User, F] {
      def getAll: F[List[User]] = {
        val sqlResponse = sql"""SELECT * FROM users"""
          .query[(String, String)]
          .to[List]
          .transact(xa)
        for {
          users <- sqlResponse
          user  <- users.traverse(makeUserFromUserCreds)
        } yield user
      }

      def makeUserFromUserCreds(user: (String, String)): F[User] = {
        val sqlResponse = sql"""SELECT searchString FROM searches WHERE username = '${user._1}'"""
          .query[String]
          .to[List]
          .transact(xa)

        for {
          searchStrings <- sqlResponse
          searches      <- searchStrings.traverse(makeSearchFromSearchString)
        } yield User(user._1, user._2, Vector() ++ searches)
      }

      def makeSearchFromSearchString(searchString: String): F[Search] = {
        val sqlResponse = sql"""SELECT title, description FROM results WHERE searchstring = '$searchString'"""
          .query[(String, String)]
          .to[List]
          .transact(xa)
        val listVectors = sqlResponse.map((list) => list.map(resultTuple => Result(resultTuple._1, resultTuple._2)))
        listVectors.map((results) => Search(searchString, Vector() ++ results))
      }
      override def get(id: String): F[Option[User]] = {
        val sqlResponse = sql"""SELECT password FROM users WHERE username = '$id'"""
          .query[String]
          .to[List]
          .transact(xa)
        sqlResponse.flatMap(
          (user) =>
            user match {
              case Nil       => F.delay(None)
              case head :: _ => makeUserFromUserCreds((id, head)).map(user => Some(user))
            }
        )
      }

      override def create(x: User): F[Option[User]] = get(x.username).flatMap { checkuser =>
        (checkuser) match {
          case Some(user) => F.delay(None)
          case _ =>
            val effect = sql"""insert into users (username, password) VALUES ('${x.username}', '${x.password}')""".update.run
              .transact(xa)
            for {
              f <- effect
              f <- addSearches(x)
            } yield(Some(x))

        }
      }

      def addSearches(x: User): F[Unit] = {
        val effects = x.searches.map(search => {
          for {
            f <- sql"""INSERT INTO searches (username, searchstring) VALUES ("${x.username}", "${search.searchString}")""".update.run.transact(xa)
            f <- addResults(search, x.username)
          } yield()
        })
        val wrapped: F[Vector[Unit]] = effects.sequence
        wrapped.map(_ => ())
      }

      def addResults(s: Search, username: String): F[Unit] = {
        val effects = s.results.map(result =>
          sql"""INSERT INTO results (searchstring, title, description, username) VALUES ("${s.searchString}", "${result.name}", "${result.desc}", "${username}")""".update.run.transact(xa)
        )
        val wrapped: F[Vector[Int]] = effects.sequence
        wrapped.map(_ => ())
      }

      override def update(x: User): F[Option[User]] = get(x.username).flatMap { checkUser =>
        checkUser match {
          case None => F.delay(None)
          case Some(user) =>
            for {
              deleted <- delete(x)
              created <- create(x)
            } yield(Some(x))
        }
      }
      override def delete(x: User): F[Option[User]] = get(x.username).flatMap { checkUser =>
        checkUser match {
          case None => F.delay(None)
          case Some(user) =>
            for {
              f <- sql"""DELETE FROM searches WHERE username == '${x.username}'""".update.run.transact(xa)
              f <- sql"""DELETE FROM results WHERE username == '${x.username}'""".update.run.transact(xa)
              f <- sql"""DELETE FROM users WHERE username == '${x.username}'""".update.run.transact(xa)
            } yield( Some(user) )
        }

      }
      def clear: F[Unit] = F.delay {
        for{
          f <- sql"""DELETE FROM searches""".update.run.transact(xa)
          f <- sql"""DELETE FROM results""".update.run.transact(xa)
          f <- sql"""DELETE FROM users""".update.run.transact(xa)
        } yield()
      }

    }
    def main(args: Array[String]) = {
      import transactor._
      val repo = UserSearchRepository.impl[IO](xa)
      // println(sql"""SELECT * FROM users""".query[(String, String)].to[List].transact(xa).unsafeRunSync)
      // println(sql"""SELECT * FROM searches WHERE username = 'Ethan'"""
      // .query[(String, String)]
      // .to[List]
      // .transact(xa).unsafeRunSync)
      println(repo.getAll.unsafeRunSync)
      //println(repo.create(User("ethan", "yay", Vector())).unsafeRunSync)
      //println(repo.getAll.unsafeRunSync)
    }
}
