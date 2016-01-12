package mostcontestedapi

import org.specs2.mutable.Specification

class ApiRequestSpec extends Specification {

  "#apply" >> {

    "returns a request object ready to be issued to strava api for the given activity id" >> {
      val accessToken = "testAccessToken"
      val testPort = 123
      val testHost = "testhost"
      val apiRequest = new ApiRequest(testHost, testPort, accessToken)

      val activityId = 345657234L
      val req = apiRequest.activityDetailRequest(activityId)

      req.host must beSome(s"$testHost:$testPort")
      req.path ==== s"/api/v3/activities/$activityId"
      req.headerMap must haveKey("Authorization")
      req.headerMap("Authorization") ==== s"Bearer $accessToken"
    }
  }
}
