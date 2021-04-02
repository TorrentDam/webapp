import org.scalajs.linker.interface.ModuleInitializer


val webapp = project
  .in(file("."))
  .settings(
    version := "1-SNAPSHOT",
    organization := "com.github.lavrov",
    scalaVersion := "2.13.5",
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
    externalResolvers ++= List(
      "server packages" at "https://maven.pkg.github.com/TorrentDam/server",
      "bittorrent packages" at "https://maven.pkg.github.com/TorrentDam/bittorrent",
    ),
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    scalaJSModuleInitializers in Compile ++= List(
      ModuleInitializer.mainMethod("default.Main", "init").withModuleID("main"),
      ModuleInitializer.mainMethod("default.ServiceWorker", "init").withModuleID("sw"),
    )
  )
  .enablePlugins(ScalaJSPlugin)

