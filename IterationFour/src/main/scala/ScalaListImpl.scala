sealed trait ListImpl[+A] {
  override def toString = {
    def toScalaList(t: ListImpl[A]): scala.List[A] = t match {
      case Empty => Nil
      case Cons(h, t) => h :: toScalaList(t)
    }
    toScalaList(this).toString
  }
}
final case object Empty extends ListImpl[Nothing]
final case class Cons[A](h: A, t: ListImpl[A]) extends ListImpl[A]

object ListImpl {
  def foldRight[A, B](as: ListImpl[A], b: B, f: (A, B) => B): B = as match {
    case Empty => b
    case Cons(h, t) => f(h, foldRight(t, b, f))
  }

  def foldLeft[A, B](as: ListImpl[A], b: B, f: (B, A) => B): B = as match {
    case Empty => b
    case Cons(h, t) => foldLeft(t, f(b, h), f)
  }

  def reduceRight[A](as: ListImpl[A], f: (A, A) => A): A = as match {
    case Empty => sys.error("oops")
    case Cons(h, t) => foldRight(t, h, f)
  }

  def reduceLeft[A](as: ListImpl[A], f: (A, A) => A): A = as match {
    case Empty => sys.error("oops")
    case Cons(h, t) => foldLeft(t, h, f)
  }

  def unfold[A, B](b: B, f: B => Option[(A, B)]): ListImpl[A] = f(b) match {
    case Some((a, b)) => Cons(a, unfold(b, f))
    case scala.None => Empty
  }
}

sealed trait Natural {
  override def toString = {
    def toInt(n: Natural): Int = n match {
      case Zero => 0
      case Succ(x) => 1 + toInt(x)
    }
    toInt(this).toString
  }
}
final case object Zero extends Natural
final case class Succ(c: Natural) extends Natural

//The part I have to implement
object Exercises {

    def add(x: Natural, y: Natural): Natural = (x: Natural, y: Natural) match {
      case (x, Zero) => x
      case (x, Succ(b)) => Succ(add(x, b))
    }

    def sum(is: ListImpl[Int]): Int = {
      ListImpl.reduceLeft(is, (i: Int, j: Int) => i + j)
    }

    def length[A](as: ListImpl[A]): Int = {
      ListImpl.foldLeft(as, 0, (tot: Int, i: Any) => tot + 1)
    }

    def map[A, B](as: ListImpl[A], f: A => B): ListImpl[B] = as match {
        case Empty => Empty
        case Cons(h, t) => Cons(f(h), map(t, f))
    }

    def filter[A](as: ListImpl[A], f: A => Boolean): ListImpl[A] = as match {
        case Empty => Empty
        case Cons(h, t)  => if (f(h)) Cons(h, filter(t, f)) else filter(t, f)
    }

    def append[A](x: ListImpl[A], y: ListImpl[A]): ListImpl[A] = x match {
        case Empty => y
        case Cons(h, t) => Cons(h, append(t, y))
    }

    def flatten[A](as: ListImpl[ListImpl[A]]): ListImpl[A] = as match {
        case Empty => Empty
        //case Cons(h , Empty) => h test for isn't needed
        case Cons(h, t) => append(h, flatten(t))
    }

    //According to the book, a flat map is the same as a map with a flatten after it. 
    //This compiles, but not sure yet how to test.
    def flatMap[A, B](as: ListImpl[A], f: A => ListImpl[B]): ListImpl[B] = {
        val mapped = map(as, f)
        flatten(mapped)
    }

    def maximum(is: ListImpl[Int]): Int = {
        def maxCompare(i: Int, j: Int): Int = {
            if(i > j) i
            else j
        }
        ListImpl.reduceLeft(is, maxCompare)
    }

    //Exact same as my implementation in 99ScalaProblems.
    //Change to use fold
    def reverse[A](as: ListImpl[A]): ListImpl[A] = {
        ListImpl.foldLeft(as, Empty, (t: ListImpl[A], h: A) => append(Cons(h, Empty), t))
    }

}