package singleton

object StringUtils {
  println("Creating StringUtils")

  def isPalindrome(s: String) = s.zip(s.reverse)
    .forall { case (c1, c2) => c1 == c2 }
}
