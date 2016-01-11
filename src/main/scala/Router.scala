package MostContestedApi

object Router {

  private val PathPattern = """/most_contested/(\d+)""".r

  def unapply(path: String): Option[Long] = {

    path match {
      case PathPattern(a) =>Some(a.toLong)
      case _ => None
    }
  }
}
