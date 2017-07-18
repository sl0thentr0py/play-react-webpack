import play.sbt.PlayRunHook
import sbt._
import sbt.Keys._
import java.net.InetSocketAddress



object WebpackDevServer {

  def apply(baseDirectory: File, log: Logger): PlayRunHook = {

    object WebpackDevServerRunHook extends PlayRunHook {

      var webpackProcess: Option[Process] = None

      override def beforeStarted(): Unit = {
        //NpmUtils.installNpmModules(baseDirectory, log) TODO replace with dependsOn nodeModules
      }

      override def afterStarted(addr: InetSocketAddress): Unit = {
        log.info("Starting webpack-dev-server")
        webpackProcess = Some(Process(Seq("npm", "start"), baseDirectory).run)
      }

      override def afterStopped(): Unit = {
        log.info("Stopping webpack-dev-server")
        webpackProcess.foreach(p => p.destroy())
        Process(Seq("pkill", "-f","webpack-dev-server"), baseDirectory).!
        webpackProcess = None
      }
    }

  WebpackDevServerRunHook
  }
}
