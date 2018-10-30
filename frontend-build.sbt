import scala.sys.process.Process

/*
 * UI Build hook Scripts
 */

// Execution status success.
val Success = 0

// Execution status failure.
val Error = 1

// Run angular serve task when Play runs in dev mode, that is, when using 'sbt run'
// https://www.playframework.com/documentation/2.6.x/SBTCookbook
//PlayKeys.playRunHooks += baseDirectory.map(FrontendRunHook.apply).value

lazy val frontend: FrontendCommandsAlgebra = NpmCommands


def runOnCommandline(script: String)(implicit dir: File): Int = Process(script, dir) !

// Check of node_modules directory exist in given directory.
def isNodeModulesInstalled(implicit dir: File): Boolean = (dir / "node_modules").exists()

// Execute `npm install` command to install all node module dependencies. Return Success if already installed.
def runNpmInstall(implicit dir: File): Int =
  if (isNodeModulesInstalled) Success else runOnCommandline(frontend.dependencyInstall)

// Execute task if node modules are installed, else return Error status.
def ifNodeModulesInstalled(task: => Int)(implicit dir: File): Int =
  if (runNpmInstall == Success) task
  else Error

// Execute frontend test task. Update to change the frontend test task.
def executeUiRun(implicit dir: File): Int = ifNodeModulesInstalled(runOnCommandline(frontend.serve))

// Execute frontend test task. Update to change the frontend test task.
def executeUiTests(implicit dir: File): Int = ifNodeModulesInstalled(runOnCommandline(frontend.test))

// Execute frontend prod build task. Update to change the frontend prod build task.
def executeProdBuild(implicit dir: File): Int = ifNodeModulesInstalled(runOnCommandline(frontend.build))


// Create frontend build tasks for prod, dev and test execution.

lazy val `frontend-run` = taskKey[Unit]("Run UI runing application.")

`frontend-run` := {
  implicit val userInterfaceRoot = baseDirectory.value / "frontend"
  if (executeUiRun != Success) throw new Exception("UI runs failed!")
}
// Create front
// end build tasks for prod, dev and test execution.

lazy val `frontend-test` = taskKey[Unit]("Run UI tests when testing application.")

`frontend-test` := {
  implicit val userInterfaceRoot = baseDirectory.value / "frontend"
  if (executeUiTests != Success) throw new Exception("UI tests failed!")
}

lazy val `frontend-prod-build` = taskKey[Unit]("Run UI build when packaging the application.")

`frontend-prod-build` := {
  implicit val userInterfaceRoot = baseDirectory.value / "frontend"
  if (executeProdBuild != Success) throw new Exception("Oops! UI Build crashed.")
}
//
//// Execute frontend prod build task prior to play dist execution.
//compile := ((compile in Compile) dependsOn `frontend-prod-build`).value
//
//// Execute frontend test task prior to play test execution.
//test := ((test in Test) dependsOn `frontend-test`).value
