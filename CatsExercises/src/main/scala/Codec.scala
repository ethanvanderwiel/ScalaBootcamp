import cats._
import cats.syntax.eq
import cats.implicits._

import cats.Eq
import cats.syntax.option._

trait Codec[A] {
  def encode(value: A): String
  def decode(value: String): A
  def imap[B](dec: A => B, enc: B => A): Codec[B] = {
    val self = this
    new Codec[B] {
      def encode(value: B): String = self.encode(enc(value))
      def decode(value: String): B = dec(self.decode(value))
    }
  }
}

object Main {
  def test(i: Int): Int =
    i + 1
  def main(args: Array[String]) = {
    val hello: Int  = 4
    val lovely: Int = test(hello)
    println(lovely)
  }
}

// object Codec[Double] {

// }
// implicit val doubleCodec: Codec[Double] = stringCodec.imap[Double](_.toDouble, _.toString)
// def encode[A](value: A)(implicit c: Codec[A]): String = c.encode(value)
// def decode[A](value: String)(implicit c: Codec[A]): A = c.decode(value)
