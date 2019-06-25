import org.specs2.mutable.Specification

object AbstractImplSpec extends Specification {
    "I don't know what this means" should {
        "respond" in {
            val Norris = RaceCarFactory("F1Car")
            val Rossi = RaceCarFactory("IndyCar")

            Norris.startEngine must beEqualTo("Lando Norris's Engine started! Ready to race")
            Rossi.startEngine must beEqualTo("Alex Rossi's Engine started! Ready to race") 
            Rossi.pitStop must beEqualTo("Alex Rossi is in the pit lane! Car: Up! Tires: Changed! Car: Down! Go!") 
            Norris.custom must beEqualTo("Lando Norris opens the hatch: going fast!") 
            Rossi.custom must beEqualTo("Alex Rossi is refueling....") 
            Norris must haveClass[F1Car]
            Rossi must haveClass[IndyCar]
        }
    }
}