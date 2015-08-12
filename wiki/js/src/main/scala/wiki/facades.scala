package wiki
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode
import scala.concurrent.Future
import scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scalatags.JsDom.all._
import scalatags.JsDom._
import upickle.default._
import autowire._


import org.scalajs.jquery.JQuery

trait Materialize extends JQuery {
  def material_select(): this.type = ???
  //def collapsible(options : js.Object) : this.type = ???
}

object Materialize {
  implicit def jq2materialize(jq: JQuery): Materialize =
    jq.asInstanceOf[Materialize]
}
