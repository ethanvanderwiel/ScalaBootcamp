 class Vec2D(x: Int, y: Int) {
    /*
    *For this whole exercise, I wasn't sure if it was better to create a new vector
    *that is the result of the operations, or to update the first vector. 
    *I currently have it set to create a new vector and return that vector.
    *This caused a need for the getter methods for x and y, I believe.
    */
    def +(adding: Vec2D): Vec2D = {
        //Adds this vector to a new vector and returns result
        new Vec2D(x + adding.getX, y + adding.getY) 
    }

    def - (subtracting: Vec2D): Vec2D = {
        //Subtracts new vector from this vector and returns result
        new Vec2D(x - subtracting.getX, y - subtracting.getY)
    }

    def * (multiply: Int): Vec2D = {
        //Multiplies this vector by a scalar value, returns result
        new Vec2D(x * multiply, y * multiply) 
    }

    //Overridden toString method to write in vector notation
    override def toString = "(" + x + ", " + y + ")"

    val getX : Int = x
    val getY : Int = y
}

object VectorMain {
    //Testing the Vec2D class
    def main(args: Array[String]) = {
        val firstVector = new Vec2D(1, 2)
        val secondVector = new Vec2D(3, 4)

        println(firstVector + secondVector) //(4, 6)
        println(firstVector - secondVector) //(-2, -2)
        println(firstVector * 7) //(7, 14)
    }
}

