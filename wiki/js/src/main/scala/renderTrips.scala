package strips.ontology

import wiki.SenseLookup
import wiki.WordLookup
import wiki.OntLookup

import wiki.Colors

import scalatags.JsDom._
import scalatags.JsDom.all._

object ListOntItemRender {
  def apply(onts : List[String], change : String => Unit) = {
    div(
      for (o <- onts) yield p(onclick := {() => change(o)})(o)
    )
  }.render

  def apply(onts : (List[String], List[String]), change : String => Unit) = {

    div(cls := "row")(
      div(cls := "col s6")(
        for (o <- onts._1) yield p(onclick := {() => change(o)})(o)
      ),
      div(cls := "col s6")(
        for (o <- onts._2) yield p(onclick := {() => change(o)})(o)
      )
    )
  }.render
}

object OntItemRender {

  def apply(o : SArgument) : org.scalajs.dom.raw.Node = span(o.role).render
  def apply(o : SSem) : org.scalajs.dom.raw.Node = span(o.fltype).render
  def apply(o : SFeatureSet) : org.scalajs.dom.raw.Node = {
    ul(cls := "sfeatureset")(
      for (f <- o.feats.toList) yield li(cls := "sfeature")(span(cls := "sfeaturename")(f._1), span(cls := "sfeatureval")(f._2))
    )
  }.render
  def apply(o : SOntItem, change : String => Unit, list : (String, Any) => Unit) : org.scalajs.dom.raw.Node = {

    div(
      h1(style := "font-style:condensed;font-weight:200;")(o.name),
      div()(
        h3(style := "display:inline-block;font-style:condensed;font-weight:200;")("Parent"),
        h4(style:= "margin-left:1em;display:inline-block;font-style:bold;",onclick := {() => change(o.parent)})(o.parent)
      ),
      buildList(o.children, "Children", (s : String) =>{() => list(s, OntLookup)}, "white", Colors.accent2),
      buildList(o.words, "Lexicon", (s : String) =>{() => list(s, WordLookup)}, "white", Colors.accent1),

      buildList(o.wn, "WordNet", (s : String) =>{() => list(s, SenseLookup)}, "white", "blue darken-4"),
      div(apply(o.sem)),
      div(id := "sontArgList")(
        div(cls := "pure-menu pure-menu")(
          a(cls:="pure-menu-heading")("Arguments")
        ),
        ul(cls := "sontSargs list pure-menu-list")(
          for (a <- o.arguments) yield li(cls := "sontSarg pure-menu-item")(apply(a))
        )
      )
    )
  }.render

  def buildList(elements : List[String], key : String, callback : (String) => () => Unit, tagcolor : String = "white", contcolor : String = "white") = {
    val clsMod2 = if (contcolor == "white") ""; else " white-text"

    if(elements.size == 0) div().render
    else
    div(cls:="card %s".format(contcolor))(
      div(cls:="card-content%s".format(clsMod2))(
        div(id := "%sList".format(key))(
          div(
            div(cls:="row card-title")(
              div(cls := "col s10")(
                span(cls:="left-align")(key)
              ),
              div(cls := "col s2")(
                a(cls := "right-align btn-floating waves-effect waves-dark btn sort %s".format(Colors.defaultBtn), "data-sort".attr := key)(
                  i(cls := "material-icons")("sort_by_alpha")
                ))
              ),
              div(cls:="row input-field")(input(`type` := "text", cls := "search", placeholder:="filter", id := "%sFilter".format(key)))
            ),
            ul(cls := "sontWN list")(
              for (w <- elements) yield li(style := "display:inline-block;padding:0.25em;margin:0.25em;border:1px solid %s;".format(tagcolor))(a(cls := "%s%s".format(key,clsMod2), onclick := {callback(w)})(w))
            ),
            script(
              raw(
                "var %sListOptions = {valueNames: [ '%s' ]};\n".format(key,key) +
                "var %sList = new List('%sList', %sListOptions);".format(key, key, key)
              )
            )
          )
        )
      ).render
    }
  }
