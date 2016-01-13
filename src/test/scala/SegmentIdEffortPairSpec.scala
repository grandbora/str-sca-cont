package mostcontestedapi

import org.specs2.mutable.Specification

class SegmentIdEffortPairSpec extends Specification {

  "#findMostContested " >> {

    "finds the most contested Segment out of the list" >> {

      val inputsAndAnswerIndex = Map(
        List(SegmentIdEffortPair(123L, 543L), SegmentIdEffortPair(325L, 9999L)) -> 1,
        List(SegmentIdEffortPair(123L, 1543L), SegmentIdEffortPair(325L, 99L)) -> 0,
        List(SegmentIdEffortPair(123L, 1543L), SegmentIdEffortPair(32L, 99L), SegmentIdEffortPair(12325L, 9234239L)) -> 2,
        List(SegmentIdEffortPair(123L, 1543L), SegmentIdEffortPair(32L, 99L), SegmentIdEffortPair(12325L, 231L)) -> 0,
        List(SegmentIdEffortPair(123L, 123L), SegmentIdEffortPair(32L, 2345534L), SegmentIdEffortPair(12325L, 231L), SegmentIdEffortPair(35L, 9877L)) -> 1
      )

      inputsAndAnswerIndex.foreach {
        case (segments, answerIndex) =>
          SegmentIdEffortPair.findMostContested(segments) ==== segments(answerIndex)
      }

      ok
    }
  }
}
