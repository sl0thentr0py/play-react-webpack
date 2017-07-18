
import java.net.InetSocketAddress

import com.typesafe.sbt.jse.JsEngineImport.JsEngineKeys

import com.typesafe.sbt.digest.Import._
import com.typesafe.sbt.gzip.Import._
import com.slidingautonomy.sbt.filter.Import._
import com.typesafe.sbt.web.Import._
import com.typesafe.sbt.web.SbtWeb
import play.sbt.Play.autoImport._
import Webpack.autoImport._
import sbt._
import sbt.Keys._


object ApplicationBuild extends Build {

  override def settings = super.settings ++ Seq(
    scalaVersion := "2.11.7",
    version := "0.1",
    incOptions := incOptions.value.withNameHashing(true),
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
    //fork := true
  )

  val commonSettings = Seq(
    sources in doc in Compile := List() // skip api generation
  )

  /** data model */
  lazy val model = Project(
    "model", file("model"), settings = commonSettings).settings(
    libraryDependencies ++= Seq(
      cache
    )
  )


  /** play website */
  lazy val main = Project("main", file("."), settings = commonSettings).
    enablePlugins(play.sbt.PlayScala, SbtWeb, Webpack).
    settings(Seq(
      JsEngineKeys.engineType := JsEngineKeys.EngineType.Node,
      libraryDependencies ++= Seq(
        cache, // play cache external module
        ws,
        "com.lihaoyi" %% "upickle" % "0.2.8",
        "org.scalatestplus" % "play_2.11" % "1.4.0"
      ),
      includeFilter in filter := "*.less" || "*.jsx",
      pipelineStages := Seq(webpack, filter, digest, gzip),
      PlayKeys.playRunHooks += WebpackDevServer(baseDirectory.value, streams.value.log),
      WebKeys.exportedMappings in Assets := Seq()
    )
  ).dependsOn(model)

}

