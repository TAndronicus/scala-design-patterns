package facade

import scala.collection.mutable

object BankingService {
  val accountsBalance: mutable.Map[Int, Double] = mutable.Map().withDefaultValue(0)

  def deposit(accountId: Int, amount: Double) = accountsBalance.update(accountId, accountsBalance.getOrElse(accountId, 0d) + amount)
  def withdraw(accountId: Int, amount: Double) = accountsBalance.update(accountId, accountsBalance.getOrElse(accountId, 0d) - amount)
  def getBalance(accountId: Int) = accountsBalance(accountId)
}
