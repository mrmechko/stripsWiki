package strips.ontology
import wiki._
import scalajs.concurrent.JSExecutionContext.Implicits.runNow


object CommentRender {
  import scalatags.JsDom._
  import scalatags.JsDom.all._

  def apply(c : Comment) : org.scalajs.dom.raw.Node = {
    val comment = div.render
    GithubMarkdown.convert(c.body, comment)

    div(cls := "card blue white-text")(
      div(cls := "card-content")(
        div(c.author),
        div(
          comment
        )
      )
    ).render
  }
}
