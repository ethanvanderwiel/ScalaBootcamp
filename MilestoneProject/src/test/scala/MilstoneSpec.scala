import org.specs2.mutable.Specification

//Need to replace comments with what they should be. Guide isn't clear on what it means (xxx should {xxx in {...}})
object MilestoneSpec extends Specification {
    lazy val searchVector = Vector(
        ("Cats"),
        ("Terriers") ,    
        ("Sports"),
        ("activities"),
        ("Cities"),
        ("Emotions"),
        ("Seasons"),
        ("Examples"),
        ("People"),
        ("Agruments"),
        ("Paper"),
        ("Tired"),
        ("Music"),
        ("New"),
        ("Dancing"),
        ("Pens"),
        ("Love"),
        ("Old"),
        ("done"),
        ("finale")
    )
    lazy val userVector = Vector(
        User("user1", "userpass1", Milestone.makeSearch(Vector(searchVector(0), searchVector(1), searchVector(7)))),
        User("user2", "userpass2", Milestone.makeSearch(Vector(searchVector(1), searchVector(3), searchVector(2)))),
        User("user3", "userpass3", Milestone.makeSearch(Vector(searchVector(4), searchVector(7), searchVector(6)))),
        User("user4", "userpass4", Milestone.makeSearch(Vector(searchVector(7), searchVector(1), searchVector(7)))),
        User("user5", "userpass5", Milestone.makeSearch(Vector(searchVector(9), searchVector(10), searchVector(18)))),
        User("user6", "userpass6", Milestone.makeSearch(Vector(searchVector(19), searchVector(0), searchVector(14)))),
        User("user7", "userpass7", Milestone.makeSearch(Vector(searchVector(15), searchVector(16), searchVector(17)))),
        User("user8", "userpass8", Milestone.makeSearch(Vector(searchVector(5), searchVector(5), searchVector(8)))),
        User("user9", "userpass9", Milestone.makeSearch(Vector(searchVector(12), searchVector(12), searchVector(13)))),
        User("user10", "userpass10", Milestone.makeSearch(Vector(searchVector(7), searchVector(1), searchVector(3))))
    )
    "Most Frequent User Search Fold" should {
        "Find max user search term" in {
            Milestone.mostFrequentUserSearchFold(userVector(8)) must beEqualTo( ("Music", 2) )
            Milestone.mostFrequentUserSearchFold(userVector(3)) must beEqualTo( ("Examples", 2) )
        }
        
    }
    "Most Frequent search across all users" should {
        "find max search term" in {
            Milestone.mostCommonSearchAllUsersFold(userVector) must beEqualTo( ("Examples", 5) )
        }
    }
}