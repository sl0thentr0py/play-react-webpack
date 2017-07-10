import java.net.InetSocketAddress
import play.sbt.PlayRunHook
import com.typesafe.sbt.web.pipeline.Pipeline
import sbt._

object Webpack {
  def apply(base: File): PlayRunHook = {
    object WebpackHook extends PlayRunHook {
      var process: Option[Process] = None

      override def beforeStarted() = {
        process = Option(
          Process("webpack", base).run()
        )
      }

      override def afterStarted(addr: InetSocketAddress) = {
        process = Option(
          Process("webpack --watch", base).run()
        )
      }

      override def afterStopped() = {
        process.foreach(_.destroy())
        process = None
      }
    }

    WebpackHook
  }

  def runDist(base: File) = {
    Process("webpack", base).run()
  }
}