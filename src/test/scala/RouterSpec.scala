package MostContestedApi

import org.specs2.mutable.Specification

class RouterSpec extends Specification {
  
  "#apply" >> {

    "any non matching path should return None" >> {
      val path = "dunno/lol"

      path match {
        case Router(activityId) => ko
        case _ => ok
      }
    }
  }
}
