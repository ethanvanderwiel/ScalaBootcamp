/*
* This activity was a bit confusing. I first started out and created the entire activity
* without the factory. It worked perfectly fine. I then saw I had to have the 
* factory and looked online as to what the factory was supposed to look like.
* The object RaceCarFactory was the answer for almost all of the questions online.
* After creating the factory, I had issue with compiling as the vals were of 
* type RaceCar and thus couldn't use their custom methods.
* I then turned to Joe Sawyer for help, and he said the best solution he could
* think of at the time was adding a custom method that references the special 
* method in each subclass. This definitely makes it work, but I have
* no idea if it is the intended solution. 
*
* Within the testing spec class, the tests do test the runttime type of the variables. 
*/

sealed abstract class RaceCar {
    def driver: String
    def custom: String //recommended solution from Joe. Within each subclass, calls their special method
    def startEngine: String = s"${driver}'s Engine started! Ready to race" //I'm aware all of these defs should probably be vals. 
    def pitStop: String = s"${driver} is in the pit lane! Car: Up! Tires: Changed! Car: Down! Go!"
}

object RaceCarFactory {
    def apply(s: String): RaceCar = {
        if(s == "F1Car") F1Car("Lando Norris")
        else IndyCar("Alex Rossi")
    }
}

case class F1Car(driver: String) extends RaceCar {
    def drsStart = s"${driver} opens the hatch: going fast!"
    def custom = drsStart
}

case class IndyCar(driver: String) extends RaceCar {
    def refuel = s"${driver} is refueling...."
    def custom = refuel
}

object AbstractImpl {
    def main(args: Array[String]) {
        val Norris = RaceCarFactory("F1Car")
        val Rossi = RaceCarFactory("IndyCar")

        println(Norris.startEngine) //Lando Norris's Engine started! Ready to race
        println(Rossi.startEngine) //Alex Rossi's Engine started! Ready to race
        println(Rossi.pitStop) //Alex Rossi is in the pit lane! Car: Up! Tires: Changed! Car: Down! Go!
        println(Norris.custom) //Lando Norris opens the hatch: going fast!
        println((Rossi).custom) //Alex Rossi is refueling....
        
    }

}