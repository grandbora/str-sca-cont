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

    val unusedRequest = (id: Long) => ???
  }

  "#getActivitySegments" >> {

    trait ActivityContext extends Context {
      val activityId = 123L
      val request = Request(s"/api/v3/activities/$activityId")
      val activityRequest: Long => Request =
        (id: Long) => {
          id ==== activityId
          request
        }

      val stravaClient = new ApiClient(finagleClientMock, accessToken, activityRequest, unusedRequest)
    }

    "returns empty list when finagle client returns a non 200 response" in new ActivityContext {
      finagleClientMock(request) returns Future.value(Response(Status.BadRequest))
      Await.result(stravaClient.getActivitySegments(activityId)) ==== List.empty
    }

    "finagle client returns a 200 response" >> {

      "returns empty list when activity doesn't have a segment" in new ActivityContext {
        val stravaResp = Response(Status.Ok)
        stravaResp.setContentString(Fixtures.noSegmentActivity)

        finagleClientMock(request) returns Future.value(stravaResp)

        Await.result(stravaClient.getActivitySegments(activityId)) ==== List.empty
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

    "single segment" >> {

      trait SingleSegmentContext extends Context {

        val segmentId = 324L
        val request = Request(s"/api/v3/segments/$segmentId")
        val segmentRequest: Long => Request =
          (id: Long) => {
            id ==== segmentId
            request
          }

        val stravaClient = new ApiClient(finagleClientMock, accessToken, unusedRequest, segmentRequest)
      }

      "returns 0 when finagle client returns a non 200 response" in new SingleSegmentContext {

        val stravaResp = Response(Status.InternalServerError)
        finagleClientMock.apply(request) returns Future.value(stravaResp)

        val result = Await.result(stravaClient.getSegmentEffortCounts(List(segmentId)))
        result must haveSize(1)
        result.head.segmentId ==== 324L
        result.head.effortCount ==== 0L
      }

      "returns effort count when finagle client returns a 200 response" in new SingleSegmentContext {

        val stravaResp = Response(Status.Ok)
        stravaResp.setContentString(Fixtures.segment)
        finagleClientMock.apply(request) returns Future.value(stravaResp)

        val result = Await.result(stravaClient.getSegmentEffortCounts(List(segmentId)))
        result must haveSize(1)
        result.head.segmentId ==== 324L
        result.head.effortCount ==== 663L
      }
    }

    "multiple segments" >> {

      "returns each segment's effort count" in new Context {

        def makeRequest(id: Long) = Request(s"/api/v3/segments/$id")

        val nonExistentSegment = 564556L
        val segmentIds = List(8229076L, 8766845L, nonExistentSegment, 8536675L)
        val expectedEffortCounts = List(367L, 1654L, 0L, 663L)

        val segmentIdsToEffortCounts = segmentIds.zip(expectedEffortCounts).toMap
        val segmentIdsToRequest = segmentIds.map(id => id -> makeRequest(id)).toMap

        val segmentRequest = (id: Long) => segmentIdsToRequest(id)
        val stravaClient = new ApiClient(finagleClientMock, accessToken, unusedRequest, segmentRequest)


        segmentIds.foreach {
          segmentId =>

            val stravaResp = if (segmentId == nonExistentSegment) {
              Response(Status.NotFound)
            } else {
              val r = Response(Status.Ok)
              r.setContentString(Fixtures.readFixture(s"segment_$segmentId"))
              r
            }

            finagleClientMock(segmentIdsToRequest(segmentId)) returns Future.value(stravaResp)
        }

        val result = Await.result(stravaClient.getSegmentEffortCounts(segmentIdsToEffortCounts.keys.toList))
        result must haveSize(4)
        result.map(_.segmentId).toSet ==== segmentIdsToEffortCounts.keySet

        result.map {
          case SegmentIdEffortPair(segmentId, effortCount) =>
            segmentIdsToEffortCounts(segmentId) ==== effortCount
        }
      }
    }
  }
}
