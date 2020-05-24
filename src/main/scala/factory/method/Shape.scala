package factory.method

sealed abstract class Shape
case class Circle(radius: Double) extends Shape
case class Square(side: Double) extends Shape
