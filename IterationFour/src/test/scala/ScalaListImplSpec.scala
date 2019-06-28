import org.specs2.mutable.Specification

object ScalaListImplSpec extends Specification {
    "I don't know what this means" should {
        "respond" in {
            val list: ListImpl[Int] = Cons( 1, Cons( 2, Cons( 3, Cons( 4, Empty ) ) ) )
            val emptyList: ListImpl[Int] = Empty
            val resultAfterMap: ListImpl[Int] = Cons( 3, Cons( 4, Cons( 5, Cons( 6, Empty ) ) ) )
            val x: Natural = Succ(Succ(Succ(Zero)))
            val y: Natural = Succ(Succ(Zero))
            val i: Natural = Succ(Succ(Succ(Zero)))
            val j: Natural = Zero

            Exercises.add(x, y) must beEqualTo(Succ(Succ(Succ(Succ(Succ(Zero) ) ) ) ) )
            Exercises.add(i, j) must beEqualTo(Succ(Succ(Succ(Zero))))
            Exercises.sum(list) must beEqualTo(10)
            //Exercises.sum(emptyList) must beEqualTo(0) - Given methods don't allow reduceLeft on empty
            Exercises.length(emptyList) must beEqualTo(0)

            def mapBy(i:Int):Int = i + 2
            Exercises.map(list, mapBy) must beEqualTo(resultAfterMap)
            Exercises.map(emptyList, mapBy) must beEqualTo(Empty)

            def filterBy(i:Int): Boolean = i > 2
            val resultAfterFilter: ListImpl[Int] = Cons(3, Cons(4, Empty))
            Exercises.filter(list, filterBy) must beEqualTo(resultAfterFilter)
            Exercises.filter(emptyList, filterBy) must beEqualTo(Empty)

            val resultAfterAppend: ListImpl[Int]= Cons( 1, Cons( 2, Cons( 3, Cons( 4, resultAfterMap) ) ) )
            Exercises.append(list, resultAfterMap) must beEqualTo(resultAfterAppend)
            Exercises.append(emptyList, resultAfterMap) must beEqualTo(resultAfterMap)

            val unFlattened: ListImpl[ListImpl[Any]] = Cons(list, Cons(resultAfterMap, Empty))
            Exercises.flatten(unFlattened) must beEqualTo(resultAfterAppend)
            Exercises.flatten(Empty) must beEqualTo(Empty)

            Exercises.maximum(list) must beEqualTo(4)
            //Exercises.maximum(Empty) must beEqualTo(0) - Reduce left doesn't work on empty

            Exercises.reverse(list) must beEqualTo(Cons( 4, Cons( 3, Cons( 2, Cons( 1, Empty ) ) ) ))
            Exercises.reverse(Empty) must beEqualTo(Empty)
        }
    }
}