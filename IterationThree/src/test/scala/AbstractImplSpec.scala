import org.specs2.mutable.Specification

object AbstractImplSpec extends Specification {
    "I don't know what this means" should {
        "respond" in {
            val Norris = AbstractImpl.F1Car("Lando Norris")
            val Rossi = AbstractImpl.IndyCar("Alex Rossi")

            Norris.startEngine must beEqualTo("Lando Norris's Engine started! Ready to race")
            Rossi.startEngine must beEqualTo("Alex Rossi's Engine started! Ready to race") 
            Rossi.pitStop must beEqualTo("Alex Rossi is in the pit lane! Car: Up! Tires: Changed! Car: Down! Go!") 
            Norris.drsStart must beEqualTo("Lando Norris opens the hatch: going fast!") 
            Rossi.refuel must beEqualTo("Alex Rossi is refueling....") 
            Norris must haveClass[AbstractImpl.F1Car]
            Norris must haveClass[AbstractImpl.IndyCar]
        }
    }
}