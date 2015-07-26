package wiki

import strips.ontology._

trait Api{
  def list(path: String): Seq[String]
  def getOnt(name : String) : Option[SOntItem]
  def getComments(name : String) : List[Comment]
  def getWordFromWN(word : String) : List[String]
}


case class Comment(author : String, body : String)

case object OntLookup
case object WordLookup
case object SenseLookup
