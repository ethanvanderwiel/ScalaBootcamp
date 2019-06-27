import org.specs2.mutable.Specification

object ScalaListImplSpec extends Specification {
    "I don't know what this means" should {
        "respond" in {
            val list: ListImpl[Int] = Cons( 1, Cons( 2, Cons( 3, Cons( 4, Empty ) ) ) )
            val resultAfterMap: ListImpl[Int] = Cons( 3, Cons( 4, Cons( 5, Cons( 6, Empty ) ) ) )
            val x: Natural = Succ(Succ(Succ(Zero)))
            val y: Natural = Succ(Succ(Zero))

            Exercises.add(x, y) must beEqualTo(Succ(Succ(Succ(Succ(Succ(Zero) ) ) ) ) )
            Exercises.sum(list) must beEqualTo(10)
            Exercises.length(list) must beEqualTo(4)

            def mapBy(i:Int):Int = i + 2
            Exercises.map(list, mapBy) must beEqualTo(resultAfterMap)

            def filterBy(i:Int): Boolean = i > 2
            val resultAfterFilter: ListImpl[Int] = Cons(3, Cons(4, Empty))
            Exercises.filter(list, filterBy) must beEqualTo(resultAfterFilter)

            val resultAfterAppend: ListImpl[Int]= Cons( 1, Cons( 2, Cons( 3, Cons( 4, resultAfterMap) ) ) )
            Exercises.append(list, resultAfterMap) must beEqualTo(resultAfterAppend)

            val unFlattened: ListImpl[ListImpl[Any]] = Cons(list, Cons(resultAfterMap, Empty))
            Exercises.flatten(unFlattened) must beEqualTo(resultAfterAppend)

            Exercises.maximum(list) must beEqualTo(4)

            Exercises.reverse(list) must beEqualTo(Cons( 4, Cons( 3, Cons( 2, Cons( 1, Empty ) ) ) ))
        }
    }
}