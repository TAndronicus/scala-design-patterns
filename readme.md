# Design patterns implemented in Scala

## Patterns
 1. [Creational](#cre)
     1. [Singleton](#sin)
     2. [Monostate](#monost)
     3. [Factory method](#factMet)
 2. [Structural](#struct)
     1. [Facade](#facade)
     2. [Adapter](#adapter)
     3. [Composite](#composite)
     4. [Proxy](#proxy)
 
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

### Adapter<a name="adapter"></a>

#### Explanation

Adapter is used to facilitate the communication between two systems by providing by providing a common interface.
An example is connecting to a legacy system, whose API does not conform to ours.

#### Implementation

Suppose our system uses the following trait for services making web requests:

```Scala
trait WebRequester {
  def request(obj: Any): String
}
```

And we need to integrate an external / legacy service with different API:

```Scala
class LegacyWebRequester {
  def sendRequest(json: String) = "200 OK"
}
```

With adapter we can wrap the service (adaptee) and expose the target interface to the client:

```Scala
class WebRequesterAdapter(legacyWebRequester: LegacyWebRequester) extends WebRequester {
  override def request(obj: Any): String = {
    val request = toJson(obj)
    legacyWebRequester.sendRequest(request)
  }
  def toJson(obj: Any) = obj.toString
}
```

In this case the service is stateless, so we can leverage Scala's singleton `object`:

```Scala
object StatelessWebRequesterAdapter extends WebRequester {
  val legacyWebRequester = new LegacyWebRequester
  override def request(obj: Any): String = {
    val request = toJson(obj)
    legacyWebRequester.sendRequest(request)
  }
  def toJson(obj: Any) = obj.toString
}
```

This will provide a single entry for the legacy module.

#### Usage

The legacy service needs to be wrapped into the adapter, so that the trait's method can be invoked:

```Scala
object Client {
  def main(args: Array[String]): Unit = {
    val webRequester: WebRequester = new WebRequesterAdapter(new LegacyWebRequester)
    println(webRequester.request(42))
    println(StatelessWebRequesterAdapter.request(42))
  }
}
```

In this case the adaptar can instantiate the adaptee, so that it's invisible to the client.

#### Reference

 Martin, Robert Cecil. Agile Software Development, Principles, Patterns, and Practices. 2002.

### Composite<a name="composite"></a>

#### Explanation

Composite pattern allows nesting structures and dealing with them uniformly.

#### Implementation

A great example of composite pattern is a tree.
A tree can be a leaf or a branch, which itself is also a tree (a subtree of it root).
The following snippet is an example of binary tree used to sort an array stored in leaves.

```Scala
sealed abstract class BinaryTree {
  def sorted: List[Double]
}
case class Branch(left: BinaryTree, right: BinaryTree) extends BinaryTree {
  override def sorted: List[Double] = sortRecursively(left.sorted, right.sorted)
  private def sortRecursively(left: List[Double], right: List[Double]): List[Double] = {
    if (left.isEmpty && right.isEmpty) List()
    else if (left.isEmpty) right.sorted
    else if (right.isEmpty) left.sorted
    else if (left.head <= right.head) left.head :: sortRecursively(left.tail, right)
    else right.head :: sortRecursively(left, right.tail)
  }
}
case class Leaf(ar: List[Double]) extends BinaryTree {
  override def sorted: List[Double] = ar.sorted
}
```

Both `Branch` and `Leaf` can be sorted.
This is a common behavior defined in the abstract superclass `BinaryTree`.
Array sorting is done recursively and propagates from root to leaves.

#### Usage

Any array can be represented as a tree.
Having a tree we can sort it in the following way:

```Scala
test("Should return sorted array") {
  val tree = Branch(
    Branch(
      Leaf(List(1, 4, 8)),
      Branch(
        Leaf(List(2, 6)),
        Leaf(List(7))
      )
    ),
    Branch(
      Leaf(List(3, 9)),
      Leaf(List(5))
    )
  )
  val sorted = tree.sorted
  assert(sorted == List(1, 2, 3, 4, 5, 6, 7, 8, 9))
}
```

#### Reference

 Martin, Robert Cecil. Agile Software Development, Principles, Patterns, and Practices. 2002.

### Proxy<a name="proxy"></a>

#### Explanation

Proxy wraps an object and delegates to it while adding additional logic.
Most common use cases are restricting access (proxy checks permissions to the underlying object), dirty checking (many ORMs are keeping track of changes in the entities during transaction, so that no unnecessary updates are triggered) or, as in the following example, gathering statistics (proxy counts the number of database accesses).

#### Implementation

Suppose we have a `DataSource` trait, which defines database access.

```Scala
trait DataSource {
  def executeQuery(query: String)
}
```

`SimpleDataSource` is an object used in production code.

```Scala
object SimpleDataSource extends DataSource {
  override def executeQuery(query: String): Unit = println(query)
}
```

`CountingDataSource` proxy can be used when testing to keep track of the database load.

```Scala
object CountingDataSource extends DataSource {
  private var counter = 0
  override def executeQuery(query: String): Unit = {
    counter += 1
    SimpleDataSource.executeQuery(query)
  }
  def getNumberOfDbQueries = counter
}
```

#### Usage

Every query to the database is recorded.
This can help avoiding excessive database load problems, for example `N+1` select problem.

```Scala
test("Should count number of db shots") {
  CountingDataSource.executeQuery("select * from people;")
  CountingDataSource.executeQuery("insert into people(id, name) values (1, 'John Doe')")
  assert(CountingDataSource.getNumberOfDbQueries == 2)
}
```

#### Reference

 Martin, Robert Cecil. Agile Software Development, Principles, Patterns, and Practices. 2002.
 
### Decorator<a name="decorator"></a>

#### Explanation

Decorator allows stacking implementations to add additional features at runtime.

#### Implementation

Suppose we have a `WebPage` interface:

```Scala
trait WebPage {
  def render();
}
```

And its implementation:

```Scala
class SimpleWebPage extends WebPage {
  override def render(): Unit = println("Rendered page")
}
```

We can define an interface for the decorators.
The decorator delegates the basic logic to the object it decorates.

```Scala
abstract class WebPageDecorator(webPage: WebPage) extends WebPage {
  override def render(): Unit = webPage.render()
}
```

Now decorators can be implemented to decorate `SimpleWebPage` with additional logic:

```Scala
class AuthorizedWebPage(webPage: WebPage) extends WebPageDecorator(webPage) {
  override def render(): Unit = {
    super.render()
    println("Authorizing...")
  }
}

class AuthenticatedWebPage(webPage: WebPage) extends WebPageDecorator(webPage) {
  override def render(): Unit = {
    super.render()
    println("Authenticating...")
  }
}
```

#### Usage

When decorating `SimpleWebPage` and running `render`, the implementations from subsequent decorators is invoked:

```Scala
object Client {

  def main(args: Array[String]): Unit = {
    val webPage = new AuthenticatedWebPage(new AuthorizedWebPage(new SimpleWebPage))
    webPage.render()
    // Rendered page
    // Authorizing...
    // Authenticating...
  }

}
```

#### Reference

 Martin, Robert Cecil. Agile Software Development, Principles, Patterns, and Practices. 2002.
