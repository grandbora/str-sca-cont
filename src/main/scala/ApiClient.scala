package mostcontestedapi

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.{Future, Try}

import scala.util.parsing.json.{JSON, JSONArray, JSONObject}

class ApiClient(client: Service[Request, Response],
                accessToken: String,
                activityRequest: Long => Request,
                segmentRequest: Long => Request
               ) {

  def getActivitySegments(activityId: Long): Future[List[Long]] = {
    val req = activityRequest(activityId)

    client(req).map {
      resp =>
        resp.status match {
          case Status.Ok =>
            parseSegmentIds(resp.contentString)
          case status =>
            println(s"Api returned $status response for activity request")
            List.empty
        }
    }
  }

  private def parseSegmentIds(jsonString: String): List[Long] = {
    JSON.parseRaw(jsonString) match {
      case Some(JSONObject(activityAttrs)) =>
        activityAttrs.get("segment_efforts") match {
          case Some(JSONArray(segmentEfforts: List[Any])) =>
            segmentEfforts.collect {
              case JSONObject(segmentEffortAttrs) =>
                segmentEffortAttrs.get("segment") match {
                  case Some(JSONObject(segmentAttr)) =>
                    segmentAttr.get("id") match {
                      case Some(id) => Try(id.asInstanceOf[Double].toLong).toOption
                      case _ => None
                    }
                  case _ => None
                }
            }.flatten
          case _ => List.empty
        }
      case _ => List.empty
    }
  }

  def getSegmentEffortCounts(segmentIds: List[Long]): Future[List[SegmentIdEffortPair]] = {

    val segmentQueries = segmentIds.map {
      segmentId =>
        val req = segmentRequest(segmentId)
        client(req).map(segmentId -> _)
    }

    Future.collect(segmentQueries).map(_.toList.map {
      case (segmentId, resp) =>
        val effortCount = resp.status match {
          case Status.Ok =>
            parseEffortCount(resp.contentString)

          case status =>
            println(s"Api returned $status response for segment request")
            0
        }

        SegmentIdEffortPair(segmentId, effortCount)
    })
  }

  private def parseEffortCount(jsonString: String): Long = {
    JSON.parseRaw(jsonString) match {
      case Some(JSONObject(activityAttrs)) =>
        activityAttrs.get("effort_count") match {
          case Some(effortCount) => Try(effortCount.asInstanceOf[Double].toLong).getOrElse(0)
          case _ => 0
        }
      case _ => 0
    }
  }
}

case class SegmentIdEffortPair(segmentId: Long, effortCount: Long)
