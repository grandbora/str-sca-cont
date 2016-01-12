package MostContestedApi

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.{Await, Future}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class StravaClientSpec extends Specification with Mockito {

  trait Context extends Scope {
    val accessToken = "testToken"
    val finagleClientMock = smartMock[Service[Request, Response]]

    val activityId = 123L
    val request = Request(s"/api/v3/activities/$activityId")
    val requestGenerator: Long => Request =
      (activityId: Long) => request

    val stravaClient = new StravaClient(finagleClientMock, accessToken, requestGenerator)
  }

  "#getActivitySegments" >> {

    "returns empty list when finagle client returns a non 200 response" in new Context {
      finagleClientMock(request) returns Future.value(Response(Status.BadRequest))
      Await.result(stravaClient.getActivitySegments(1L)) ==== List.empty
    }

    "finagle client returns a 200 response" >> {

      "returns empty list when activity doesn't have a segment" in new Context {
        val stravaResp = Response(Status.Ok)
        stravaResp.setContentString(Fixtures.noSegmentActivity)

        finagleClientMock(request) returns Future.value(stravaResp)

        Await.result(stravaClient.getActivitySegments(1L)) ==== List.empty
      }

      "returns the segment ids of given activity" in new Context {
        val expectedSegmentIds = List(11038522478L, 11038522472L, 11038522481L)

        val stravaResp = Response(Status.Ok)
        stravaResp.setContentString(Fixtures.activity)

        finagleClientMock(request) returns Future.value(stravaResp)
        Await.result(stravaClient.getActivitySegments(activityId)) ==== expectedSegmentIds
      }
    }
  }
}
