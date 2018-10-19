trait FrontendCommandsAlgebra {

  def dependencyInstall: String

  def test: String

  def serve: String

  def build: String

}

object NpmCommands extends FrontendCommandsAlgebra {

  val dependencyInstall: String = "npm install"

  val test: String = "npm run test:ci"

  val serve: String = "npm run start"

  val build: String = "npm run build:prod"

}

// create YarnCommands when needed