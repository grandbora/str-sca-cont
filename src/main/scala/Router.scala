package mostcontestedapi

import com.twitter.util.Try

object Router {

  private val PathPattern = """/most_contested/(\d+)""".r

  def unapply(path: String): Option[Long] = {

    path match {
      case PathPattern(a) =>Try(a.toLong).toOption
      case _ => None
    }
  }
}
