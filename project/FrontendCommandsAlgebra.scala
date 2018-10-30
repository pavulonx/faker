trait FrontendCommandsAlgebra {

  def dependencyInstall: String

  def test: String

  def serve: String

  def build: String

}

object NpmCommands extends FrontendCommandsAlgebra {

  val dependencyInstall: String = "npm install"

  val test: String = "npm run test"

  val serve: String = "npm run start"

  val build: String = "npm run build"

}

// create YarnCommands when needed