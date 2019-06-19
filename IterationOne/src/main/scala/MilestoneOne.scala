case class Result(name: String, desc: String)

case class Search(searchString: String, results: List[Result])

case class User(username: String, password: String, searches: List[Search])

object MilestoneMain {
    //Testing the collections
    def main(args: Array[String]): Unit = { 
        val resTiger = Result("Tiger", "A fearsome cat")
        val resLion = Result("Lion", "King of the jungle")
        val catList = List(resTiger, resLion)
        val catSearch = Search("Big Cats", catList)

        val resBTerrier = Result("Bull Terrier", "A terrier bull mix.... I think")
        val resETerrier = Result("English Terrier", "A terrier, but with an accent")
        val terrList = List(resBTerrier, resETerrier)
        val terrSearch = Search("Terriers", terrList)

        val animalUser = User("AnimalLover", "dogsncats", List(terrSearch, catSearch))

        val resTennis = Result("Tennis", "A sport with a yellow ball")
        val resSoccer = Result("Soccer", "A sport with a white ball")
        val sportList = List(resTennis, resSoccer)
        val sportSearch = Search("Sports", sportList)

        val resHiking = Result("Hiking", "Climbing a mountain for... fun?")
        val resBiking = Result("Biking", "It's like walking but faster")
        val outList = List(resHiking, resBiking)
        val outSearch = Search("Outdoors activities", outList)

        val activityUser = User("OutsideIsFun", "bikingnhiking", List(sportSearch, outSearch))
        val userCollection = List(animalUser, activityUser)
        for {
            user <- userCollection
        } yield (println(user + "\n"))
    }
}