package composite

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

