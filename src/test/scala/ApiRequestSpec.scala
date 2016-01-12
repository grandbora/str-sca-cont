package MostContestedApi

import org.specs2.mutable.Specification

class ApiRequestSpec extends Specification {


  "#apply" >> {

    "returns a request object ready to be issued to strava api for the given activity id" >> {
      val accessToken = "testAccessToken"
      val apiRequest = new ApiRequest(accessToken)

      val activityId = 345657234L
      val actual = apiRequest.activityDetailRequest(activityId)
      actual.path ==== s"/api/v3/activities/$activityId"
      actual.headerMap must haveKey("Authorization")
      actual.headerMap("Authorization") ==== s"Bearer $accessToken"
    }


  }
}
