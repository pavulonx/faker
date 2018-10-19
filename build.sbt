name := "Faker"
scalaVersion := "2.12.6"


val Success = 0
val Error = 1


lazy val model = project in file("model")
lazy val kafka = project in file("kafka")
lazy val api = project in file("api")

lazy val fakerApp = (project in file("."))
  .aggregate(model, kafka, api)



lazy val prod = taskKey[Unit]("Run production ready packaging.")

//prod := {
//  implicit val userInterfaceRoot = baseDirectory.value
//  if (compile() != Success) throw new Exception("Oops! UI Build crashed.")
//}