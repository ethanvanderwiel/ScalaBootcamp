import org.specs2.mutable.Specification

//Need to replace comments with what they should be. Guide isn't clear on what it means (xxx should {xxx in {...}})
object MilestoneSpec extends Specification {
    "I don't know what this means" should {
        "respond" in {
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
                User("user1", "userpass1", Milestone.makeSearch(List(searchList(0), searchList(1), searchList(7)))),
                User("user2", "userpass2", Milestone.makeSearch(List(searchList(1), searchList(3), searchList(2)))),
                User("user3", "userpass3", Milestone.makeSearch(List(searchList(4), searchList(7), searchList(6)))),
                User("user4", "userpass4", Milestone.makeSearch(List(searchList(7), searchList(1), searchList(7)))),
                User("user5", "userpass5", Milestone.makeSearch(List(searchList(9), searchList(10), searchList(18)))),
                User("user6", "userpass6", Milestone.makeSearch(List(searchList(19), searchList(0), searchList(14)))),
                User("user7", "userpass7", Milestone.makeSearch(List(searchList(15), searchList(16), searchList(17)))),
                User("user8", "userpass8", Milestone.makeSearch(List(searchList(5), searchList(5), searchList(8)))),
                User("user9", "userpass9", Milestone.makeSearch(List(searchList(12), searchList(12), searchList(13)))),
                User("user10", "userpass10", Milestone.makeSearch(List(searchList(7), searchList(1), searchList(3))))
            )

            Milestone.mostFrequentUserSearchFold(userList(8)) must beEqualTo( ("Music", 2) )
            Milestone.mostFrequentUserSearchFold(userList(3)) must beEqualTo( ("Examples", 2) )
            Milestone.mostCommonSearchAllUsersFold(userList) must beEqualTo( ("Examples", 5) )
        }
    }
}