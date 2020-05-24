package composite

import org.scalatest.funsuite.AnyFunSuite

class BinaryTreeTest extends AnyFunSuite {

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

}
