
import akka.actor.{Actor, Props}

case class accountCreationMessage(name: String, var amount: Double)

case class ActorRouteRequest(bankId: BankId)

class AccountSupervisor extends Actor{

  var count = 1
  override def receive: Receive = {
    case msg: accountCreationMessage => {
      val ref = context.actorOf(Props(new AccountActor(BankId(count), msg.name, msg.amount)), count.toString)
      println(s"created actor $ref")
      count += 1
    }
    case msg : ActorRouteRequest => {
      context.child(msg.bankId.id.toString()) match {
        case Some(actorRef) => {
          println(s"account ref found $actorRef")
          sender() ! actorRef
        }
        case None => {
          println(s"id not found ${msg.bankId.id}")}
          throw new RuntimeException("transaction failed, actor ref not found")
      }
    }
    case _ => {
      println("unknown message " + _)
    }
  }
}
