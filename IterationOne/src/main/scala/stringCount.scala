class stringCount {
    /*
    * Method that receives a list of strings and returns a list of ints, where each int 
    * corresponds to length of string. According to spec, null values return 0
    * and empty strings return -1
    */
    def count(strings: List[String]): List[Int] = {
        //for-yield loop that is assigned to returned int list
        val countList: List[Int] = for {
            string <- strings
        } yield (
            //To deal with the null case, needed to use option type
            Option(string) match {
                case None => 0
                //If string is not null, match lengths
                case _ => string match {
                    case "" => -1
                    case _ => string.length()
                }
            }
        )
        countList
    }
}

//Main Method for testing string counting and milestone collection
object StringMain {
    def main(args: Array[String]): Unit = { 
        val stringList = List("Hello", null, "wow", "", null, "This is the last one!")
        val testObj = new stringCount()
        println("Testing string counting activity: ")
        println(testObj.count(stringList)) //(5, 0, 3, -1, 0, 21)
    }
}

