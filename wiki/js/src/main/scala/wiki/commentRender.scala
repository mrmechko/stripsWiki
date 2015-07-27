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
    val inputBox = textarea(value := c.body).render

    GithubMarkdown.convert(c.body, comment)
    val edit = form(cls := "pure-form")(
      fieldset(
        legend("search ontology"),
        inputBox,
        button(`type` := "submit", cls := "pure-button pure-button-primary")("submit")
      )
    ).render
    val content = div(
      ondblclick := { () => {
          comment.innerHTML = ""
          comment.appendChild(edit)
        }
      },
      comment
    ).render

    edit.onsubmit = {e : dom.Event => {
        val newcomment = c.copy(body = inputBox.value)
        update(newcomment)
        GithubMarkdown.convert(newcomment.body, comment)
        false
      }
    }

    div(cls := "card blue white-text")(
      div(cls := "card-content")(
        div(c.author),
        content
      )
    ).render
  }
}
