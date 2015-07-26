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

object GithubMarkdown {
  def convert(text : String, target : dom.Element) : Unit = {
    dom.ext.Ajax.post(
      url = "https://api.github.com/markdown/raw",
      data = text,
      headers = Map[String, String]("Content-Type" -> "text/plain")
    ).map(_.responseText).foreach { result =>
      target.innerHTML = result
    }
  }
}

@JSExport
object ScalaJSwiki {
  @JSExport
  def main(): Unit = {

    val inputBox = input.render
    val lookupTypes = select(
      option("ont"),
      option("word"),
      option("sense")
    ).render
    val outputBox = div.render
    val comments = div.render
    val theForm = form(cls := "pure-form")(
      fieldset(
        legend("search ontology"),
        inputBox,
        lookupTypes
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
          Client[Api].getWordFromWN(inp).call().foreach { result =>
            outputBox.innerHTML = ""
            outputBox.appendChild(
              ListOntItemRender(result, ontView)
            ).render
          }
        }
        case WordLookup => {
          dom.alert("WordLookup")
        }
        case _ => dom.alert("unknown")
      }
    }

    def ontView : String => Unit = (inp : String) => {
      getComments("o:%s".format(inp))
      Client[Api].getOnt(inp).call().foreach { result =>
        outputBox.innerHTML = ""
        outputBox.appendChild(
          result.map(OntItemRender(_, ontView, listView)).getOrElse(div().render)
        ).render
      }
    }

    def getComments(name : String) = {
      Client[Api].getComments(name).call().foreach{ result =>
        comments.innerHTML = ""
        comments.appendChild(
          result.map(CommentRender(_)).render
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
