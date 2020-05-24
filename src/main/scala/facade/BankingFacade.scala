package facade

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
