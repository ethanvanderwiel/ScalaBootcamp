case class Result(name: String, desc: String)

case class Search(searchString: String, results: Vector[Result])

case class User(username: String, password: String, searches: Vector[Search])


object Milestone {


    /*
    * Finds the most frequent search within a user's search history. Uses foldLeft to create a new map that combines
    * already used keys and increments their value counter. Finally, converts the map to a Seq to be sorted and returns
    * the last key-value (highest value)
    */
    def mostFrequentUserSearchFold(user: User): Seq[String] = {
        val res = user.searches.foldLeft(Map.empty[String, Int])( (map, search) => map + (
            (map get search.searchString) match {
                case None => (search.searchString -> 1)
                case _ => (search.searchString -> ((map get search.searchString).get + 1))
            }
        ) )
        val most = res.toSeq.sortBy(_._2).apply(res.size - 1)._2
        val mostSeq = res.toSeq.filter(_._2 == most )
        mostSeq.map((tup: (String, Int)) => tup._1)
    }

    /*
    * Converts all users to a Vector that contains every search using flat map. Then, does the same as above
    *
    */
    def mostCommonSearchAllUsersFold(users: Vector[User]): Seq[String] = {
        val totalSearches = users flatMap (user => user.searches)
        val res = totalSearches.foldLeft(Map.empty[String, Int])( (map, search) => map + (
            (map get search.searchString) match {
                case None => (search.searchString -> 1)
                case _ => (search.searchString -> ((map get search.searchString).get + 1))
            }
        ) )
        val most = res.toSeq.sortBy(_._2).apply(res.size - 1)._2
        val mostSeq = res.toSeq.filter(_._2 == most )
        mostSeq.map((tup: (String, Int)) => tup._1)
    }



}
