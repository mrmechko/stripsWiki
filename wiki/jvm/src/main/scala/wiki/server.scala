package wiki
import upickle.default._
import spray.routing.SimpleRoutingApp
import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext.Implicits.global
import spray.http.{MediaTypes, HttpEntity}

import wiki.Graph._

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
      } ~
      get {
        path("graph" / Segment ){ s =>
          complete{
            HttpEntity(
              MediaTypes.`text/html`,
              Template.buildGraph(s, Server.mgfw(s))
            )
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

  import strips.lexicon._
  import strips.util.LexiconFromXML
  lazy val lex = new TripsLexicon(LexiconFromXML("/Users/mechko/nlpTools/flaming-tyrion/lexicon/data/"))

  def getOnt(name : String) = {
    ont --> name
  }

  def getOntsFromWNSense(word : String) : List[String] = {
    println(word)
    (ont !# word).map(_.name)
  }

  def getOntsFromWordWN(word : String) : List[String] = {
    println(word)
    (ont !@ word).map(_.name)
  }

  def getTripsAndWN(word : String) : (List[String], List[String]) = {
    val forms = (lex % word)
    (forms.flatMap(f=>getOntsFromWordWN(f)).toList,
    forms.flatMap(f => (lex --> f).flatMap(_.classes.map(_.ontType))).toList)
  }

  def graphImage(word : String) : String= {
    //if dot file exists
    val dot = makeGraphFromWord(word)
    import scala.sys.process._
    import java.io._
    val pw = new PrintWriter(new File("dot/%s.dot".format(word)))
    pw.write(dot.mkString("\n"))
    pw.flush
    pw.close
    Seq("dot", "-oimg/%s.png".format(word), "-Tpng", "dot/%s.dot".format(word)) #| Seq("echo", "img/%s.png".format(word)) !!<
  }

  def mgfw(word : String) : GGraph = {
    val onts = (ont !!@ word)
    val trips = onts.map(k => TGN(k._1))
    val wn = onts.flatMap(_._2).toList.distinct.map(k => WGN(k))
    val te = onts.map(e => GEdge(TGN(e._1), WGN(e._2.head)))
    val we = onts.flatMap(o => o._2.sliding(2).map(l => GEdge(WGN(l(0)), WGN(l(1)))))
    GGraph((wn ++ trips), (te ++ we))
  }

  def makeGraphFromWord(word : String) : List[String] = {
    val onts = (ont !!@ word)
    onts.map(o => {
      o._2.sliding(2).map(x => (nmlz(x(0)), nmlz(x(1)))).toList.+:((nmlz(o._2.head), nmlz(o._1)))
    }).flatten.distinct.map(o=> "%s -> %s;".format(o._1, o._2)).+:("digraph {").:+("}")
  }

  var comments : Map[String, Set[Comment]] = Map().withDefault((s : String) => {Set[Comment](
    Comment("rik", "# %s\n `some code`".format(s), target = s, uuid = Comment.uuid)
  )})

  def getComments(name : String) : List[Comment] = {
    comments(name).toList
  }

  def addComment(comment : Comment) = {
    //repace the existing one if it does
    val rel = comments(comment.target).filter(x => x.uuid != comment.uuid) + comment
    comments = comments.updated(comment.target, rel)
  }

}
