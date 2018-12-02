
import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.packager.Keys.{dockerExposedPorts, _}
import sbt.Def
import sbt.Keys._

object ComponentScalaDocker {

  val commonSettings: Seq[Def.Setting[_ <: Any]] = Seq(
    dockerBaseImage := "java:8",
    dockerUpdateLatest := true,
    defaultLinuxInstallLocation in Docker := s"/opt/${name.value}"
  )

  def apply[T](settings: Seq[Def.Setting[_ <: Any]] = List()): Seq[Def.Setting[_ <: Any]] = commonSettings ++ settings

  def apply[T](exposedPort: Int): Seq[Def.Setting[_ <: Any]] = apply(Seq(
    dockerExposedPorts := Seq(exposedPort),
  ))
}

object ApiDocker {
  val settings = ComponentScalaDocker(exposedPort = 8811)
}

object NotifierDocker {
  val settings = ComponentScalaDocker(exposedPort = 8822)
}

object HandlerDocker {
  val settings = ComponentScalaDocker(exposedPort = 8888)
}

object CallManagerDocker {
  val settings = ComponentScalaDocker()
}