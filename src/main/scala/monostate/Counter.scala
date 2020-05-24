package monostate

class Counter {
  def increment = Counter.x += 1
  def get = Counter.x
}

object Counter {
  private var x: Int = 0
}
