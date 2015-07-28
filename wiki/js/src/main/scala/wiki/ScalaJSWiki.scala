package wiki
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode
import scala.util.Random
import scala.concurrent.Future
import scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scalatags.JsDom.all._
import scalatags.JsDom._
import upickle.default._
import autowire._
import strips.ontology._

object Client extends autowire.Client[String, upickle.default.Reader, upickle.default.Writer]{
  override def doCall(req: Request): Future[String] = {
    dom.ext.Ajax.post(
      url = "/api/" + req.path.mkString("/"),
      data = upickle.default.write(req.args)
    ).map(_.responseText)
  }

  def read[Result: upickle.default.Reader](p: String) = upickle.default.read[Result](p)
  def write[Result: upickle.default.Writer](r: Result) = upickle.default.write(r)
}

object Data {
  var ont : Option[SOntItem] = None
  def ontIs(name : String) : Boolean = ont match {
    case Some(o) => o.name == name
    case _ => false
  }
  var comments : List[Comment] = List()
}

@JSExport
object ScalaJSwiki {
  @JSExport
  def main(): Unit = {

    val outputBox = div.render
    val comments = div.render
    val inputBox = input(placeholder:="search", id:="search", `type`:="text").render
    val lineProg = {
      div(cls:="progress")(div(cls:="indeterminate")).render
    }

    def listView : (String, Any) => Unit = (inp : String, lookupType : Any) => {
      outputBox.innerHTML = ""
      outputBox.appendChild(lineProg)
      lookupType match {
        case OntLookup => {
          ontView(inp)
        }
        case SenseLookup => {
          Client[Api].getOntsFromWNSense(inp).call().foreach { result =>
            outputBox.innerHTML = ""
            outputBox.appendChild(
              ListOntItemRender(result, ontView)
            ).render
          }
        }
        case WordLookup => {
          Client[Api].getTripsAndWN(inp).call().foreach { result =>
            outputBox.innerHTML = ""
            outputBox.appendChild(
              ListOntItemRender(result, ontView)
            ).render
          }
        }
        case _ => dom.alert("unknown")
      }
    }

    def ontView : String => Unit = (inp : String) => {
      def _render(result : Option[SOntItem]) {
        outputBox.innerHTML = ""
        outputBox.appendChild(
          result.map(OntItemRender(_, ontView, listView)).getOrElse(div().render)
        ).render
      }
      if (Data ontIs inp) {
        getComments("o:%s".format(inp))
        _render(Data.ont)
      } else {
        //dom.alert("loading from server: %s".format(inp))
        getComments("o:%s".format(inp))
        Client[Api].getOnt(inp).call().foreach { result =>
          Data.ont = result
          _render(result)
        }
      }
    }

    def getComments(name : String) = {
      val addComment : (Comment) => Unit = (c : Comment) => {
        Client[Api].addComment(c).call()
      }

      Client[Api].getComments(name).call().foreach{ result =>
        Data.comments = result
        comments.innerHTML = ""
        comments.appendChild(
          result.map(CommentRender(_, addComment)).render
        )
      }
    }

    val lookupTypes = select(
      option(value := "ont")("ont"),
      option(value :="word")("word"),
      option(value :="sense")("sense"),
      option(value :="graph")("graph")
    ).render

    val spinner = {
      div(cls:="preloader-wrapper big active")(
        div(cls:="spinner-layer spinner-blue-only")(
          div(cls:="circle-clipper left")(
            div(cls:="circle")
          ),
          div(cls:="gap-patch")(
            div(cls:="circle")
          ),
          div(cls:="circle-clipper right")(
            div(cls:="circle")
          )
        )
      ).render
    }

    val submitHandler = {e : dom.Event => {
      if(lookupTypes.value == "ont")
      listView(inputBox.value, OntLookup)
      else if(lookupTypes.value == "word")
      listView(inputBox.value, WordLookup)
      else if(lookupTypes.value == "sense")
      listView(inputBox.value, SenseLookup)
      false;}
    }

    val theForm = form(onsubmit := submitHandler, style := "width:100%;")(
      div(cls := "container")(
        div(cls:="row %s".format(Colors.sidebar))(
          div(cls:="input-field col s6")(label("search"),inputBox),
          div(cls:="input-field col s3")(lookupTypes),
          div(cls:="input-field col s3")(button(cls :="waves-effect waves-light btn %s".format(Colors.defaultBtn), `type` := "submit")("Search"))
        )
      )
      //,
      //button(`type` := "submit", cls := "pure-button pure-button-primary")("search")
    ).render
    dom.document.body.appendChild(
      div(style:="height:100%;")(
        tags2.nav(cls := Colors.navColor, id:="navbar")(
          div(cls :="nav-wrapper container")(
            a(href:="#", cls:="brand-logo", style:="font-weight:200;")("Trips Wiki"),
            ul(id:="nav-mobile", cls:="right hide-on-med-and-down")(
              li(a(href:="/")("browser")),
              li(a(href:="/graph")("graph"))
            )
          )
        ),
        div(cls:= "row", id:="main", style := "height:100%;")(
          div(cls := "col s12 %s %s".format(Colors.bodyColor, Colors.bodyText), style := "height:100%;")(
            div(cls := "container")(
              //p("Enter an ont name"),
              theForm,
              script(
                raw(
                  """$(document).ready(function() {
                    $('select').material_select();
                  });"""
                )),
                div(cls:= "row")(
                  div(cls := "col s6")(outputBox),
                  div(cls := "col s6")(comments)
                )
              )
            )
          )
        ).render
      )
    }
  }
