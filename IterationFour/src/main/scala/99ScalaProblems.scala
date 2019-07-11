object Problems {
    //P01
    def last[A](list: List[A]): Option[A] = list match {
        case Nil => None
        case head::Nil => Option(head)
        case head::tail => last(tail)
    }
    //P02
    def penultimate[A](list: List[A]): Option[A] = list match {
       case Nil => None
       case head::(h::Nil) => Option(head)
       case head::tail => penultimate(tail)
    }
    //P03
    def nth[A](i: Int, list: List[A]): Option[A] = (i, list) match {
        case (0 , h::t) => Option(h)
        case (_ , h::t) => nth(i-1, t)
        case (_ , Nil) => None
    }

    //P04
    def length[A](list: List[A]): Int = {
        list.foldLeft(0)((tot: Int, i: A) => tot + 1)
    }

    //P05
    def reverse[A](list: List[A]): List[A] = {
        list.foldLeft(List[A]())((h,t) => t::h)
    }

    //P06
    def isPalindrome[A](list: List[A]): Boolean = {
        reverse(list) == list
    }

    //P07
    def flatten[A](list: List[A]): List[A] = list match {
        case Nil => list
        case (x: List[A])::tail => flatten(x) ::: flatten(tail)
        case x::tail => x :: flatten(tail)
    }

    //P08 - Changed from the second review
    def compress[A](list: List[A]): List[A] = list match {
        case Nil => Nil
        case h :: t => h :: compress(list.dropWhile(_ == h))
    }

    //P09 - Changed from the second review
    def pack[A](list: List[A]): List[List[A]] = list match {
        case Nil => Nil
        case h::t => val (acc, rest) = list.span(_ == h)
          rest match {
            case Nil => List(acc)
            case x => acc :: pack(x)
          }
    }

    //P10 - Changed from the second review
    def encode[A](list: List[A]): List[(Int, A)] = {
        pack(list).map(x => (x.length, x(0)))
    }

    //P12 - Looks like it wants me to skip P11?
    def decode[A](list: List[(Int, A)]): List[A] = {
        for {
            tup <- list
            i <- 1 to tup._1
        } yield (
            tup._2
        )
    }

    //P13 - This is starting to become near unreadable, keeps building off the last one
    def encodeDirect[A](list: List[A]): List[(Int, A)] = {
        @scala.annotation.tailrec
        def go(list: List[A], accum: List[(Int, A)]): List[(Int, A)] = list match {
            case Nil => accum
            case head :: tail if accum.length != 0 && head == accum(accum.length - 1)._2 =>
                go(list.tail, accum.updated(accum.length - 1, (accum(accum.length - 1)._1 + 1, accum(accum.length-1)._2)))
            case head::tail => val accumTemp = accum :+ (1, head)
                                go(list.tail, accumTemp)
        }
        val accum: List[(Int, A)] = List()
        go(list, accum)
    }

    //P14
    def duplicate[A](list: List[A]): List[A] = {
       list.flatMap((s) => List(s, s))
    }

    //P15 - Assumes list.fill is valid in this exercise
    def duplicateN[A](i: Int, list: List[A]): List[A] = {
        list.flatMap((s) => List.fill(i)(s))
    }

    //P16 - changed after second review
    def drop[A](i: Int, list: List[A]): List[A] =  {
      def rec[A](current: Int, list: List[A]): List[A] = (current, list) match {
        case (_, Nil) => Nil
        case (1, h::t) => rec(i, t)
        case (_, h::t) => h :: rec(current - 1, t)
      }
      rec(i, list)
    }

    //P17 - changed after second review
    def split[A](i: Int, list: List[A]): (List[A], List[A]) = (i, list) match {
      case (_, Nil) => (Nil, Nil)
      case (0, h::t) => (Nil, h::t)
      case (_, h::t) => val res = split(i - 1, t)
        (h::res._1, res._2)

    }

    //P18 - Changed from second review
    def slice[A](i: Int, k: Int,list: List[A]): List[A] = (i, k, list) match {
        case (_, _, Nil) => Nil
        case (0, 0, _) => Nil
        case (0, y, h::t) => h+:slice(0, y-1, t)
        case (x, y, h::t) => slice(x-1, y-1, t)
    }

    //P19 - Changed from second review
    def rotate[A](i: Int, list: List[A]): List[A] = (i, list) match {
       case (_, Nil) => Nil
       case (x, list) if (x % length(list) < 0) => rotate(length(list) + x, list)
       case (x, list) => split(x, list)._2 ::: split(x, list)._1
    }

    //P20
    def removeAt[A](i: Int, list: List[A]): (List[A], A) = {
        (slice(0, i, list):::slice(i+1, list.length, list), list(i))
    }

    //P21
    def insertAt[A](a: A, i: Int, list: List[A]): List[A] = {
        (slice(0, i, list):+ a):::slice(i, list.length, list)
    }

    //P22 - Changed from second review
    def range(i: Int, j: Int): List[Int] = (i, j) match {
      case (i, j) if i < j => Nil
      case (i, j) => i :: range(i + 1, j)
    }

    //P23 - For later exercises to use this, I put the constraint of no repeats. This is why I needed the recursive call
    def randomSelect[A](i: Int, list: List[A]): List[A] = {
        val rand = new scala.util.Random(1)
        @scala.annotation.tailrec
        def getRandom(i: Int, listLength: Int, accum: List[Int], rand: scala.util.Random): List[Int] = accum.length match {
            case `i` => accum
            case _ => {
                rand.nextInt(listLength) match {
                    case x if accum.contains(x) => getRandom(i, listLength, accum, rand)
                    case x => getRandom(i, listLength, accum :+ x, rand)
                }
            }
        }
        val randomList = getRandom(i, list.length, List(), rand)
        randomList.map(v => nth(v, list).get)
    }

    //P24
    def lotto(n:Int, m:Int): List[Int] = {
        val rand = new scala.util.Random(1)
        (1 to n).toList.map(i => rand.nextInt(m))
    }

    //P25
    def randomPermute[A](list: List[A]): List[A] = {
        randomSelect(list.length, list)
    }



}

