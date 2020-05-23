# Design patterns implemented in Scala

## Patterns
 1. [Creational](#cre)
     1. [Singleton](#sin)
     2. [Monostate](#monost)
 
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

 [Scala tour - singleton objects](https://docs.scala-lang.org/tour/singleton-objects.html)

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

#### Reference

Martin, Robert Cecil. Agile Software Development, Principles, Patterns, and Practices. 2002.

