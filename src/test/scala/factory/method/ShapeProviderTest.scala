package factory.method

import org.scalatest.funsuite.AnyFunSuite

class ShapeProviderTest extends AnyFunSuite {

  test("should decide using inheritance") {
    val shape = new SquareProvider().getShape(.5)
    assert(shape.isInstanceOf[Square])
  }

}
