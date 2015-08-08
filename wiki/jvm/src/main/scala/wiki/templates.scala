package wiki

object Template{
  import scalatags.Text.all._
  import scalatags.Text.tags2.title
  import scalatags.Text.svgTags.svg
  import scalatags.Text.svgAttrs.{width, height}
  import wiki.Graph._

  val materializecss = link(rel :="stylesheet", href:=BaseUrl()+"/css/materialize.min.css")
  val materializejs = script(src := BaseUrl()+"/js/materialize.min.js")
  val markedown = script(src := BaseUrl()+"/js/marked.min.js")
  val listjs = script(src := BaseUrl()+"/js/list.min.js")
  val jquery = script(src := BaseUrl()+"/js/jquery-2.1.4.min.js")
  val materialicons = link(href:="https://fonts.googleapis.com/icon?family=Material+Icons", rel:="stylesheet")

  val login =
    "<!DOCTYPE html>" +
    html(
      head(
        title("Trips Wiki"),
        jquery,
        //link(rel :="stylesheet", href:="http://yui.yahooapis.com/pure/0.6.0/pure-min.css"),
        materializecss,
        materializejs,
        materialicons,
        meta(name :="viewport", content :="width=device-width, initial-scale=1"),
        meta(httpEquiv:="Content-Type", content:="text/html; charset=UTF-8"),
        script(`type`:="text/javascript", src:=BaseUrl()+"/client-fastopt.js")
      ),
      body(cls := "%s %s".format(Colors.bodyColor, Colors.bodyText), style:="height:100%;")(
        script("wiki.ScalaJSwiki().login()")
      )
    )

  val txt =
    "<!DOCTYPE html>" +
    html(
      head(
        title("Trips Wiki"),
        jquery,
        //link(rel :="stylesheet", href:="http://yui.yahooapis.com/pure/0.6.0/pure-min.css"),
        materializecss,
        materializejs,
        materialicons,
        listjs,
        meta(name :="viewport", content :="width=device-width, initial-scale=1"),
        meta(httpEquiv:="Content-Type", content:="text/html; charset=UTF-8"),
        script(`type`:="text/javascript", src:=BaseUrl()+"/client-fastopt.js")
      ),
      body(cls := "%s %s".format(Colors.bodyColor, Colors.bodyText))(
        script("wiki.ScalaJSwiki().main()")
      )
    )
    def buildGraph(word : String, graph : GGraph) = {
      "<!DOCTYPE html>" +
      html(
        head(
          script(src:=BaseUrl()+"/js/dagre-d3.min.js"),
          script(src:=BaseUrl()+"/js/d3.v3.min.js", charset:="utf-8")
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
