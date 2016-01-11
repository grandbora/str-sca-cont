package MostContestedApi

import com.twitter.finagle.{Http, Service, http}
import com.twitter.util.{Await, Future}

object MostContestedApi extends App {

  println("starting web server")

  val service = new Service[http.Request, http.Response] {
    def apply(req: http.Request): Future[http.Response] = {
      val resp = http.Response(req.version, http.Status.Ok)
      resp.setContentString("I am alive as a web server")
      Future.value(resp)
    }
  }

  val server = Http.serve(":8080", service)
  Await.ready(server)
}
