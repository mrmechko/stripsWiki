package wiki

object FileWriter {
  import akka.actor.Actor
  import akka.event.Logging
  import java.io._

  def commitFF(path : String, user : String) : Int = {
    import scala.sys.process._
    println(Seq("git", "-C", Paths.wikiBase, "add", path) !!)
    println(Seq("git", "-C", Paths.wikiBase, "commit", "-m", "%s modified %s".format(user, path.split("/").last)) !!)
    println(Seq("git", "-C", Paths.wikiBase, "push", "origin", "master") !!)
    1
  }

  class FileWriterImpl extends Actor {
    //need to add committing

    private def write(name : String, old : String, res : String, user : String) : Boolean = {
      if(!(new File(name).exists) && old == "") {
        if (create(name, res)){
          commitFF(name, user)
          true
        } else false
      } else {
        val lines = scala.io.Source.fromFile(name).mkString.stripLineEnd//("\n")
        println("-\n%s\n-\n%s\n-".format(lines, old))
        if (lines == old) {
          val pw = new PrintWriter(new File(name))
          pw.write(res); pw.flush; pw.close;
          commitFF(name, user);
          true
        } else false
      }
    }

    private def create(name : String, res : String) : Boolean = {
      if(!(new File(name).exists)) {
        val pw = new PrintWriter(new File(name))
        pw.write(res); pw.flush; pw.close;
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
