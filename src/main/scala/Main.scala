
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Main {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem("hello")
    val supervisor = system.actorOf(Props[AccountSupervisor], "supervisor")

    while (true){
      println("Option 1 for account and 2 for transactions")
      val option: Int = scala.io.StdIn.readInt()
      if (option == 1) {
        println("Enter name")
        val name: String = scala.io.StdIn.readLine()

        println("Enter amount")
        val amount: Double = scala.io.StdIn.readDouble()

        val createAccount = accountCreationMessage(name = name, amount = amount)
        supervisor ! createAccount
      } else {
        println("Enter sender bank ID")
        val senderBankId: Int = scala.io.StdIn.readInt()
        println("Enter receiver bank id")
        val receiverBankId: Int = scala.io.StdIn.readInt()
        println("Enter amount to send ")
        val amount: Double = scala.io.StdIn.readDouble()
        val transaction = Transaction(java.util.UUID.randomUUID(),BankId(senderBankId), BankId(receiverBankId), amount)

        implicit val timeout = Timeout(10 seconds)
        implicit val ec = ExecutionContext.global
        val routeRequest = (supervisor ? ActorRouteRequest(BankId(senderBankId))).mapTo[ActorRef]
        routeRequest.onComplete {
          case Success(actorRef) => {
            println(s"Transaction processed $transaction")
            actorRef ! transaction
          }
          case Failure(exception) => {
            println(s"Transaction failed $exception")
          }
        }
      }
    }
  }
}
