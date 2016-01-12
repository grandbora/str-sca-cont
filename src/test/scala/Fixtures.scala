package MostContestedApi

object Fixtures {

  val noSegmentActivity = readFixture("no_segment_activity")

  val activity = readFixture("activity")

  private def readFixture(fileName: String) =
    scala.io.Source.fromFile(s"src/test/resources/$fileName.json").mkString

}
