package MostContestedApi

import org.specs2.mutable.Specification

class RouterSpec extends Specification {

  "#apply" >> {

    "any non matching path should return None" >> {
      val paths = List(
        "/a-path",
        "/anotherPath",
        "anotherPath/23234",
        "/most_contested",
        "/most_contested/some-string",
        "most_contested/456",
        "/most_contested/123/suffix",
        "prefix/most_contested/123"
      )

      paths.map {
        case p@Router(activityId) => ko(s"Path $p shouldn't have been matched")
        case _ => ok
      }
    }

    "any matching path should return activityId" >> {
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
}
