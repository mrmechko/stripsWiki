package wiki
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode
import scala.util.Random
import scala.concurrent.Future
import scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scalatags.JsDom.all._
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

    val inputBox = input.render
    val lookupTypes = select(
      option("ont"),
      option("word"),
      option("sense"),
      option("graph")
    ).render
    val outputBox = div.render
    val comments = div.render
    val theForm = form(cls := "pure-form")(
      fieldset(
        legend("search ontology"),
        span(inputBox,
        lookupTypes)
        //,
        //button(`type` := "submit", cls := "pure-button pure-button-primary")("search")
      )
    ).render

    def listView : (String, Any) => Unit = (inp : String, lookupType : Any) => {
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
        dom.alert("loading from server: %s".format(inp))
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

    theForm.onsubmit = {e : dom.Event => {
      if(lookupTypes.value == "ont")
        listView(inputBox.value, OntLookup)
      else if(lookupTypes.value == "word")
        listView(inputBox.value, WordLookup)
      else if(lookupTypes.value == "sense")
        listView(inputBox.value, SenseLookup)
      false;}
    } /*{(e: dom.KeyboardEvent) => {
        if (e.keyCode == KeyCode.enter)
          updateOutput()
        e.stopPropagation()
      }
    }*/
    //ontView("")
    dom.document.body.appendChild(
      div(cls:= "pure-g")(
          div(cls := "pure-u-1-24")(),
          div(cls := "pure-u-12-24")(
            h1("Ont Browser"),
            //p("Enter an ont name"),
            theForm,
            outputBox
          ),
          div(cls := "pure-u-1-24")(),
          div(cls := "pure-u-9-24")(
            comments
          ),
          div(cls := "pure-u-1-24")()
      ).render
    )
  }
}
