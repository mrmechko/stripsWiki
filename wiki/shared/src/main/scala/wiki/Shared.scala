package wiki

case class WriteFile(name : String, old : String, res : String, user : String = "system")
case class CreateFile(name : String, res : String)
case object WriteSuccess
case object WriteFail

import strips.ontology._

case class TreeNode(title : String, key : String, folder : Boolean, children : List[TreeNode])

case class Authenticat(username : String, password : String)
trait credentials
case class LoginS(username : String, token : String) extends credentials
case object LoginF extends credentials

case class RegisterToken(user : String, token : String)
case class BearToken(token : String)

object Colors {
  val base1 = "blue"
  val base2 = "indigo"
  val bodyColor = "%s accent-2".format(base1)
  val bodyText = "white-text"
  val container = base2
  val commentBg = "%s darken-4".format(base1)
  val containerText = "white-text"
  val navColor = "%s darken-4".format(base2)
  val defaultBtn = base1
  val sidebar = base2
  val footerColor = "%s accent-2".format(base2)
  val accent1 = "red"
  val accent2 = "purple darken-4"
}

trait Api{
  def list(path: String): Seq[String]
  def getOnt(name : String) : Option[SOntItem]
  def getOntsFromWNSense(word : String) : List[String]
  def getOntsFromWordWN(word : String) : List[String]
  def getTripsAndWN(word : String) : (List[String], List[String])
}

case class Comment(author : String, body : String, target : String, uuid : String = "")

object Comment {
  def uuid = java.util.UUID.randomUUID.toString
}

case object OntLookup
case object WordLookup
case object SenseLookup
case object GraphWNTrips


object Graph {
  case class GNode(id : String, name : String) {
    def u : String = "{ data : { id: %s, name : %s } }".format(id, name)
    def g : String = {
      val style = if(name.startsWith("\"o:")) {
        "g.node(%s).style = \"fill: #f77\";".format(id)
      } else ""
      "g.setNode(%s, { label: %s });".format(id, name) + style
    }
  }
  case class GEdge(target : GNode, source : GNode) {
    def u : String = "{ data : { source : %s, target : %s } }".format(source.id, target.id)
    def g : String = "g.setEdge(%s, %s);".format(source.id, target.id)
  }

  case class GGraph(nodes : List[GNode], edges : List[GEdge]) {
    def script = {
      """var g = new dagreD3.graphlib.Graph()
          .setGraph({})
          .setDefaultEdgeLabel(function() { return {}; });
      """+
      "%s\n".format(nodes.map(_.g).mkString("\n"))+
      "%s\n".format(edges.map(_.g).mkString("\n"))+
      """
      var svg = d3.select("svg"),inner = svg.select("g");
      // Set up zoom support
      var zoom = d3.behavior.zoom().on("zoom", function() {
        inner.attr("transform", "translate(" + d3.event.translate + ")" +
                                        "scale(" + d3.event.scale + ")");
          });
      svg.call(zoom);

      // Create the renderer
      var render = new dagreD3.render();

      // Run the renderer. This is what draws the final graph.
      render(inner, g);

      // Center the graph
      var initialScale = 0.75;
      zoom
        .translate([(svg.attr("width") - g.graph().width * initialScale) / 2, 20])
        .scale(initialScale)
        .event(svg);
        svg.attr('height', g.graph().height * initialScale + 40);
      """
    }
  }



  def nmlz(s : String) : String = {
    "\"%s\"".format(s)
  }
  def TGN(n : String) = GNode(nmlz(n), "\"o:%s\"".format(n))
  def WGN(n : String) = GNode(nmlz(n), "\"w:%s\"".format(n))
}
