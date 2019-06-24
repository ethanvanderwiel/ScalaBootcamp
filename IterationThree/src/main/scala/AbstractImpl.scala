object AbstractImpl {
    sealed abstract class RaceCar() {
        def driver: String
        def startEngine = println(s"${driver}'s Engine started! Ready to race")
        def pitStop = println(s"${driver} is in the pit lane! Car: Up! Tires: Changed! Car: Down! Go!")
    }

    case class F1Car(driver: String) extends RaceCar {
        def drsStart = println(s"${driver} opens the hatch: going fast!")
    }

    case class IndyCar(driver: String) extends RaceCar {
        def refuel = println(s"${driver} is refueling....")
    }

    def main(args: Array[String]) {
        val Norris = F1Car("Lando Norris")
        val Rossi = IndyCar("Alex Rossi")

        Norris.startEngine //Lando Norris's Engine started! Ready to race
        Rossi.startEngine //Alex Rossi's Engine started! Ready to race
        Rossi.pitStop //Alex Rossi is in the pit lane! Car: Up! Tires: Changed! Car: Down! Go!
        Norris.drsStart //Lando Norris opens the hatch: going fast!
        Rossi.refuel //Alex Rossi is refueling....
        //Rossi.drsStart, causes exception as expected

    }

}