import org.specs2.mutable.Specification


object AbstractImplSpec extends Specification {
    val Norris = RaceCarFactory("F1Car")
    val Rossi = RaceCarFactory("IndyCar")
    "Indycar" should {
        "construct class" in {
            Rossi must haveClass[IndyCar]
        }
        "startEngine" in {
            Rossi.startEngine must beEqualTo("Alex Rossi's Engine started! Ready to race") 
        }
        "pitStop" in {
            Rossi.pitStop must beEqualTo("Alex Rossi is in the pit lane! Car: Up! Tires: Changed! Car: Down! Go!") 
        }
        "custom" in {
            Rossi.custom must beEqualTo("Alex Rossi is refueling....")
        }
    }
    "F1Car" should {
        "construct class" in {
            Norris must haveClass[F1Car]
        }
        "startEngine" in {
             Norris.startEngine must beEqualTo("Lando Norris's Engine started! Ready to race")
        }
        "pitStop" in {
            Norris.pitStop must beEqualTo("Lando Norris is in the pit lane! Car: Up! Tires: Changed! Car: Down! Go!") 
        }
        "custom" in {
            Norris.custom must beEqualTo("Lando Norris opens the hatch: going fast!") 
        }
    }
}