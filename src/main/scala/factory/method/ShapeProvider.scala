package factory.method

trait ShapeProvider {
  def getShape(diameter: Double): Shape
}
class CircleProvider extends ShapeProvider {
  override def getShape(diameter: Double): Shape = Circle(diameter / 2)
}
class SquareProvider extends ShapeProvider {
  override def getShape(diameter: Double): Shape = Square(diameter / math.sqrt(2))
}
