package MostContestedApi

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.{Http => HttpCodec, Request, Response, Status}
import com.twitter.util.{Await, Future}


object MostContestedApi extends App {

  println("starting web server")

  val accessToken = sys.props.get("access_token")
    .getOrElse(throw new IllegalArgumentException("access_token must be set as a system property"))
  val apiHost = "www.strava.com"
  val apiPort = 443

  val apiRequest = new ApiRequest(apiHost, apiPort, accessToken)

  val finagleClient =
    ClientBuilder()
      .codec(HttpCodec())
      .hosts(s"$apiHost:$apiPort")
      .tls(apiHost)
      .hostConnectionLimit(10)
      .build()

  val apiClient = new ApiClient(finagleClient, accessToken, apiRequest.activityDetailRequest)

  val service = new Service[Request, Response] {
    def apply(req: Request): Future[Response] = {

      req.path match {
        case Router(activityId) =>
          apiClient.getActivitySegments(activityId).map {
            segmentIds =>
              val r = Response(req.version, Status.Ok)
              r.setContentString(s"segment ids : $segmentIds")
              r
          }
        case _ =>
          Future.value(Response(req.version, Status.BadRequest))
      }
    }
  }

  val server = Http.serve(":8080", service)
  Await.ready(server)
}
