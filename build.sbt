scalaVersion in ThisBuild := "2.12.6"
organization in ThisBuild := "cf.jrozen"

//val Http4sVersion = "0.18.20" //todo: upgrade to 0.20.x series
val Http4sVersion = "0.20.0-M1" //todo: upgrade to 0.20.x series
val Specs2Version = "4.2.0"
val LogbackVersion = "1.2.3"
val CirceVersion = "0.10.1"
val KafkaSerializationV = "0.3.16"
val fs2KafkaVersion = "0.16.0"
val fs2V = "1.0.0"
val PureConfigVersion = "0.9.2"


val Success = 0
val Error = 1

shellPrompt := { s => Project.extract(s).currentProject.id + " > " }

lazy val commonSettings = Seq(
  scalacOptions ++= commonScalacOptions(scalaVersion.value)
)

lazy val testDependencies = Seq(
  "org.specs2" %% "specs2-core" % Specs2Version % "test"
)
lazy val circeDependencies = Seq(
  "io.circe" %% "circe-core" % CirceVersion,
  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-parser" % CirceVersion
)

lazy val http4sDependencies = Seq(
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
)

lazy val model = (project in file("model"))
  .settings(moduleName := "model", name := "model")
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
    ) ++ circeDependencies
  )

lazy val kafka = (project in file("kafka"))
  .settings(moduleName := "kafka", name := "kafka")
  .settings(commonSettings)
  .settings(
    resolvers += Resolver.bintrayRepo("ovotech", "maven"),
    libraryDependencies ++= Seq(
      "com.ovoenergy" %% "fs2-kafka" % fs2KafkaVersion,
      "com.ovoenergy" %% "kafka-serialization-core" % KafkaSerializationV,
      "com.ovoenergy" %% "kafka-serialization-core" % KafkaSerializationV,
      "com.ovoenergy" %% "kafka-serialization-circe" % KafkaSerializationV,
    ) ++ circeDependencies
  )

lazy val api = (project in file("api"))
  .settings(moduleName := "api", name := "api")
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % LogbackVersion
    ) ++ http4sDependencies
      ++ testDependencies
  )

lazy val handler = (project in file("handler"))
  .settings(moduleName := "handler", name := "handler")
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion
    ) ++ http4sDependencies
      ++ testDependencies
  ).dependsOn(model, kafka, mongo)

lazy val mongo = (project in file("mongo"))
  .settings(moduleName := "mongo", name := "mongo")
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.lyranthe" %% "fs2-mongodb" % "0.5.0",
      "ch.qos.logback" % "logback-classic" % LogbackVersion
    ) ++ circeDependencies
  ).dependsOn(model)

lazy val notifier = (project in file("notifier"))
  .settings(moduleName := "notifier", name := "notifier")
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-core" % fs2V,
      "ch.qos.logback" % "logback-classic" % LogbackVersion
    ) ++ http4sDependencies
      ++ circeDependencies
      ++ testDependencies
  ).dependsOn(model, kafka)

lazy val fakerApp = (project in file("."))
  .settings(name := "faker", moduleName := "root")
  .settings(commonSettings)
  .aggregate(aggregatedProjects: _*)


lazy val aggregatedProjects: Seq[ProjectReference] = Seq(
  model,
  kafka,
  api,
  notifier,
  mongo,
  handler
)

lazy val http4sCompilerPlugins = Seq(
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6"),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4")
)

lazy val prod = taskKey[Unit]("Run production ready packaging.")

def commonScalacOptions(scalaVersion: String) =
  Seq(
    "-encoding",
    "UTF-8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:experimental.macros",
    "-language:postfixOps",
    "-unchecked",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfuture"
  ) ++ (if (priorTo2_13(scalaVersion))
    Seq(
      "-Yno-adapted-args",
      "-Xfatal-warnings",
      "-deprecation"
    ) else
    Seq(
      "-Ymacro-annotations"
    ))

def priorTo2_13(scalaVersion: String): Boolean =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, minor)) if minor < 13 => true
    case _ => false
  }
