package mostcontestedapi

import org.specs2.mutable.Specification

class RouterSpec extends Specification {
  
    "doesn't match, non-matching paths" >> {
      val paths = List(
        "/a-path",
        "/anotherPath",
        "anotherPath/23234",
        "/most_contested",
        "/most_contested/some-string",
        "most_contested/456",
        "/most_contested/456/",
        "/most_contested/123/suffix",
        "prefix/most_contested/123"
      )

      paths.map {
        case p@Router(activityId) => ko(s"Path $p shouldn't have been matched")
        case _ => ok
      }
    }

    "doersn't match when activity id is greater than a Long" >> {
      val path = "/most_contested/123324453464545654667868979764563423123123123324"

      path match {
        case p@Router(activityId) => ko(s"Path $p shouldn't have been matched")
        case _ => ok
      }
    }

    "returns activityId when path is matched" >> {
      val paths = Map(
        "/most_contested/123" -> 123L,
        "/most_contested/123456789" -> 123456789L,
        "/most_contested/4557645" -> 4557645L,
        "/most_contested/1234567823543639" -> 1234567823543639L
      )

      paths.map {
        case (Router(actual), expected) => actual ==== expected
        case (path, _) => ko(s"was expecting to match path $path")
      }

      ok
    }
}
