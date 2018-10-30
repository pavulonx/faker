scalaVersion in ThisBuild := "2.12.6"
organization in ThisBuild := "cf.jrozen"

val Http4sVersion = "0.18.20" //todo: upgrade to 0.20.x series
val Specs2Version = "4.2.0"
val LogbackVersion = "1.2.3"

val Success = 0
val Error = 1

shellPrompt := { s => Project.extract(s).currentProject.id + " > " }

lazy val commonSettings = Seq(
  scalacOptions ++= commonScalacOptions(scalaVersion.value)
)

lazy val model = (project in file("model"))
  .settings(moduleName := "model", name := "model")


lazy val kafka = (project in file("kafka"))
  .settings(moduleName := "kafka", name := "kafka")
  .settings(
    resolvers += "Ovotech" at "https://dl.bintray.com/ovotech/maven",
    libraryDependencies ++= Seq(
      "com.ovoenergy" %% "fs2-kafka-client" % "0.1.13"
    )
  )

lazy val api = (project in file("api"))
  .settings(moduleName := "api", name := "api")
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.specs2" %% "specs2-core" % Specs2Version % "test",
      "ch.qos.logback" % "logback-classic" % LogbackVersion
    )
  )

lazy val notifier = (project in file("notifier"))
  .settings(moduleName := "notifier", name := "notifier")
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.specs2" %% "specs2-core" % Specs2Version % "test",
      "ch.qos.logback" % "logback-classic" % LogbackVersion
    )
  )

lazy val fakerApp = (project in file("."))
  .settings(name := "faker", moduleName := "root")
  .settings(commonSettings)
  .aggregate(aggregatedProjects: _*)


lazy val aggregatedProjects: Seq[ProjectReference] = Seq(
  model,
  kafka,
  api,
  notifier
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
