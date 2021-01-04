import java.util.UUID
case class Transaction(uuid: UUID, senderBankId: BankId, receiverBankId: BankId, amount: Double)

case class MoneyRequest(amount: Double)
case class MoneyResponse(amount: Double)
case class MoneyDeposit(amount: Double)

case class BankId(id: Int) {
  if(id <= 0) {
    throw new RuntimeException(s"Invalid id $id")
  }
}

