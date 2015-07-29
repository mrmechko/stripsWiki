package wiki

object FileWriter {
  import akka.actor.Actor
  import akka.event.Logging
  import java.io._


  class FileWriterImpl extends Actor {
    //need to add committing
    private def write(name : String, old : String, res : String, user : String) : Boolean = {
      println(name)
      println(old)
      println(res)
      println(user)
      if(!(new File(name).exists) && old == "") {
        create(name, res)
      } else {
        val lines = scala.io.Source.fromFile(name).mkString("\n")
        if (lines == old) {
          val pw = new PrintWriter(new File(name))
          pw.write(res); pw.flush; pw.close
          true
        } else false
      }
    }

    private def create(name : String, res : String) : Boolean = {
      if(!(new File(name).exists)) {
        val pw = new PrintWriter(new File(name))
        pw.write(res); pw.flush; pw.close
        true
      } else false
    }

    def receive = {
      case WriteFile(name, old, res, user) => {
        if(write(name, old, res, user)){
          sender ! WriteSuccess
        } else {
          sender ! WriteFail
        }
      }
      case CreateFile(name, res) => {
        if (create(name, res)){
          sender ! WriteSuccess
        } else sender ! WriteFail
      }
      case _ => println("Error: message not recognized")
    }
  }

  class LoginDemon extends Actor {
    private var tokens = Map[String, String]()
    def receive = {
      case Authenticat(user, pass) => {
        println("authenticating...")
        if ("rochester" == pass) {
          val token = Comment.uuid
          tokens = tokens.updated(token, user)
          sender ! LoginS(user, token)
        }
        else sender ! LoginF
      }
      case BearToken(t) => {
        sender ! tokens.get(t)
      }
      case _ => println("Error: message not recognized")
    }
  }

}
