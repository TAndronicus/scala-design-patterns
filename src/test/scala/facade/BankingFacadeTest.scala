package facade

import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite

class BankingFacadeTest extends AnyFunSuite with BeforeAndAfter {

  before {
    BankingService.deposit(1, 100)
    BankingService.deposit(2, 100)
  }

  test("should transfer money between accounts") {
    BankingFacade.transfer(1, 2, 25)
    assert(BankingService.getBalance(2) == 125d)
  }

}
