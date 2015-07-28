package wiki

object Template{
  import scalatags.Text.all._
  import scalatags.Text.tags2.title
  import scalatags.Text.svgTags.svg
  import scalatags.Text.svgAttrs.{width, height}
  import wiki.Graph._


  val txt =
    "<!DOCTYPE html>" +
    html(
      head(
        title("Example Scala.js application"),
        script(src := "https://code.jquery.com/jquery-2.1.4.min.js"),
        //link(rel :="stylesheet", href:="http://yui.yahooapis.com/pure/0.6.0/pure-min.css"),
        link(rel :="stylesheet", href:="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.0/css/materialize.min.css"),
        script(src := "https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.0/js/materialize.min.js"),
        script(src :="//cdnjs.cloudflare.com/ajax/libs/list.js/1.1.1/list.min.js"),
        meta(name :="viewport", content :="width=device-width, initial-scale=1"),
        meta(httpEquiv:="Content-Type", content:="text/html; charset=UTF-8"),
        script(`type`:="text/javascript", src:="/client-fastopt.js"),
        link(href:="https://fonts.googleapis.com/icon?family=Material+Icons", rel:="stylesheet")
        //script(`type`:="text/javascript", src:="//localhost:12345/workbench.js")
        //link(
        //  rel:="stylesheet",
        //  `type`:="text/css",
        //  href:="META-INF/resources/webjars/bootstrap/3.2.0/css/bootstrap.min.css"
        //)
      ),
      body(cls := "%s %s".format(Colors.bodyColor, Colors.bodyText))(
        script("wiki.ScalaJSwiki().main()")
      )
    )
    def buildGraph(word : String, graph : GGraph) = {
      "<!DOCTYPE html>" +
      html(
        head(
          script(src:="http://cpettitt.github.io/project/dagre-d3/latest/dagre-d3.min.js"),
          script(src:="http://d3js.org/d3.v3.min.js", charset:="utf-8")
        ),
        body(
          raw(
            """
            <style id="css">
            g.type-TK > rect {fill: #00ffd0;}
            text {font-weight: 300;font-family: "Helvetica Neue", Helvetica, Arial, sans-serf;font-size: 14px;}
            .node rect {stroke: #999;fill: #fff;stroke-width: 1.5px;}
            .edgePath path {stroke: #333;stroke-width: 1.5px;}</style>
            """
          ),
          svg(id:="svg-canvas", style:="width:100%;height:100%;position:fixed;top:0;left:0;bottom:0;right:0;")(raw("<g/>")),
          script(raw(
            //GGraph.makegscript(Server.mgfw(word))
            graph.script
          ))
        )
      )
    }
}
