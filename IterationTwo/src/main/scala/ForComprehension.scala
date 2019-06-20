
object ForCompTest {
    def forComp(nums: List[List[Int]]): List[Int] = {
        for {
            numLst <- nums
            num <- numLst
            if(num > 0)
        } yield (num.toString.length)
    }
    def main(args: Array[String]) = {
        val nums = List(List(0, 10000, 22, 3093, 5), List (-1, -2, 400), List())
        println(nums.flatMap(lst => lst.filter(num => num > 0).map(number => number.toString.length))) //(5, 2, 4, 1, 3), given by doc
        println(ForCompTest.forComp(nums)) //(5, 2, 4, 1, 3), checked successfully
    }
}