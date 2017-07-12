
import java.net.InetSocketAddress

import com.typesafe.sbt.jse.JsEngineImport.JsEngineKeys

import com.typesafe.sbt.digest.Import._
import com.typesafe.sbt.gzip.Import._
import com.typesafe.sbt.web.Import._
import com.typesafe.sbt.web.SbtWeb
import play.sbt.Play.autoImport._
import play.sbt.PlayRunHook
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
    enablePlugins(play.sbt.PlayScala, SbtWeb).
    settings(Seq(
      JsEngineKeys.engineType := JsEngineKeys.EngineType.Node,
      libraryDependencies ++= Seq(
        cache, // play cache external module
        ws,
        "com.lihaoyi" %% "upickle" % "0.2.8",
        "org.scalatestplus" % "play_2.11" % "1.4.0"
      ),
      pipelineStages := Seq(digest, gzip),
      WebKeys.exportedMappings in Assets := Seq()
    ) ++ addJsSourceFileTasks(webpack)
    ).dependsOn(model)

  //val webpack = taskKey[Unit]("Run webpack dist")
  val webpack = taskKey[Seq[File]]("Webpack source file task")

  // from https://github.com/sbt/sbt-js-engine/blob/master/src/main/scala/com/typesafe/sbt/jse/SbtJsTask.scala
  def addUnscopedJsSourceFileTasks(sourceFileTask: TaskKey[Seq[File]]): Seq[Setting[_]] = {
    Seq(
      resourceGenerators <+= sourceFileTask,
      managedResourceDirectories += (resourceManaged in sourceFileTask).value
    ) ++ inTask(sourceFileTask)(Seq(
      sourceDirectories := unmanagedSourceDirectories.value ++ managedSourceDirectories.value,
      sources := unmanagedSources.value ++ managedSources.value
    ))
  }

  def addJsSourceFileTasks(sourceFileTask: TaskKey[Seq[File]]): Seq[Setting[_]] = {
    Seq(
      sourceFileTask in Assets := webpackTask.value,
      resourceManaged in sourceFileTask in Assets := WebKeys.webTarget.value / sourceFileTask.key.label / "main",
      sourceFileTask := (sourceFileTask in Assets).value
    ) ++ inConfig(Assets)(addUnscopedJsSourceFileTasks(sourceFileTask))
  }

  def webpackTask : Def.Initialize[Task[Seq[File]]] = Def.task {
    val targetDir = WebKeys.webTarget.value / "webpack" / "main"
    println("running webpack dist")
    val statusCode = Process("npm run webpack", baseDirectory.value).!
    if(statusCode > 0) throw new Exception("Webpack failed with exit code : " + statusCode)
    targetDir.***.get.filter(_.isFile)
  }

}

