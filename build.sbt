name := "Faker"
scalaVersion := "2.12.6"

lazy val model = project in file("model")
lazy val web = project in file("web")

lazy val fakerApp = (project in file("."))
  .aggregate(model, web)
