import org.scalajs.linker.interface.ModuleInitializer

lazy val webapp = project
  .in(file("."))
  .aggregate(app, sw)

lazy val app = project
  .dependsOn(shared)
  .settings(
    libraryDependencies ++= List(
      "io.github.torrentdam.server" %%% "protocol" % "3.0.0",
      "com.raquo" %%% "laminar" % "15.0.0-M7",
      "com.raquo" %%% "waypoint" % "6.0.0-M5",
      "io.laminext" %%% "websocket" % "0.15.0-M7",
      "io.circe" %%% "circe-parser" % "0.15.0-M1",
      "io.circe" %%% "circe-generic" % "0.15.0-M1",
      "org.typelevel" %%% "squants" % "1.8.3",
    ),
    Compile / scalaJSModuleInitializers ++= List(
      ModuleInitializer.mainMethod("default.Main", "init").withModuleID("main"),
    )
  )
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)

lazy val sw = project
  .dependsOn(shared)
  .settings(
    Compile / scalaJSModuleInitializers ++= List(
      ModuleInitializer.mainMethod("default.ServiceWorker", "init").withModuleID("sw"),
    )
  )
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)

lazy val shared = project
  .settings(
    libraryDependencies ++= List(
      "io.github.torrentdam.bittorrent" %%% "common" % "1.0.0",
      "io.circe" %%% "circe-parser" % "0.15.0-M1",
      "io.circe" %%% "circe-generic" % "0.15.0-M1",
      ("org.scala-js" %%% "scalajs-dom" % "2.0.0"),
    )
  )
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)

lazy val commonSettings = List(
  organization := "io.github.torrentdam.webapp",
  scalaVersion := "3.2.1",
  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
)