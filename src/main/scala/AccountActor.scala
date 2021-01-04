
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class AccountActor(val bankId: BankId, val name: String, var amount: Double) extends Actor{

  override def preStart(): Unit = {
    super.preStart()
    println(s"$name created with id ${bankId.id} and amount $amount")
  }

  override def receive: Receive = {
    case msg: Transaction => {
      implicit val timeout = Timeout(10 seconds)
      implicit val ec = ExecutionContext.global
      val routeRequest = (context.parent ? ActorRouteRequest(msg.receiverBankId)).mapTo[ActorRef]
      routeRequest.onComplete{
        case Success(actorRef) => {
          val ref = context.actorOf(Props(new TransactionActor(actorRef)))
          ref ! msg
        } case Failure(exception) => {
          println(exception)
          println("Transaction failed")
        }
      }
    }
    case msg: MoneyRequest => {
      if(msg.amount <= amount) {
        amount -= msg.amount
        sender() ! MoneyResponse(msg.amount)
        println(s"Money ${msg.amount} sent to transaction actor")
      } else {
        println("not enough money")
      }
    }
    case msg: MoneyDeposit => {
      amount += msg.amount
      println(s"$name received money ${msg.amount} and total is $amount")
    }
    case _ => {
      println(s"Unknown message = " + _)
    }
  }
}
