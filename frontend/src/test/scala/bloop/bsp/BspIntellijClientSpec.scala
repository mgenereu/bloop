package bloop.bsp

import java.io.File
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.StandardOpenOption

import bloop.cli.BspProtocol
import bloop.data.WorkspaceSettings
import bloop.io.AbsolutePath
import bloop.logging.BspClientLogger
import bloop.logging.RecordingLogger
import bloop.util.{TestProject, TestUtil}
import ch.epfl.scala.bsp.WorkspaceBuildTargetsResult

object LocalBspIntellijClientSpec extends BspIntellijClientSpec(BspProtocol.Local)
object TcpBspIntellijClientSpec extends BspIntellijClientSpec(BspProtocol.Tcp)

class BspIntellijClientSpec(
    override val protocol: BspProtocol
) extends BspBaseSuite {

  test("refresh project data on buildTargets request") {
    TestUtil.withinWorkspace { workspace =>
      val logger = new RecordingLogger(ansiCodesSupported = false)
      val `A` = TestProject(workspace, "a", Nil)
      val `B` = TestProject(workspace, "b", Nil)
      val initialProjects = List(`A`)
      val configDir = TestProject.populateWorkspace(workspace, initialProjects)

      val refreshProjectsCommand = {
        val content = sanitizeForBash(`B`.toJson)
        val path = configDir.resolve("b.json")
        val addProjectBCommand = s"echo $content > $path"
        // as settings are read when project is initialized, with this command
        // we simulate that project 'b' is added after first request
        List(
          "bash",
          "-c",
          s"[ ! -f first-run-marker ] && touch first-run-marker || $addProjectBCommand"
        )
      }

      val workspaceSettings =
        WorkspaceSettings(None, None, refreshProjectsCommand = Some(refreshProjectsCommand))
      WorkspaceSettings.writeToFile(configDir, workspaceSettings, logger)

      loadBspState(workspace, initialProjects, logger, bspClientName = "IntelliJ") { state =>
        def projectNames(w: WorkspaceBuildTargetsResult) = w.targets.flatMap(_.displayName)

        val initialTargets = state.workspaceTargets
        assertEquals(projectNames(initialTargets), List("a"))
        val finalTargets = state.workspaceTargets
        assertEquals(projectNames(finalTargets), List("a", "b"))
      }
    }
  }

  private def sanitizeForBash(str: String): String = {
    str.replace("\n", "").replace("\"", "\\\"")
  }

}
