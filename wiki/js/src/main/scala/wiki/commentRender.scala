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

  //add a a submit button
  def apply(c : Comment, update : (Comment) => Unit) : org.scalajs.dom.raw.Node = {
    val comment = div.render
    val inputBox = textarea(cls := "materialize-textarea", `type`:="text", id:=c.uuid).render
    inputBox.value = c.body

    GithubMarkdown.convert(c.body, comment)
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
        val newcomment = c.copy(body = inputBox.value)
        update(newcomment)
        GithubMarkdown.convert(newcomment.body, comment)
        false
      }
    }

    div(cls := "card %s white-text".format(Colors.commentBg))(
      div(cls := "card-content")(
        div(cls := "card-title row")(div(cls := "col s10")(c.author + " says:"), div(cls := "col s2")(a(cls := "btn btn-floating right-align", onclick := { () => {
            comment.innerHTML = ""
            comment.appendChild(editBody)
          }
        })(i(cls := "material-icons")("add")))),
        content
      )
    ).render
  }
}
