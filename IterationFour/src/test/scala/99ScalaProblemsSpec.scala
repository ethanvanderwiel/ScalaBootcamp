import org.specs2.mutable.Specification

object MilestoneSpec extends Specification {
    "I don't know what this means" should {
        "respond" in {
            val concreteList = List(1,2,3,4)
            val shortList = List(1)
            val emptyList = List()

            Problems.last(concreteList) must beEqualTo(Some(4))
            Problems.last(emptyList) must beEqualTo(None)

            Problems.penultimate(concreteList) must beEqualTo(Some(3))
            Problems.penultimate(shortList) must beEqualTo(None) //undefined in problem document, I assume it should be none
            Problems.penultimate(emptyList) must beEqualTo(None)

            Problems.nth(1, concreteList) must beEqualTo(Some(2))
            Problems.nth(1, emptyList) must beEqualTo(None)
            Problems.nth(10, concreteList) must beEqualTo(None)
            Problems.nth(0, shortList) must beEqualTo(Some(1))

            Problems.length(concreteList) must beEqualTo(4)
            Problems.length(emptyList) must beEqualTo(0)
            Problems.length(shortList) must beEqualTo(1)

            Problems.reverse(concreteList) must beEqualTo(List(4,3,2,1))
            Problems.reverse(shortList) must beEqualTo(List(1))
            Problems.reverse(emptyList) must beEqualTo(List())

            Problems.flatten(List(List(1, 1), 2, List(3, List(5, 8)))) must beEqualTo(List(1, 1, 2, 3, 5, 8))

            Problems.compress(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)) must beEqualTo(List('a, 'b, 'c, 'a, 'd, 'e))

            Problems.pack(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)) must beEqualTo(List(List('a, 'a, 'a, 'a), List('b), List('c, 'c), List('a, 'a), List('d), List('e, 'e, 'e, 'e)))

            Problems.encode(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)) must beEqualTo(List((4,'a), (1,'b), (2,'c), (2,'a), (1,'d), (4,'e)))

            Problems.decode(List((4, 'a), (1, 'b), (2, 'c), (2, 'a), (1, 'd), (4, 'e))) must beEqualTo(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e))

            Problems.encodeDirect(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)) must beEqualTo(List((4,'a), (1,'b), (2,'c), (2,'a), (1,'d), (4,'e)))

            Problems.duplicate(List('a, 'b, 'c, 'c, 'd)) must beEqualTo(List('a, 'a, 'b, 'b, 'c, 'c, 'c, 'c, 'd, 'd))

            Problems.duplicateN(3, List('a, 'b, 'c, 'c, 'd)) must beEqualTo(List('a, 'a, 'a, 'b, 'b, 'b, 'c, 'c, 'c, 'c, 'c, 'c, 'd, 'd, 'd))

            Problems.split(3, List('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k)) must beEqualTo((List('a, 'b, 'c),List('d, 'e, 'f, 'g, 'h, 'i, 'j, 'k)))

            Problems.drop(3, List('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k)) must beEqualTo(List('a, 'b, 'd, 'e, 'g, 'h, 'j, 'k))

            Problems.slice(3, 7, List('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k)) must beEqualTo(List('d, 'e, 'f, 'g))
            
            Problems.rotate(3, List('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k)) must beEqualTo(List('d, 'e, 'f, 'g, 'h, 'i, 'j, 'k, 'a, 'b, 'c))
            Problems.rotate(22, List('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k)) must beEqualTo(List('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k))
            Problems.rotate(-22, List('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k)) must beEqualTo(List('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k))
            Problems.rotate(-2, List('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k)) must beEqualTo(List('j, 'k, 'a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i))

            Problems.range(4, 9) must beEqualTo(List(4, 5, 6, 7, 8, 9))
            Problems.removeAt(1, List('a, 'b, 'c, 'd)) must beEqualTo((List('a, 'c, 'd),'b))
            Problems.removeAt(0, List('a, 'b, 'c, 'd)) must beEqualTo((List('b, 'c, 'd),'a))

            Problems.insertAt('new, 1, List('a, 'b, 'c, 'd)) must beEqualTo(List('a, 'new, 'b, 'c, 'd))
            Problems.insertAt('new, 0, List('a, 'b, 'c, 'd)) must beEqualTo(List('new, 'a, 'b, 'c, 'd))

            Problems.randomSelect(3, List('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h)) must beEqualTo(List('f, 'a, 'd)) //Seeded, but usually random
            
            Problems.lotto(6, 49) must beEqualTo(List(46, 46, 29, 0, 6, 13)) //Seeded, but usually random

            Problems.randomPermute(List('a, 'b, 'c, 'd, 'e, 'f)) must beEqualTo(List('d, 'e, 'b, 'c, 'a, 'f)) //Seeded, but usually random
        }
    }
}