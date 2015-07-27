package strips.ontology

import wiki.SenseLookup

import scalatags.JsDom._
import scalatags.JsDom.all._

object ListOntItemRender {
  def apply(onts : List[String], change : String => Unit) = {
    div(
      for (o <- onts) yield p(onclick := {() => change(o)})(o)
    )
  }.render

  def apply(onts : (List[String], List[String]), change : String => Unit) = {
    div(cls := "pure-g")(
      div(cls := "pure-u-1-2")(
        for (o <- onts._1) yield p(onclick := {() => change(o)})(o)
      ),
      div(cls := "pure-u-1-2")(
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
    div(cls := "sont")(
      h1(cls := "sontName")(o.name),
      div(cls := "pure-menu pure-menu-horizontal")(
        a(cls:="pure-menu-heading")("Parent"),
        span(onclick := {() => change(o.parent)})(o.parent)
      ),
      //h2(cls := "sontParent")("parent: %s".format(o.parent)),
      div(id := "childList")(
        div(cls := "pure-menu")(
          a(cls:="pure-menu-heading")("children"),
          span(
            input(cls :="search"),
            a(cls :="sort pure-button", "data-sort".attr :="childName")("Sort")
          )
        ),
        ul(cls := "sontChildren list pure-menu-list")(
          for (c <- o.children) yield li(cls := "sontChild pure-menu-item")(
            a(cls := "childName pure-menu-link", onclick := {() => change(c)})(c)
          )
        ),
        script("""
          var childListOptions = {valueNames: [ 'childName' ]};
          var childList = new List('childList', childListOptions);
          """
        )
      ),
      div(apply(o.sem)),
      div(id := "sontArgList")(
        div(cls := "pure-menu pure-menu")(
          a(cls:="pure-menu-heading")("Arguments")
        ),
        ul(cls := "sontSargs list pure-menu-list")(
          for (a <- o.arguments) yield li(cls := "sontSarg pure-menu-item")(apply(a))
        )
      ),
      div(cls := "pure-g")(
        div(id := "wordList", cls := "pure-u-1-2")(
          div(cls := "pure-menu pure-menu")(
            a(cls:="pure-menu-heading")("Words"),
            span(
              input(cls :="search"),
              a(cls :="sort pure-button", "data-sort".attr :="word")("Sort")
            )
          ),
          ul(cls := "sontWords list pure-menu-list")(
            for (w <- o.words) yield li(cls := "sontWord pure-menu-item")(a(cls := "word pure-menu-link")(w))
          ),
          script("""
            var wordListOptions = {valueNames: [ 'word' ]};
            var wordList = new List('wordList', wordListOptions);
            """
          )
        ),
        div(id := "wordList", cls := "pure-u-1-2")(
          div(cls := "pure-menu pure-menu")(
            span(
              a(cls:="pure-menu-heading")("WordNet"),
              input(cls :="search")
            ),
          a(cls :="sort pure-button", "data-sort".attr :="word")("Sort")
          ),
          ul(cls := "sontWN list pure-menu-list")(
            for (w <- o.wn) yield li(cls := "sontWN pure-menu-item")(a(cls := "wordNet pure-menu-link", onclick := {() => list(w, SenseLookup)})(w))
          ),
          script("""
            var wordNetListOptions = {valueNames: [ 'wordNet' ]};
            var wordNetList = new List('wordNetList', wordNetListOptions);
            """
          )
        )
      )
    )
  }.render
}
