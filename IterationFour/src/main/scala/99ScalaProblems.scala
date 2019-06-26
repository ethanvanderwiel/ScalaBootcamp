object Problems {
    //P01
    def last(list: List[Any]): Option[Any] = {
        list.length match {
            case 0 => None
            case l => Option(list(l - 1))
        }
    }
    //P02
    def penultimate(list: List[Any]): Option[Any] = {
        list.length match {
            case 0 | 1 => None
            case x => Some(list(x - 2))
        }
    }
    //P03
    def nth(i: Int, list: List[Any]): Option[Any] = {
        list.length match {
            case 0 => None
            case x if i < x => Some(list(i))
            case _ => None
        }
    }

    //P04
    def length(list: List[Any]): Int = {
        def go(list: List[Any], i: Int):Int = list match {
            case Nil => 0
            case head::tail => 1 + go(tail, i)
        }
        val length = go(list, 0)
        length
    }

    //P05, basically just did the mapping recursively I made in additionCurry.scala (iteration 2) but with no mapping
    def reverse(list: List[Any]): List[Any] = {
         @scala.annotation.tailrec
        def go(list: List[Any], accum: List[Any]): List[Any] = list match {
            case Nil => accum
            case head::tail => val accumTemp =  head +: accum
                                go(list.tail, accumTemp)
        }
        val accum: List[Any] = List()
        go(list, accum)
    }

    //P06
    def isPalindrome(list: List[Any]): Boolean = {
        reverse(list) == list
    }

    //P07
    def flatten(list: List[Any]): List[Any] = list match {
        case Nil => list
        case (x: List[_])::tail => flatten(x) ::: flatten(tail)
        case (x: Any)::tail => x :: flatten(tail)
        case _ :: tail => flatten(tail)
    }

    //P08 - Based on the code I wrote for mapping recursively 
    def compress(list: List[Any]): List[Any] = {
        @scala.annotation.tailrec
        def go(list: List[Any], accum: List[Any]): List[Any] = list match {
            case Nil => accum
            case head :: tail if accum.length != 0 && head == accum(accum.length - 1) => go(list.tail, accum)
            case head::tail => val accumTemp = accum :+ head
                                go(list.tail, accumTemp)
        }
        val accum: List[Any] = List()
        go(list, accum)
    }

    //P09
    def pack(list: List[Any]): List[List[Any]] = {
        @scala.annotation.tailrec
        def go(list: List[Any], accum: List[List[Any]]): List[List[Any]] = list match {
            case Nil => accum
            case head :: tail if accum.length != 0 && head == accum(accum.length - 1)(0) => 
                go(list.tail, accum.updated(accum.length - 1, accum(accum.length - 1) :+ head)) //this whole case has become a mess. I'm sure there's an incredibly simple solution
            case head::tail => val accumTemp = accum :+ List(head)
                                go(list.tail, accumTemp)
        }
        val accum: List[List[Any]] = List()
        go(list, accum)
    }

    def encode(list: List[Any]): List[(Int, Any)] = {
        val packed = pack(list)
        val res = packed.map(x => (x.length, x(0)))  
        res      
    }

    //P12 - Looks like it wants me to skip P11?
    def decode(list: List[(Int, Any)]): List[Any] = {
        for {
            tup <- list
            i <- 1 to tup._1
        } yield (
            tup._2
        )
    }

    //P13 - This is starting to become near unreadable, keeps building off the last one
    def encodeDirect(list: List[Any]): List[(Int, Any)] = {
        @scala.annotation.tailrec
        def go(list: List[Any], accum: List[(Int, Any)]): List[(Int, Any)] = list match {
            case Nil => accum
            case head :: tail if accum.length != 0 && head == accum(accum.length - 1)._2 => 
                go(list.tail, accum.updated(accum.length - 1, (accum(accum.length - 1)._1 + 1, accum(accum.length-1)._2))) 
            case head::tail => val accumTemp = accum :+ (1, head)
                                go(list.tail, accumTemp)
        }
        val accum: List[(Int, Any)] = List()
        go(list, accum)
    }

    //P14
    def duplicate(list: List[Any]): List[Any] = {
        val encodedList = encode(list)
        val doubleEncoded = encodedList.map(x => (x._1 * 2, x._2))
        val doubleDecoded = decode(doubleEncoded)
        doubleDecoded
    }

    //P15 - Same as P14 but with 2 changed to i
    def duplicateN(i: Int, list: List[Any]): List[Any] = {
        val encodedList = encode(list)
        val doubleEncoded = encodedList.map(x => (x._1 * i, x._2))
        val doubleDecoded = decode(doubleEncoded)
        doubleDecoded
    }

    //P16
    def drop(i: Int, list: List[Any]): List[Any] = {
        for {
            keep <- (0 to list.length - 1).toList //Seems redundant, but I need the space keep is in
            if((keep + 1) % i != 0) //keep + 1 is included due to spec saying every i starts at element i, not 0
        } yield (list(keep))
    }

    //P17
    def split(i: Int, list: List[Any]): (List[Any], List[Any]) = {
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
    def slice(i: Int, k: Int,list: List[Any]): List[Any] = {
        for {
            keep <- (0 to list.length - 1).toList
            if(keep >= i && keep < k)
        } yield (list(keep))
    }

    //P19
    def rotate(i: Int, list: List[Any]): List[Any] = {
        for {
            pos <- (0 to list.length - 1).toList
            val rotation = i % list.length //If number is greater than list.length, it repeats itself. If it's negative, causes issues without this statement
        } yield (
            if(i >= 0)list((pos + rotation) % list.length)
            else list((pos + list.length + rotation) % list.length)
        )
    }

    //P20
    def removeAt(i: Int, list: List[Any]): (List[Any], Any) = {
        (slice(0, i, list):::slice(i+1, list.length, list), list(i))
    }

    //P21
    def insertAt(a: Any, i: Int, list: List[Any]): List[Any] = {
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
    // With repeats, it is just a simple for comp.
    def randomSelect(i: Int, list: List[Any]): List[Any] = {
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
        for {
            value <- randomList
        } yield (list(value))
    }

    //P24
    def lotto(n:Int, m:Int): List[Any] = {
        val rand = new scala.util.Random(1)
        for {
            sel <- (1 to n).toList
        } yield (rand.nextInt(m))
    }

    //P25
    def randomPermute(list: List[Any]): List[Any] = {
        randomSelect(list.length, list)
    }

    

}

