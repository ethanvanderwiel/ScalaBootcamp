


object HybridName {
  def apply(first: String, last: String): String = {
    first + "-" + last
  }
  def unapply(hybrid: String): Option[(String, String)] = {
    val pattern = "(.*?)(-)(.*?)".r
    hybrid match {
      case pattern(first, hyp, last) => Some((first, last))
      case _ => None
    }
  }
}

object HybridNameMain {
  /* Said to create a main method, not test class */
  def main(args: Array[String]): Unit = {
    val hybrid = HybridName.apply("Hello", "World")
    val correct = HybridName.unapply(hybrid)
    val wrong = HybridName.unapply("fjdksls")
    println(hybrid)
    println(correct)
    println(wrong)

    val myName = "Ethan-Vanderwiel" //true
    val notMyName = "Willis-Knox" //false
    val noOnesName = "fjdslfs" //false


    val bool = noOnesName match {
      case HybridName("Vanderwiel", _) => true
      case HybridName(_, "Vanderwiel") => true
      case _ => false
    }
    println(bool)
  }
}
