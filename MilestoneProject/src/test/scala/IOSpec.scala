import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterEach
import org.specs2.specification.BeforeEach

//Need to replace comments with what they should be. Guide isn't clear on what it means (xxx should {xxx in {...}})
object IOSpec extends Specification with HttpClient {
  sequential
  lazy val searchVector = Vector(
    ("Cats"),
    ("Terriers"),
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
  "HttpServiceClient" should {
    "ping" in {
      executeHttpGetIO("http://localhost:9000/ping").unsafeRunSync.body must beEqualTo("Pong")
    }
    "create_user" in {
      UserSearchRepository.clear
      val newUser = Map("username" -> "user1", "password" -> "pass1")
      executeHttpPostIO("http://localhost:9000/create_user", newUser).unsafeRunSync
      UserSearchRepository.getAll.headOption.getOrElse() must beEqualTo(User("user1", "pass1", Vector()))
    }
    "change_password" in {
      UserSearchRepository.clear
      val newUser  = Map("username" -> "user1", "password" -> "pass1")
      val newUser2 = Map("username" -> "user2", "password" -> "pass2")
      executeHttpPostIO("http://localhost:9000/create_user", newUser).unsafeRunSync
      executeHttpPostIO("http://localhost:9000/create_user", newUser2).unsafeRunSync

      val changePass = Map("username" -> "user2", "oldPassword" -> "pass2", "newPassword" -> "newPass2")
      val req        = executeHttpPostIO("http://localhost:9000/change_password", changePass).unsafeRunSync
      UserSearchRepository.get("user2") must beEqualTo(Some(User("user2", "newPass2", Vector())))
    }
    "search" in {
      UserSearchRepository.clear
      val newUser = Map("username" -> "user1", "password" -> "pass1")
      executeHttpPostIO("http://localhost:9000/create_user", newUser).unsafeRunSync

      executeHttpPostIO("http://localhost:9000/search?q=cat", newUser).unsafeRunSync.body must beEqualTo(
        """{"searchString":"cat","results":[{"name":"Cat","desc":" A small, typically furry, carnivorous mammal. They are often called house cats when kept as..."},{"name":"Cat Stevens","desc":"A British singer-songwriter and multi-instrumentalist."},{"name":"Computed axial tomography","desc":"A CT scan, also known as computed tomography scan, makes use of computer-processed combinations..."}]}"""
      )
    }
    "search_terms GET" in {
      UserSearchRepository.clear
      val newUser = Map("username"-> "user1", "password" -> "pass1")
      val newUser2 = Map("username"-> "user2", "password" -> "pass2")
      executeHttpPostIO("http://localhost:9000/create_user", newUser).unsafeRunSync
      executeHttpPostIO("http://localhost:9000/create_user", newUser2).unsafeRunSync

      executeHttpPostIO("http://localhost:9000/search?q=cat", newUser).unsafeRunSync //search string changed slightly to fit duck duck go and http4s standards
      executeHttpPostIO("http://localhost:9000/search?q=house", newUser).unsafeRunSync
      executeHttpPostIO("http://localhost:9000/search?q=bird", newUser2).unsafeRunSync
      executeHttpGetIO("http://localhost:9000/search_terms").unsafeRunSync.body must beEqualTo(
        """["cat","house","bird"]"""
        )
    }
      "search_terms POST" in {
        UserSearchRepository.clear
        val newUser = Map("username"-> "user1", "password" -> "pass1")
        val newUser2 = Map("username"-> "user2", "password" -> "pass2")
        executeHttpPostIO("http://localhost:9000/create_user", newUser).unsafeRunSync
        executeHttpPostIO("http://localhost:9000/create_user", newUser2).unsafeRunSync

        executeHttpPostIO("http://localhost:9000/search?q=cat", newUser).unsafeRunSync //search string changed slightly to fit duck duck go and http4s standards
        executeHttpPostIO("http://localhost:9000/search?q=house", newUser).unsafeRunSync
        executeHttpPostIO("http://localhost:9000/search?q=bird", newUser2).unsafeRunSync
        executeHttpPostIO("http://localhost:9000/search_terms", newUser).unsafeRunSync.body must beEqualTo(
          """["cat","house"]"""
          )
      }

      "most_common_search GET" in {
        UserSearchRepository.clear
        val newUser = Map("username"-> "user1", "password" -> "pass1")
        val newUser2 = Map("username"-> "user2", "password" -> "pass2")
        executeHttpPostIO("http://localhost:9000/create_user", newUser).unsafeRunSync
        executeHttpPostIO("http://localhost:9000/create_user", newUser2).unsafeRunSync

        executeHttpPostIO("http://localhost:9000/search?q=cat", newUser).unsafeRunSync //search string changed slightly to fit duck duck go and http4s standards
        executeHttpPostIO("http://localhost:9000/search?q=house", newUser).unsafeRunSync
        executeHttpPostIO("http://localhost:9000/search?q=bird", newUser2).unsafeRunSync
        executeHttpPostIO("http://localhost:9000/search?q=cat", newUser2).unsafeRunSync
        executeHttpGetIO("http://localhost:9000/most_common_search").unsafeRunSync.body must beEqualTo(
          """["cat"]"""
          )
      }
      "multi most_common_search GET" in {
        UserSearchRepository.clear
        val newUser = Map("username"-> "user1", "password" -> "pass1")
        val newUser2 = Map("username"-> "user2", "password" -> "pass2")
        executeHttpPostIO("http://localhost:9000/create_user", newUser).unsafeRunSync
        executeHttpPostIO("http://localhost:9000/create_user", newUser2).unsafeRunSync

        executeHttpPostIO("http://localhost:9000/search?q=cat", newUser).unsafeRunSync //search string changed slightly to fit duck duck go and http4s standards
        executeHttpPostIO("http://localhost:9000/search?q=house", newUser).unsafeRunSync
        executeHttpPostIO("http://localhost:9000/search?q=bird", newUser2).unsafeRunSync
        executeHttpPostIO("http://localhost:9000/search?q=cat", newUser2).unsafeRunSync
        executeHttpPostIO("http://localhost:9000/search?q=house", newUser2).unsafeRunSync
        executeHttpGetIO("http://localhost:9000/most_common_search").unsafeRunSync.body must beEqualTo(
          """["cat","house"]"""
          )
      }
      "most_common_search POST" in {
        UserSearchRepository.clear
        val newUser = Map("username"-> "user1", "password" -> "pass1")
        val newUser2 = Map("username"-> "user2", "password" -> "pass2")
        executeHttpPostIO("http://localhost:9000/create_user", newUser).unsafeRunSync
        executeHttpPostIO("http://localhost:9000/create_user", newUser2).unsafeRunSync

        executeHttpPostIO("http://localhost:9000/search?q=cat", newUser).unsafeRunSync //search string changed slightly to fit duck duck go and http4s standards
        executeHttpPostIO("http://localhost:9000/search?q=house", newUser).unsafeRunSync
        executeHttpPostIO("http://localhost:9000/search?q=house", newUser).unsafeRunSync
        executeHttpPostIO("http://localhost:9000/search?q=bird", newUser2).unsafeRunSync
        executeHttpPostIO("http://localhost:9000/search?q=cat", newUser2).unsafeRunSync
        executeHttpPostIO("http://localhost:9000/search?q=house", newUser2).unsafeRunSync

        executeHttpPostIO("http://localhost:9000/most_common_search", newUser).unsafeRunSync.body must beEqualTo(
          """["house"]""")

    }
  }

}
