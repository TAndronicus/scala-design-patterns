# Design patterns implemented in Scala

## Patterns
 1. [Creational](#cre)
     1. [Singleton](#sin)
     2. [Monostate](#monost)
     3. [Factory method](#factMet)
 2. [Structural](#struct)
     1. [Facade](#facade)
 
## Creational patterns<a name="cre"></a>

### Singleton<a name="sin"></a>

#### Explanation

It is useful when only one instance should be created in the application, for example utility classes, data sources.
The object's creation is lazy, i.e. it is not created when not accessed.

#### Implementation

In Scala singleton can be created using the `object` keyword:

```Scala
object StringUtils {
  println("Creating StringUtils")

  def isPalindrome(s: String) = s.zip(s.reverse)
    .forall { case (c1, c2) => c1 == c2 }
}
```

#### Usage

```Scala
object Client {

  def main(args: Array[String]): Unit = {
    println(StringUtils.isPalindrome("akka"))
    // Creating StringUtils
    // true
    println(StringUtils.isPalindrome("scala"))
    // false
  }

}
```

The object is initialized only once — during the first call (`Creating StringUtils` is printed only once, after calling `StringUtils.isPalindrome`).
If the object was not used, the initialization would not occur.

#### Reference

 1. [Scala tour - singleton objects](https://docs.scala-lang.org/tour/singleton-objects.html)
 2. Martin, Robert Cecil. Agile Software Development, Principles, Patterns, and Practices. 2002.

### Monostate<a name="monost"></a>

#### Explanation

According to R. Martin monostate has the same behavior as singleton, but does not require creating only one instance of the class.
This means, that multiple different instances of a single class should share state.

#### Implementation

In languages like Java this is easy to implement using static variables (although this is considered a code smell). 
Since scala does not have the concept of static variables, [companion objects](https://docs.scala-lang.org/tour/singleton-objects.html) can be used.
(There is a [proposal](https://docs.scala-lang.org/sips/static-members.html) of `@static` annotation for Scala.)

```Scala
class Counter {
  def increment = Counter.x += 1
  def get = Counter.x
}

object Counter {
  private var x: Int = 0
}
```

#### Usage

```Scala
test("Instances should share state") {
  val (c1, c2) = (new Counter(), new Counter())
  assert(!c1.eq(c2), "Two different instances are created")
  c1.increment
  assert(c1.get == c2.get, "They share the same state")
  assert(c2.get == 1, "The counter of c2 was incremented on c1.increment call")
}
```

Although different instances were created, they share the same state.
The behavior is the same as of singleton.
The difference is in how the client code uses it — in monostate the fact, that the state is shared is hidden from the client (constructor is used instead of `object` or `getInstance` method).
Singleton fulfills the definition with the property of multiple instances relaxed.

#### Reference

Martin, Robert Cecil. Agile Software Development, Principles, Patterns, and Practices. 2002.

### Factory method<a name="factMet"></a>

#### Explanation

Factory method implements logic associated with creating subclasses of a certain class.

#### Implementation

Consider the following type hierarchy:

```Scala
sealed abstract class Shape
case class Circle(radius: Double) extends Shape
case class Square(side: Double) extends Shape
```

The creation is declared in `ShapeProvider` interface and implemented in `CircleProvider` and `SquareProvider`:

```Scala
trait ShapeProvider {
  def getShape(diameter: Double): Shape
}
class CircleProvider extends ShapeProvider {
  override def getShape(diameter: Double): Shape = Circle(diameter / 2)
}
class SquareProvider extends ShapeProvider {
  override def getShape(diameter: Double): Shape = Square(diameter / math.sqrt(2))
}
```

#### Usage

```Scala
test("should decide using inheritance") {
  val shape = new SquareProvider().getShape(.5)
  assert(shape.isInstanceOf[Square])
}
```

#### Reference

Gamma, Erich, et al. Design Patterns: Elements of Reusable Object-Oriented Software. 1994.

## Structural patterns<a name="struct"></a>

### Facade<a name="facade"></a>

#### Explanation

Facade eases the usage of some complex API.

#### Implementation

Suppose we have a service for the deposit and withdrawal of money and lookup of balance:

```Scala
object BankingService {
  val accountsBalance: mutable.Map[Int, Double] = mutable.Map().withDefaultValue(0)

  def deposit(accountId: Int, amount: Double) = accountsBalance.update(accountId, accountsBalance.getOrElse(accountId, 0d) + amount)
  def withdraw(accountId: Int, amount: Double) = accountsBalance.update(accountId, accountsBalance.getOrElse(accountId, 0d) - amount)
  def getBalance(accountId: Int) = accountsBalance(accountId)
}
```

Transferring money between accounts would require the client to subsequently call `BankingService.withdraw` and `BankingService.deposit` and make sure both run within a single transaction, so that no money is lost.
We can provide a facade, that eases takes the responsibility of transferring and easing API usage:

```Scala
object BankingFacade {
  def transfer(from: Int, to: Int, amount: Double) = {
    withinTransaction {
      BankingService.withdraw(from, amount)
      BankingService.deposit(to, amount)
    }
  }
  // dummy method that should wrap a transaction context
  private def withinTransaction(r: => Unit) = r
}
```

Now all there operations are as easy as invoking `BankingFacade.transfer`.

#### Usage

```Scala
before {
  BankingService.deposit(1, 100)
  BankingService.deposit(2, 100)
}

test("should transfer money between accounts") {
  BankingFacade.transfer(1, 2, 25)
  assert(BankingService.getBalance(2) == 125d)
}
```

Client only needs to invoke `BankingFacade.transfer` function, that takes the responsibility of transactional withdrawal and deposit.

#### Reference

 Martin, Robert Cecil. Agile Software Development, Principles, Patterns, and Practices. 2002.
