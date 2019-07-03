


object HybridName {
  def apply(f: String, l: String): String = {
    f + "-" + l
  }
  def unapply(t: String): Option[(String, String)] = {
    val pattern = "(.*?)(-)(.*?)".r
    t match {
      case pattern(first, hyp, last) => Some((first, last))
      case _ => None
    }
  }
}

object HybridNameMain {
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

    val bool = HybridName.unapply(noOnesName).getOrElse(("empty", "empty")) match {
      case (_, "Vanderwiel") => true
      case ("Vanderwiel", _) => true
      case (_,_) => false
    }
    println(bool)
  }
}
