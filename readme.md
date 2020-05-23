# Design patterns implemented in Scala

## Patterns
 1. [Creational](#cre)
     1. [Singleton](#sin)
 
## Creational patterns<a name="introduction"></a>

### Singleton

It is useful when only one instance should be created in the application, for example utility classes, data sources.
The object's creation is lazy, i.e. it is not created when not accessed.

In Scala singleton can be created using the `object` keyword:

```Scala
object StringUtils {

  println("Creating StringUtils")

  def isPalindrome(s: String) = s.zip(s.reverse)
    .forall { case (c1, c2) => c1 == c2 }

}
```

Usage:

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
