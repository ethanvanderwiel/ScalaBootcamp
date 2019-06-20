case class Result(name: String, desc: String)

case class Search(searchString: String, results: List[Result])

case class User(username: String, password: String, searches: List[Search])

object MilestoneMain {

    /*
    * Finds the most frequent search of an individual user. For each search, 
    * if the key exists in a map, update value, otherwise add map element with v set to 1
    * Finally, sorts the map by value and returns the highest valued search (last tuple of list).
    * I chose to return the value as a tuple, but could be returned as string or Search object
    */
    def mostFrequentUserSearch(user: User): (String, Int) = {
        var SearchMap = Map[String, Int]()
        for {
            search <- user.searches
        } yield ( //I don't understand why I need yield here
            (SearchMap get search.searchString) match {
                case None => SearchMap += (search.searchString -> 1)
                case _ => SearchMap += (search.searchString -> ((SearchMap get search.searchString).get + 1))
            }
        )      
        SearchMap.toSeq.sortBy(_._2).apply(SearchMap.size - 1)  
    }

    /*
    * Finds the most frequent search among all users. Almost identical
    * in terms of algorthm to the mostFrequentUserSearch method, but uses
    * nested for to search through all users. Probably a way to consolidate this, 
    * but I haven't thought of it
    */
    def mostCommonSearchAllUsers(users: List[User]): (String, Int)= {
        var SearchMap = Map[String, Int]()
        for {
            user <- users
            search <- user.searches
        } yield ( //I don't understand why I need yield here
            (SearchMap get search.searchString) match {
                case None => SearchMap += (search.searchString -> 1)
                case _ => SearchMap += (search.searchString -> ((SearchMap get search.searchString).get + 1))
            }
        )      
        SearchMap.toSeq.sortBy(_._2).apply(SearchMap.size - 1)
    }

    /*
    * Method made purely for fun/setting up the testing quicker. Doc wanted 10 users, which would take forever to make by hand
    * using the old method
    */
    def makeSearch(searches: List[(String, List[(String, String)])]): List[Search] = {
        for {
            search <- searches
        } yield (Search(search._1, 
        List(Result((search._2)(0)._1,(search._2)(0)._2), 
             Result((search._2)(1)._1,(search._2)(1)._2))))
    }

    //Testing the collections
    def main(args: Array[String]): Unit = { 
       
        val searchList = List(
        ("Big Cats", List(("Tiger", "A fearsome cat"), ("Lion", "King of the jungle"))),
        ("Terriers", List(("Bull Terrier", "A terrier bull mix.... I think"), ("English Terrier", "A terrier, but with an accent"))) ,    
        ("Sports", List(("Tennis", "A sport with a yellow ball"), ("Soccer", "A sport with a white ball"))),
        ("Outdoors activities", List(("Hiking", "Climbing a mountain for... fun?"), ("Biking", "It's like walking but faster"))),
        ("Ancient Cities", List(("Athens", "Home of failure and cool plays"), ("Troy", "All we do is war"))),
        ("Emotions", List(("Happy", "I'm glad!"), ("Sad", "oof"))),
        ("Seasons", List(("Winter", "Why does this one exist tbh"), ("Summer", "Too hot"))),
        ("Examples", List(("Example1", "Lazy"), ("ExampleB", "No patter I guess"))),
        ("People", List(("User1", "When you can't even be creative"), ("Scott Pilgrim", "He fights for love, and himself!"))),
        ("Agruments", List(("Sass", "I just think its funny that..."), ("Jokes", "This isn't an argument, I promise"))),
        ("Paper", List(("Thin", "Ink hates this!"), ("Thicc", "That's more like it"))),
        ("Tired", List(("You", "Current State"), ("Who reads this", "No one"))),
        ("Music", List(("Jazz", "babitty boo baht"), ("Country", "Badditty bad bad"))),
        ("...", List(("?", "Who knows"), ("404", "Error"))),
        ("Dancing", List(("Korean", "A fantastical dance style, and person from korea!"), ("Square", "None of those things"))),
        ("Pens", List(("Pilot", "Flies you places"), ("Lamy", "Not lame, I promise"))),
        ("Love", List(("Cute", "Awwwww"), ("Hearts", "Body part"))),
        ("So many examples", List(("Another one", "DJ Khalid joke"), ("Second one!", "If you're not first..."))),
        ("Almost done", List(("Why did they want 10 users", "This'll break everything"), ("Can't be", "true"))),
        ("The finale", List(("Last", "End of the line"), ("Its done", "hopefully"))))

        val userList = List(
            User("user1", "userpass1", makeSearch(List(searchList(0), searchList(1), searchList(7)))),
            User("user2", "userpass2", makeSearch(List(searchList(1), searchList(3), searchList(2)))),
            User("user3", "userpass3", makeSearch(List(searchList(4), searchList(7), searchList(6)))),
            User("user4", "userpass4", makeSearch(List(searchList(7), searchList(1), searchList(7)))),
            User("user5", "userpass5", makeSearch(List(searchList(9), searchList(10), searchList(18)))),
            User("user6", "userpass6", makeSearch(List(searchList(19), searchList(0), searchList(14)))),
            User("user7", "userpass7", makeSearch(List(searchList(15), searchList(16), searchList(17)))),
            User("user8", "userpass8", makeSearch(List(searchList(5), searchList(5), searchList(8)))),
            User("user9", "userpass9", makeSearch(List(searchList(12), searchList(12), searchList(13)))),
            User("user10", "userpass10", makeSearch(List(searchList(7), searchList(1), searchList(3))))
        )
        for {
            user <- userList.take(3) //iteration 1 testing, successfully tested
        } yield (println(user + "\n"))

        
        println(MilestoneMain.mostFrequentUserSearch(userList(8)))  //iteration 2 testing: (Music, 2), successfully tested
        println(MilestoneMain.mostCommonSearchAllUsers(userList))  //iteration 2 testing: (Examples, 5), successfully tested   
    }
}