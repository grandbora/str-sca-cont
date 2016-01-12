package MostContestedApi

import com.twitter.finagle.http.Request

class ApiRequest(accessToken: String) {

  def activityDetailRequest(activityId: Long): Request = {
    val req = Request(s"/api/v3/activities/$activityId")
    req.headerMap.set("Authorization", s"Bearer $accessToken")
    req
  }
}
