import cats.implicits._
import cats.effect.Sync
import org.flywaydb.core.Flyway

object Migrations {
  def makeMigrations[F[_]: Sync](url: String, user: String, password: String): F[Unit] =
    Sync[F].delay {
      Flyway.configure
        .dataSource(url, user, password)
        .load
        .migrate
    }.void
}
