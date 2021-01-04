
import akka.actor.{Actor, ActorRef}

class TransactionActor(receiver: ActorRef) extends Actor{

  override def receive: Receive = {
    case msg: Transaction => {
      sender() ! MoneyRequest(msg.amount)
    }
    case msg: MoneyResponse => {
      receiver ! MoneyDeposit(msg.amount)
    }
  }
}
