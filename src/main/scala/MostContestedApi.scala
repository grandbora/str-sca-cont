package MostContestedApi

import com.twitter.finagle.{Http, Service, http}
import com.twitter.util.{Await, Future}

object MostContestedApi extends App {

  println("starting web server")

  val accessToken = sys.props.get("access_token")
    .getOrElse(throw new IllegalArgumentException("access_token must be set as a system property"))

  val service = new Service[http.Request, http.Response] {
    def apply(req: http.Request): Future[http.Response] = {

      val resp = req.path match {
        case Router(id) =>
          val r = http.Response(req.version, http.Status.Ok)
          r.setContentString("I received a valid request")
          r

        case _ =>
          http.Response(req.version, http.Status.BadRequest)
      }

      Future.value(resp)
    }
  }

  val server = Http.serve(":8080", service)
  Await.ready(server)
}
