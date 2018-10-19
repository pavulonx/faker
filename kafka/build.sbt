lazy val kafka = (project in file("."))
  .settings(
    organization := "cf.jrozen",
    name := "kafka",
    version := "0.0.1-SNAPSHOT",

    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(
      "org.scodec" %% "scodec-bits" % "1.1.6",
      "com.spinoco" %% "fs2-kafka" % "0.2.0",
    )
  )



