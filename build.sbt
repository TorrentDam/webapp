import org.scalajs.linker.interface.ModuleInitializer


val webapp = project
  .in(file("."))
  .settings(
    libraryDependencies ++= List(
      "com.raquo" %%% "laminar" % "0.13.1",
      "com.raquo" %%% "waypoint" % "0.4.0",
      "io.laminext" %%% "websocket" % "0.13.1",
      ("org.typelevel" %%% "squants" % "1.6.0").cross(CrossVersion.for3Use2_13),
    ),
    libraryDependencies ++= List(
      ("com.github.torrentdam" %%% "protocol" % "0.5.0").cross(CrossVersion.for3Use2_13),
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
      ("org.scala-js" %%% "scalajs-dom" % "1.1.0").cross(CrossVersion.for3Use2_13),
    ),
    Compile / scalaJSModuleInitializers ++= List(
      ModuleInitializer.mainMethod("default.ServiceWorker", "init").withModuleID("sw"),
    )
  )
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)

lazy val commonSettings = List(
  organization := "com.github.lavrov",
  scalaVersion := "3.0.1",
  externalResolvers ++= List(
    "server packages" at "https://maven.pkg.github.com/TorrentDam/server",
    "bittorrent packages" at "https://maven.pkg.github.com/TorrentDam/bittorrent",
  )
)