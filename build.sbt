import org.scalajs.linker.interface.ModuleInitializer


val webapp = project
  .in(file("."))
  .settings(
    libraryDependencies ++= List(
      "com.raquo" %%% "laminar" % "0.12.1",
      "com.raquo" %%% "waypoint" % "0.3.0",
      "io.laminext" %%% "websocket" % "0.12.1",
      "io.laminext" %%% "websocket-circe" % "0.12.1",
      "com.lihaoyi" %%% "upickle" % "1.3.8",
      "org.typelevel" %%% "squants" % "1.6.0",
    ),
    libraryDependencies ++= List(
      "com.github.torrentdam" %%% "protocol" % "0.5.0",
    ),
    Compile / scalaJSModuleInitializers ++= List(
      ModuleInitializer.mainMethod("default.Main", "init").withModuleID("main"),
    )
  )
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)
  .aggregate(sw)

lazy val sw = project
  .settings(
    libraryDependencies ++= List(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0",
    ),
    Compile / scalaJSModuleInitializers ++= List(
      ModuleInitializer.mainMethod("default.ServiceWorker", "init").withModuleID("sw"),
    )
  )
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)

lazy val commonSettings = List(
  organization := "com.github.lavrov",
  scalaVersion := "2.13.5",
  externalResolvers ++= List(
    "server packages" at "https://maven.pkg.github.com/TorrentDam/server",
    "bittorrent packages" at "https://maven.pkg.github.com/TorrentDam/bittorrent",
  )
)