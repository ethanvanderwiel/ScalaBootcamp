import scala.language.higherKinds
import cats.Monad
import cats.instances.option._
import cats.instances.list._
import cats.Id
import cats.Eval

//Custom implementation of a Monad
trait MonadEx[F[_]] {
  def pure[A](a: A): F[A]

  def flatMap[A, B](value: F[A])(func: A => F[B]): F[B]

  def map[A, B](value: F[A])(func: A => B): F[B] =
    flatMap(value)(a => pure(func(a)))
}

object IdImpl {
  def pure[A](value: A): Id[A] = value

  def map[A, B](initial: Id[A])(func: A => B): Id[B] = func(initial)

  def flatMap[A, B](initial: Id[A])(func: A => B): Id[B] = func(initial)
}

object MainMonad {

  def foldRightEval[A, B](as: List[A], acc: Eval[B])(fn: (A, Eval[B]) => Eval[B]): Eval[B] =
    as match {
      case head :: tail =>
        Eval.defer(fn(head, foldRightEval(tail, acc)(fn)))
      case Nil =>
        acc
    }

  def foldRight[A, B](as: List[A], acc: B)(fn: (A, B) => B): B =
    foldRightEval(as, Eval.now(acc)) { (a, b) =>
      b.map(fn(a, _))
    }.value

  def main(args: Array[String]) = {}
}
