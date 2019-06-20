object AddCurryTest {
    def additionCurry(i: Int) =  (j: Int) => i + j //What is the benefit of doing this exactly?

    def ++ = additionCurry(1) //Increment
    def -- = additionCurry(-1) //Decrement

    //skeleton of this method given in doc
    def mapOverListRec(ints: List[Int], f: Int => Int): List[Int] = {
        @scala.annotation.tailrec
        def go(ints: List[Int], accum: List[Int]): List[Int] = ints match {
            case Nil => accum
            case head::tail => val accumTemp = accum :+ f(head)
            go(ints.tail, accumTemp)
        }
        val accum: List[Int] = List()
        go(ints, accum)
    }

    //mapOverList given in doc
    def mapOverList(ints: List[Int], f: Int => Int): List[Int] = {
        ints match {
            case Nil => ints
            case head :: tail => f(head) :: mapOverList(tail, f)
        }
    }

    //Testing the increment, decrement, and mapping
    def main(args: Array[String]) = {
        val firstAdd = additionCurry(3)
        val res = firstAdd(5)
        println(res) //8, successfully tested

        val shouldBe4 =  ++(3) 
        val shouldBe2 = --(3)
        println(shouldBe4) //4, successfully tested
        println(shouldBe2) //2, successfully tested

        val intList = List(1,2,3,4,5)
        val incRes = mapOverList(intList, ++)
        val decRes = mapOverList(intList, --)
        println(incRes) //(2,3,4,5,6)
        println(decRes) //(0,1,2,3,4)

        val incResRec = mapOverListRec(intList, ++)
        val decResRec = mapOverListRec(intList, --)
        println(incResRec) //(2,3,4,5,6)
        println(decResRec) //(0,1,2,3,4)
    }
}