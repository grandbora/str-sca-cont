package MostContestedApi

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.{Future, Try}

import scala.util.parsing.json.{JSON, JSONArray, JSONObject}

class StravaClient(client: Service[Request, Response],
                   accessToken: String,
                   requestGenerator: Long => Request
                  ) {

  type StringMap = Map[String, Any]

  def getActivitySegments(activityId: Long): Future[List[Long]] = {
    val req = requestGenerator(activityId)

    client(req).map {
      resp =>
        resp.status match {
          case Status.Ok => parseSegmentIds(resp.contentString)
          case _ => List.empty
        }
    }
  }

  private def parseSegmentIds(jsonString: String): List[Long] = {
    JSON.parseRaw(jsonString) match {
      case Some(JSONObject(activityAttrs: StringMap)) =>
        activityAttrs.get("segment_efforts") match {
          case Some(JSONArray(segmentEfforts: List[Any])) =>
            segmentEfforts.collect {
              case JSONObject(segmentEffortAttrs) =>
                segmentEffortAttrs.get("id") match {
                  case Some(id) => Try(id.asInstanceOf[Double].toLong).toOption
                  case _ => None
                }
            }.flatten
          case _ => List.empty
        }
      case _ => List.empty
    }
  }
}