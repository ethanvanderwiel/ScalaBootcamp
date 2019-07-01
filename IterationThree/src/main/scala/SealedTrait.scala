object SealedTraits {
    sealed trait Values

    case class Numbers(i: Int) extends Values
    case class Strings(s: String) extends Values
    case class UnaryFunction(f: Any => Any) extends Values

    def interpret(v: Values): String = {
        v match {
            case x: Numbers if x.i < 10 => s"This is a number less than 10: ${x.i}"
            case x: Numbers => s"This is a number 10 or greater: ${x.i}"
            case x: Strings => s"This is a string: ${x.s}"
            case x: UnaryFunction => s"This is a unary function: ${x.f}" //Unsure if there is a way to see function body
            case _ => s"Uh oh, this is an unexpected result: ${v}"
        }
    }

    def main(args: Array[String]) = {
        val n1 = Numbers(3)
        val n2 = Numbers(12)
        
        val s1 = Strings("Hello World!")
        val s2 = Strings("Testing, testing, testing...")

        def +(b: Any):Any = b
        val u1 = UnaryFunction(+) 

        //val ux = 3
        

        val col = List(n1, n2, s1, s2, u1)
        col foreach { x => println(interpret(x)) }
    }
}
