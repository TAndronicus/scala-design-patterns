package singleton

import org.scalatest.funsuite.AnyFunSuite


class StringUtilsTest extends AnyFunSuite {

  test("should return true only on palindrome") {
    assert(StringUtils.isPalindrome("akka"))
    assert(!StringUtils.isPalindrome("scala"))
  }

}
