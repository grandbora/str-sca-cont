package mostcontestedapi

import com.twitter.finagle.http.{Response, Status}
import com.twitter.util.Future

class Controller(apiClient: ApiClient) {

  def get(activityId: Long): Future[Response] = {

    apiClient.getActivitySegments(activityId).flatMap {

      case Nil =>
        Future.value(Response(Status.NotFound))

      case onlySegmentId :: Nil =>
        val r = Response(Status.Ok)
        r.setContentString(onlySegmentId.toString)
        Future.value(r)

      case multipleSegmentIds =>
        findMostContested(multipleSegmentIds).map {
          mostContested =>
            val r = Response(Status.Ok)
            r.setContentString(s"${mostContested.segmentId}")
            r
        }
    }
  }

  private def findMostContested(segmentIds: List[Long]): Future[SegmentIdEffortPair] = {
    apiClient.getSegmentEffortCounts(segmentIds).map(SegmentIdEffortPair.findMostContested)
  }
}
