package MostContestedApi

import java.net.URL

import com.twitter.finagle.http.{Request, RequestBuilder}

class ApiRequest(host: String, port: Int, accessToken: String) {

  def activityDetailRequest(activityId: Long): Request = {
    RequestBuilder()
      .url(new URL("https", host, port, s"/api/v3/activities/$activityId"))
      .setHeader("Authorization", s"Bearer $accessToken")
      .buildGet()
  }
}
