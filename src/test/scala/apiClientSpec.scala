package mostcontestedapi

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.{Await, Future}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class ApiClientSpec extends Specification with Mockito {

  trait Context extends Scope {
    val accessToken = "testToken"
    val finagleClientMock = smartMock[Service[Request, Response]]

    val unusedRequest = (activityId: Long) => ???
  }

  "#getActivitySegments" >> {

    trait ActivityContext extends Context {
      val activityId = 123L
      val request = Request(s"/api/v3/activities/$activityId")
      val activityRequest: Long => Request =
        (activityId: Long) => request

      val stravaClient = new ApiClient(finagleClientMock, accessToken, activityRequest, unusedRequest)
    }

    "returns empty list when finagle client returns a non 200 response" in new ActivityContext {
      finagleClientMock(request) returns Future.value(Response(Status.BadRequest))
      Await.result(stravaClient.getActivitySegments(1L)) ==== List.empty
    }

    "finagle client returns a 200 response" >> {

      "returns empty list when activity doesn't have a segment" in new ActivityContext {
        val stravaResp = Response(Status.Ok)
        stravaResp.setContentString(Fixtures.noSegmentActivity)

        finagleClientMock(request) returns Future.value(stravaResp)

        Await.result(stravaClient.getActivitySegments(1L)) ==== List.empty
      }

      "returns the segment ids of given activity" in new ActivityContext {
        val expectedSegmentIds = List(8536675L, 8766845L, 8229076L)

        val stravaResp = Response(Status.Ok)
        stravaResp.setContentString(Fixtures.activity)

        finagleClientMock(request) returns Future.value(stravaResp)
        Await.result(stravaClient.getActivitySegments(activityId)) ==== expectedSegmentIds
      }
    }
  }

  "#getSegmentEffortCounts" >> {

    trait SegmentContext extends Context {

      val segmentId = 324L
      val request = Request(s"/api/v3/segments/$segmentId")
      val segmentRequest: Long => Request =
        (activityId: Long) => request

      val stravaClient = new ApiClient(finagleClientMock, accessToken, unusedRequest, segmentRequest)
    }

    "single segment" >> {

      "returns 0 when finagle client returns a non 200 response" in new SegmentContext {

        val stravaResp = Response(Status.InternalServerError)
        finagleClientMock.apply(request) returns Future.value(stravaResp)

        val result = Await.result(stravaClient.getSegmentEffortCounts(List(segmentId)))
        result must haveSize(1)
        result.head.segmentId ==== 324L
        result.head.effortCount ==== 0L
      }

      "returns effort count when finagle client returns a 200 response" in new SegmentContext {

        val stravaResp = Response(Status.Ok)
        stravaResp.setContentString(Fixtures.segment)
        finagleClientMock.apply(request) returns Future.value(stravaResp)

        val result = Await.result(stravaClient.getSegmentEffortCounts(List(segmentId)))
        result must haveSize(1)
        result.head.segmentId ==== 324L
        result.head.effortCount ==== 663L
      }
    }
  }
}
