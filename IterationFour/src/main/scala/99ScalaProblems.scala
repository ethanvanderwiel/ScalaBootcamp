object Problems {
    //P01  - Changed from first review
    def last[A](list: List[A]): Option[A] = list match {
        case Nil => None
        case head::Nil => Option(head)
        case head::tail => last(tail)
    }
    //P02  - Changed from first review
    def penultimate[A](list: List[A]): Option[A] = list match {
       case Nil => None
       case head::(h::Nil) => Option(head)
       case head::tail => penultimate(tail)
    }
    //P03  - Changed from first review
    def nth[A](i: Int, list: List[A]): Option[A] = (i, list) match {
        case (0 , h::t) => Option(h)
        case (_ , h::t) => nth(i-1, t)
        case (_, Nil) => None
    }

    //P04  - Changed from first review
    def length[A](list: List[A]): Int = {
        list.foldLeft(0)((tot: Int, i: A) => tot + 1)
    }

    //P05  - Changed from first review
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

    //P08 - Changed from first review
    def compress[A](list: List[A]): List[A] = {
        list.foldLeft(List[A]())(
            (a, b) => {
                if(last(a).getOrElse() == b) a
                else a :+ b
            }
        )
    }

    //P09
    def pack[A](list: List[A]): List[List[A]] = {
        @scala.annotation.tailrec
        def go(list: List[A], accum: List[List[A]]): List[List[A]] = list match {
            case Nil => accum
            case head :: tail if accum.length != 0 && head == accum(accum.length - 1)(0) => 
                go(list.tail, accum.updated(accum.length - 1, accum(accum.length - 1) :+ head)) //this whole case has become a mess. I'm sure there's an incredibly simple solution
            case head::tail => val accumTemp = accum :+ List(head)
                                go(list.tail, accumTemp)
        }
        val accum: List[List[A]] = List()
        go(list, accum)
    }

    def encode[A](list: List[A]): List[(Int, A)] = {
        val packed = pack(list)
        val res = packed.map(x => (x.length, x(0)))  
        res      
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

    //P14  - Changed from first review
    def duplicate[A](list: List[A]): List[A] = {
       list.flatMap((s) => List(s, s))
    }

    //P15 - Assumes list.fill is valid in this exercise
    // - Changed from first review
    def duplicateN[A](i: Int, list: List[A]): List[A] = {
        list.flatMap((s) => List.fill(i)(s))
    }

    //P16
    def drop[A](i: Int, list: List[A]): List[A] = {
        for {
            keep <- (0 to list.length - 1).toList //Seems redundant, but I need the space keep is in
            if((keep + 1) % i != 0) //keep + 1 is included due to spec saying every i starts at element i, not 0
        } yield (list(keep))
    }

    //P17
    def split[A](i: Int, list: List[A]): (List[A], List[A]) = {
        (for {
            cut <- (0 to (i - 1)).toList
            if(cut < list.length)
        } yield ( list(cut) ),
        for {
            cut <- (i to (list.length - 1)).toList
            if(cut < list.length) 
        } yield ( list(cut) )
        )
    }

    //P18 - Same logic as drop but "drops" all values not within given list
    def slice[A](i: Int, k: Int,list: List[A]): List[A] = {
        for {
            keep <- (0 to list.length - 1).toList
            if(keep >= i && keep < k)
        } yield (list(keep))
    }

    //P19
    def rotate[A](i: Int, list: List[A]): List[A] = {
        for {
            pos <- (0 to list.length - 1).toList
            val rotation = i % list.length //If number is greater than list.length, it repeats itself. If it's negative, causes issues without this statement
        } yield (
            if(i >= 0)list((pos + rotation) % list.length)
            else list((pos + list.length + rotation) % list.length)
        )
    }

    //P20
    def removeAt[A](i: Int, list: List[A]): (List[A], A) = {
        (slice(0, i, list):::slice(i+1, list.length, list), list(i))
    }

    //P21
    def insertAt[A](a: A, i: Int, list: List[A]): List[A] = {
        (slice(0, i, list):+ a):::slice(i, list.length, list) 
    }

    //P22
    def range(i: Int, j: Int): List[Int] = {
        def go(i: Int, j: Int, accum: List[Int]): List[Int] = {
            if( i == j ) accum :+ j
            else i +: accum ::: go(i + 1, j, accum)
        }
        val accum: List[Int] = List()
        go(i, j, accum)
    }

    //P23 - For later exercises to use this, I put the constraint of no repeats. This is why I needed the recursive call
    // - Changed from first review
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

    //P24  - Changed from first review
    def lotto(n:Int, m:Int): List[Int] = {
        val rand = new scala.util.Random(1)
        (1 to n).toList.map(i => rand.nextInt(m))
    }

    //P25
    def randomPermute[A](list: List[A]): List[A] = {
        randomSelect(list.length, list)
    }

    

}

