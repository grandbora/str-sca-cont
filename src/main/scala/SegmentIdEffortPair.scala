package mostcontestedapi

case class SegmentIdEffortPair(segmentId: Long, effortCount: Long)

object SegmentIdEffortPair {
  def findMostContested (list: List[SegmentIdEffortPair]): SegmentIdEffortPair =
    list match {
      case lastSurviving :: Nil => lastSurviving
      case first :: second :: tail => findMostContested((if(first.effortCount > second.effortCount) first else second) :: tail)
    }
}
