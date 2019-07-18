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
        User("user1", "userpass1", Vector()),
        User("user2", "userpass2", Vector()),
        User("user3", "userpass3", Vector()),
        User("user4", "userpass4", Vector()),
        User("user5", "userpass5", Vector()),
        User("user6", "userpass6", Vector()),
        User("user7", "userpass7", Vector()),
        User("user8", "userpass8", Vector()),
        User("user9", "userpass9", Vector()),
        User("user10", "userpass10", Vector())
    )

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

    "Circe Json Parse" should {
      "Write user data" in {
        UserSearchRepository.clear
        val jsonUser = CirceParse.encodeAllUsers(userVector)
        val decoded = CirceParse.decodeAllUsers
        decoded must beEqualTo(userVector)
      }
    }


}
