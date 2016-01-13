package mostcontestedapi

import com.twitter.finagle.http.Status
import com.twitter.util.{Await, Future}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class ControllerSpec extends Specification with Mockito {

  "#get" >> {

    trait Context extends Scope {
      val activityId = 872124L
      val apiClientMock = smartMock[ApiClient]
      val controller = new Controller(apiClientMock)
    }

    "returns 404 when activity doesn't have any segments" in new Context {

      apiClientMock.getActivitySegments(activityId) returns Future.value(List.empty)
      val resp = Await.result(controller.get(activityId))
      resp.status ==== Status.NotFound
    }

    "returns the segmentId when activity has only one segment" in new Context {
      val segmentId = 12343L

      apiClientMock.getActivitySegments(activityId) returns Future.value(List(segmentId))
      val resp = Await.result(controller.get(activityId))
      resp.status ==== Status.Ok
      resp.contentString ==== "12343"
    }

    "returns the most contested segmentId when activity has only multiple segments" in new Context {
      val segmentIds = List(12343L, 546534L, 1232L)
      val effortCounts = List(1224L, 1549923L, 12399L)
      val segmentEffortPairs = segmentIds.zip(effortCounts).map {
        case (segId, effCount) =>
          SegmentIdEffortPair(segId, effCount)
      }

      apiClientMock.getActivitySegments(activityId) returns Future.value(segmentIds)
      apiClientMock.getSegmentEffortCounts(segmentIds) returns Future.value(segmentEffortPairs)

      val resp = Await.result(controller.get(activityId))
      resp.status ==== Status.Ok
      resp.contentString ==== "546534"
    }

  }
}
