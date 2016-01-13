package mostcontestedapi

import java.net.URL

import com.twitter.finagle.http.{Request, RequestBuilder}

class ApiRequest(host: String, port: Int, accessToken: String) {

  def activityRequest(activityId: Long): Request =
    makeRequest("activities", activityId)

  def segmentRequest(segmentId: Long): Request =
    makeRequest("segments", segmentId)

  private def makeRequest(resource: String, id: Long): Request = {
    RequestBuilder()
      .url(new URL("https", host, port, s"/api/v3/$resource/$id"))
      .setHeader("Authorization", s"Bearer $accessToken")
      .buildGet()
  }
}
