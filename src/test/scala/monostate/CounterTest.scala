package monostate

import org.scalatest.funsuite.AnyFunSuite

class CounterTest extends AnyFunSuite {

  test("Instances should share state") {
    val (c1, c2) = (new Counter(), new Counter())
    assert(!c1.eq(c2), "Two different instances are created")
    c1.increment
    assert(c1.get == c2.get, "They share the same state")
    assert(c2.get == 1, "The counter of c2 was incremented on c1.increment call")
  }

}
