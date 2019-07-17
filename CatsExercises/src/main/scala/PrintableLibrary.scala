import cats._
import cats.syntax.eq
import cats.implicits._

import cats.Eq
import cats.syntax.option._

final case class Cat(name: String, age: Int, color: String)
final case class Box[A](value: A)

trait Printable[A] {
  self =>

  def format(value: A): String

  def contramap[B](func: B => A): Printable[B] =
    new Printable[B] {
      def format(value: B): String = self.format(func(value))
    }
}

object Cat {
  implicit val catShow = Show.show[Cat] { cat =>
    s"$cat.name is a $cat.age year-old $cat.color cat."
  }

  import cats.instances.int._    // for Eq
  import cats.instances.string._ // for Eq
  import cats.syntax.eq._

  implicit val catEq: Eq[Cat] =
    Eq.instance[Cat] { (cat1, cat2) =>
      {
        (cat1.name === cat2.name) &&
        (cat1.age === cat2.age) &&
        (cat1.color === cat2.color)
      }
    }
}

// object Cat {
//   import PrintableInstances._
//   implicit val catPrintable = new Printable[Cat] {
//     def format(cat: Cat) = {
//       s"$cat.name is a $cat.age year-old $cat.color cat."
//     }
//   }
// }

object PrintableInstances {
  implicit val intPrintable = new Printable[Int] {
    def format(value: Int): String = value.toString
  }
  implicit val stringPrintable = new Printable[String] {
    def format(value: String): String = "\"" + value + "\""
  }
  implicit val booleanPrintable = new Printable[Boolean] {
    def format(value: Boolean): String = if (value) "yes" else "no"
  }
  implicit def boxPrintable[A](implicit p: Printable[A]) =
    p.contramap[Box[A]](_.value)
}

object Printable {
  def format[A](value: A)(implicit print: Printable[A]): String =
    print.format(value)
  def print[A](value: A)(implicit print: Printable[A]): Unit =
    println(format(value))
}

object PrintableSyntax {
  implicit class PrintableOps[A](value: A) {
    def format(implicit p: Printable[A]): String =
      p.format(value)
    def print(implicit p: Printable[A]): Unit =
      println(format(p))
  }
}
