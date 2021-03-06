package mostcontestedapi

object Fixtures {

  val noSegmentActivity = readFixture("no_segment_activity")

  val activity = readFixture("activity")

  val segment = readFixture("segment_8536675")

  def readFixture(fileName: String) =
    scala.io.Source.fromFile(s"src/test/resources/$fileName.json").mkString

}
