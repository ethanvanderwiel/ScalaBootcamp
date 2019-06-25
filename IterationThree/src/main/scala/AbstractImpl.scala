object AbstractImpl {
    sealed abstract class RaceCar() {
        def driver: String
        def startEngine: String = s"${driver}'s Engine started! Ready to race" //I'm aware all of these defs should probably be vals. 
        def pitStop: String = s"${driver} is in the pit lane! Car: Up! Tires: Changed! Car: Down! Go!"
    }

    case class F1Car(driver: String) extends RaceCar {
        def drsStart = s"${driver} opens the hatch: going fast!"
    }

    case class IndyCar(driver: String) extends RaceCar {
        def refuel = s"${driver} is refueling...."
    }

    def main(args: Array[String]) {
        val Norris = F1Car("Lando Norris")
        val Rossi = IndyCar("Alex Rossi")

        println(Norris.startEngine) //Lando Norris's Engine started! Ready to race
        println(Rossi.startEngine) //Alex Rossi's Engine started! Ready to race
        println(Rossi.pitStop) //Alex Rossi is in the pit lane! Car: Up! Tires: Changed! Car: Down! Go!
        println(Norris.drsStart) //Lando Norris opens the hatch: going fast!
        println(Rossi.refuel) //Alex Rossi is refueling....
        
    }

}