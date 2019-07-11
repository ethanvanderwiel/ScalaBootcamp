import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterEach
import org.specs2.specification.BeforeEach

//Need to replace comments with what they should be. Guide isn't clear on what it means (xxx should {xxx in {...}})
object MilestoneSpec extends Specification with HttpClient {
    sequential
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
            Milestone.mostFrequentUserSearchFold(userVector(8)) must beEqualTo( Seq(("Music")) )
            Milestone.mostFrequentUserSearchFold(userVector(3)) must beEqualTo( Seq(("Examples")) )
        }

    }
    "Most Frequent search across all users" should {
        "find max search term" in {
            Milestone.mostCommonSearchAllUsersFold(userVector) must beEqualTo( Seq("Examples") )
        }
    }

    "UserSearchRepository" should {
        "clear" in {
            UserSearchRepository.clear
            UserSearchRepository.getAll must beEqualTo(Seq())
        }
        "create" in {
            UserSearchRepository.create(userVector(0)) must beEqualTo(Some(userVector(0)))
        }
        "get all" in {
            UserSearchRepository.clear
            UserSearchRepository.create(userVector(0))
            UserSearchRepository.create(userVector(1))
            UserSearchRepository.getAll must beEqualTo(Seq(userVector(0), userVector(1)))
        }
        "delete" in {
            UserSearchRepository.clear
            UserSearchRepository.create(userVector(0))
            UserSearchRepository.create(userVector(1))
            UserSearchRepository.delete(userVector(0))
            UserSearchRepository.getAll must beEqualTo(Seq(userVector(1)))
        }
        "delete nonexistent" in {
            UserSearchRepository.clear
            UserSearchRepository.create(userVector(0))
            UserSearchRepository.create(userVector(1))
            UserSearchRepository.delete(userVector(2)) must beEqualTo(None)
            UserSearchRepository.getAll must beEqualTo(Seq(userVector(0), userVector(1)))
        }
        "create existent" in {
            UserSearchRepository.clear
            UserSearchRepository.create(userVector(0))
            UserSearchRepository.create(userVector(1))
            UserSearchRepository.create(userVector(0))
            UserSearchRepository.getAll must beEqualTo(Seq(userVector(0), userVector(1)))
        }
    }
    "HttpServiceClient" should {
      "ping" in {
        val req = executeHttpGet("http://localhost:8000/ping")
        req.body must beEqualTo("Pong")
        req.statusCode must beEqualTo(200)
      }
      "create_user" in {
        UserSearchRepository.clear
        val newUser = Map("username"-> "user1", "password" -> "pass1")
        val req = executeHttpPost("http://localhost:8000/create_user", newUser)
        req.statusCode must beEqualTo(200)
        UserSearchRepository.getAll.headOption.getOrElse() must beEqualTo(User("user1", "pass1", Vector()))
      }
      "change_password" in {
        UserSearchRepository.clear
        val newUser = Map("username"-> "user1", "password" -> "pass1")
        val newUser2 = Map("username"-> "user2", "password" -> "pass2")
        executeHttpPost("http://localhost:8000/create_user", newUser)
        executeHttpPost("http://localhost:8000/create_user", newUser2)

        val changePass = Map("username" -> "user2", "oldPassword" -> "pass2", "newPassword" -> "newPass2")
        val req = executeHttpPost("http://localhost:8000/change_password", changePass)
        req.statusCode must beEqualTo(200)
        UserSearchRepository.get("user2") must beEqualTo(Some(User("user2", "newPass2", Vector())))
      }
      "search" in {
        UserSearchRepository.clear
        val newUser = Map("username"-> "user1", "password" -> "pass1")
        executeHttpPost("http://localhost:8000/create_user", newUser)

        val req = executeHttpPost("http://localhost:8000/search?q=cat", newUser)
        req.statusCode must beEqualTo(200)
        req.body must beEqualTo("""[{"name":"Cat","desc":" A small, typically furry, carnivorous mammal. They are often called house cats when kept as..."},{"name":"Cat Stevens","desc":"A British singer-songwriter and multi-instrumentalist."},{"name":"Computed axial tomography","desc":"A CT scan, also known as computed tomography scan, makes use of computer-processed combinations..."}]""")
      }
      "search_terms GET" in {
        UserSearchRepository.clear
        val newUser = Map("username"-> "user1", "password" -> "pass1")
        val newUser2 = Map("username"-> "user2", "password" -> "pass2")
        executeHttpPost("http://localhost:8000/create_user", newUser)
        executeHttpPost("http://localhost:8000/create_user", newUser2)

        executeHttpPost("http://localhost:8000/search?q=cat", newUser) //search string changed slightly to fit duck duck go and http4s standards
        executeHttpPost("http://localhost:8000/search?q=house", newUser)
        executeHttpPost("http://localhost:8000/search?q=bird", newUser2)
        val req = executeHttpGet("http://localhost:8000/search_terms")
        req.statusCode must beEqualTo(200)
        req.body must beEqualTo("""{"searches":[{"term":"cat"},{"term":"house"},{"term":"bird"}]}""")
      }
      "search_terms POST" in {
        UserSearchRepository.clear
        val newUser = Map("username"-> "user1", "password" -> "pass1")
        val newUser2 = Map("username"-> "user2", "password" -> "pass2")
        executeHttpPost("http://localhost:8000/create_user", newUser)
        executeHttpPost("http://localhost:8000/create_user", newUser2)

        executeHttpPost("http://localhost:8000/search?q=cat", newUser) //search string changed slightly to fit duck duck go and http4s standards
        executeHttpPost("http://localhost:8000/search?q=house", newUser)
        executeHttpPost("http://localhost:8000/search?q=bird", newUser2)
        val req = executeHttpPost("http://localhost:8000/search_terms", newUser)
        req.statusCode must beEqualTo(200)
        req.body must beEqualTo("""{"searches":[{"term":"cat"},{"term":"house"}]}""")
      }
      "most_common_search GET" in {
        UserSearchRepository.clear
        val newUser = Map("username"-> "user1", "password" -> "pass1")
        val newUser2 = Map("username"-> "user2", "password" -> "pass2")
        executeHttpPost("http://localhost:8000/create_user", newUser)
        executeHttpPost("http://localhost:8000/create_user", newUser2)

        executeHttpPost("http://localhost:8000/search?q=cat", newUser) //search string changed slightly to fit duck duck go and http4s standards
        executeHttpPost("http://localhost:8000/search?q=house", newUser)
        executeHttpPost("http://localhost:8000/search?q=bird", newUser2)
        executeHttpPost("http://localhost:8000/search?q=cat", newUser2)
        val req = executeHttpGet("http://localhost:8000/most_common_search")
        req.statusCode must beEqualTo(200)
        req.body must beEqualTo("""{"searches":[{"term":"cat"}]}""")
      }
      "multi most_common_search GET" in {
        UserSearchRepository.clear
        val newUser = Map("username"-> "user1", "password" -> "pass1")
        val newUser2 = Map("username"-> "user2", "password" -> "pass2")
        executeHttpPost("http://localhost:8000/create_user", newUser)
        executeHttpPost("http://localhost:8000/create_user", newUser2)

        executeHttpPost("http://localhost:8000/search?q=cat", newUser) //search string changed slightly to fit duck duck go and http4s standards
        executeHttpPost("http://localhost:8000/search?q=house", newUser)
        executeHttpPost("http://localhost:8000/search?q=bird", newUser2)
        executeHttpPost("http://localhost:8000/search?q=cat", newUser2)
        executeHttpPost("http://localhost:8000/search?q=house", newUser2)
        val req = executeHttpGet("http://localhost:8000/most_common_search")
        req.statusCode must beEqualTo(200)
        req.body must beEqualTo("""{"searches":[{"term":"cat"},{"term":"house"}]}""")
      }
      "most_common_search POST" in {
        UserSearchRepository.clear
        val newUser = Map("username"-> "user1", "password" -> "pass1")
        val newUser2 = Map("username"-> "user2", "password" -> "pass2")
        executeHttpPost("http://localhost:8000/create_user", newUser)
        executeHttpPost("http://localhost:8000/create_user", newUser2)

        executeHttpPost("http://localhost:8000/search?q=cat", newUser) //search string changed slightly to fit duck duck go and http4s standards
        executeHttpPost("http://localhost:8000/search?q=house", newUser)
        executeHttpPost("http://localhost:8000/search?q=house", newUser)
        executeHttpPost("http://localhost:8000/search?q=bird", newUser2)
        executeHttpPost("http://localhost:8000/search?q=cat", newUser2)
        executeHttpPost("http://localhost:8000/search?q=house", newUser2)

        val req = executeHttpPost("http://localhost:8000/most_common_search", newUser)
        req.statusCode must beEqualTo(200)
        req.body must beEqualTo("""{"searches":[{"term":"house"}]}""")
      }
    }

    "Circe Json Parse" should {
      "Write user data" in {
        UserSearchRepository.clear
        val jsonUser = CirceParse.encodeAllUsers(userVector)
        val decoded = CirceParse.decodeAllUsers
        decoded must beEqualTo(userVector)
      }
    }


}
