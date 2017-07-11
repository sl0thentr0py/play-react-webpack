import java.net.InetSocketAddress
import play.sbt.PlayRunHook
import sbt._
import sbt.Keys._

object Webpack {
  def apply(base: File): PlayRunHook = {
    object WebpackHook extends PlayRunHook {
      var process: Option[Process] = None

      override def beforeStarted() = {
        process = Option(
          Process("npm run webpack", base).run()
        )
      }

      override def afterStarted(addr: InetSocketAddress) = {
        process = Option(
          Process("npm run webpack -- --watch", base).run()
        )
      }

      override def afterStopped() = {
        process.foreach(_.destroy())
        process = None
      }
    }

    WebpackHook
  }

  def runDist = Def.task {
      println("running webpack dist")
      val statusCode = Process("npm run webpack", baseDirectory.value).!
      if(statusCode > 0) throw new Exception("Webpack failed with exit code : " + statusCode)
  }

}