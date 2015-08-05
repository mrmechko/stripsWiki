package wiki

object LoggerForms {
  """
  {
    "type": "rename-ont",
    "comment" : [comment],
    "for": [version],
    "data": {
      "orig": [original-name],
      "new" : [new-name]
    },
    "for": [version]
  }
  """
  import wiki.Colors

  import scalatags.JsDom._
  import scalatags.JsDom.all._
  val rename : (String) => org.scalajs.dom.raw.Node = (node : String) => {
    val original = input.render
    val current = input.render
    def loginHandler = () => ()
    form(style := "height:100%;width:100%;", onsubmit := loginHandler)(
      div(cls:="row %s".format(Colors.sidebar))(
        div(cls := "col s4")(
          div()
        ),
        div(cls := "col s4")(
          div(cls:="input-field row")(label("original name"),original),
          div(cls:="input-field row")(label("new name"),current),
          div(cls:="input-field row")(button(cls :="waves-effect waves-light btn %s".format(Colors.defaultBtn), `type` := "submit")("login"))
        )
      )
    ).render
  }

}
