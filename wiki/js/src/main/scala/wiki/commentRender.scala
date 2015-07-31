package strips.ontology
import wiki._
import scalajs.concurrent.JSExecutionContext.Implicits.runNow
import org.scalajs.dom

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

object CommentRender {
  import scalatags.JsDom._
  import scalatags.JsDom.all._



  //update : (String, Option[String]) => Unit
  def apply(c : String, token : Option[String], update : (String, String) => Unit, name : String = "Wiki") : org.scalajs.dom.raw.Node = {
    //dom.alert("loading: %s".format(c))
    val comment = div.render
    val inputBox = textarea(cls := "materialize-textarea", `type`:="text", id:="%sEdit".format(name)).render
    inputBox.value = c

    GithubMarkdown.convert(c, comment)
    val editBody = form(
      div(cls := "input-field")(
        inputBox
      ),
      button(`type` := "submit", cls := "btn %s".format(Colors.defaultBtn))("submit")
    ).render
    val content = div(
      comment
    ).render

    editBody.onsubmit = {e : dom.Event => {
      val newcomment = inputBox.value
      update(c, newcomment)
      false
    }
  }

  div(cls := "card %s white-text".format(Colors.commentBg))(
    div(cls := "card-content")(

      div(cls := "card-title row")(div(cls := "col s10")("%s".format(name)), div(cls := "col s2")(
        token match {
          case Some(token) => a(cls := "btn btn-floating right-align", onclick := { () => {
            comment.innerHTML = ""
            comment.appendChild(editBody)
          }
        }
      )(i(cls := "material-icons")("add"))
      case None => a.render
    }
  )),
  content
)
).render
}
}
