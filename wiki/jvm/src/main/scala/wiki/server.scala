package wiki
import upickle.default._
import spray.routing.SimpleRoutingApp
import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext.Implicits.global
import spray.http.{MediaTypes, HttpEntity}

object Template{
  import scalatags.Text.all._
  import scalatags.Text.tags2.title
  val txt =
    "<!DOCTYPE html>" +
    html(
      head(
        title("Example Scala.js application"),
        link(rel :="stylesheet", href:="http://yui.yahooapis.com/pure/0.6.0/pure-min.css"),
        link(rel :="stylesheet", href:="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.0/css/materialize.min.css"),
        script(src := "https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.0/js/materialize.min.js"),
        script(src :="//cdnjs.cloudflare.com/ajax/libs/list.js/1.1.1/list.min.js"),
        meta(name :="viewport", content :="width=device-width, initial-scale=1"),
        meta(httpEquiv:="Content-Type", content:="text/html; charset=UTF-8"),
        script(`type`:="text/javascript", src:="/client-fastopt.js"),
        link(href:="http://fonts.googleapis.com/css?family=Cabin", rel:="stylesheet"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.0/js/materialize.min.js")
        //script(`type`:="text/javascript", src:="//localhost:12345/workbench.js")
        //link(
        //  rel:="stylesheet",
        //  `type`:="text/css",
        //  href:="META-INF/resources/webjars/bootstrap/3.2.0/css/bootstrap.min.css"
        //)
      ),
      body(style := "font-family: 'Cabin', sans-serif;")(
        script("wiki.ScalaJSwiki().main()")
      )
    )
}
object AutowireServer extends autowire.Server[String, upickle.default.Reader, upickle.default.Writer]{
  def read[Result: upickle.default.Reader](p: String) = upickle.default.read[Result](p)
  def write[Result: upickle.default.Writer](r: Result) = upickle.default.write(r)
}
object Server extends SimpleRoutingApp with Api{
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    startServer("0.0.0.0", port = 8080) {
      get{
        pathSingleSlash {
          complete{
            HttpEntity(
              MediaTypes.`text/html`,
              Template.txt
            )
          }
        } ~
        getFromResourceDirectory("")
      } ~
      post {
        path("api" / Segments){ s =>
          extract(_.request.entity.asString) { e =>
            complete {
              AutowireServer.route[Api](Server)(
                autowire.Core.Request(s, upickle.default.read[Map[String, String]](e))
              )
            }
          }
        }
      }
    }
  }

  def list(path: String) = {
    val chunks = path.split("/", -1)
    val prefix = "./" + chunks.dropRight(1).mkString("/")
    val files = Option(new java.io.File(prefix).list()).toSeq.flatten
    files.filter(_.startsWith(chunks.last))
  }
  import strips.ontology._
  import strips.util.OntologyFromXML
  val ont = SOntology(OntologyFromXML("/Users/mechko/nlpTools/flaming-tyrion/lexicon/data/"))

  def getOnt(name : String) = {
    ont --> name
  }

  def getComments(name : String) : List[Comment] = {
    List(Comment("rik", "# %s\n `some code`".format(name)), Comment("omid", "test2"))
  }

  def getWordFromWN(word : String) : List[String] = {
    println(word)
    (ont !# word).map(_.name)
  }
}
