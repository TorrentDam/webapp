import org.scalajs.linker.interface.ModuleInitializer

lazy val webapp = project
  .in(file("."))
  .aggregate(app, sw)

lazy val app = project
  .dependsOn(shared)
  .settings(
    libraryDependencies ++= List(
      "com.github.torrentdam.server" %%% "protocol" % "1.1.0",
      "com.raquo" %%% "laminar" % "0.14.0",
      "com.raquo" %%% "waypoint" % "0.5.0",
      "io.laminext" %%% "websocket" % "0.14.0",
      "io.circe" %%% "circe-parser" % "0.15.0-M1",
      "io.circe" %%% "circe-generic" % "0.15.0-M1",
      "org.typelevel" %%% "squants" % "1.6.0" cross CrossVersion.for3Use2_13,
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
      "com.github.torrentdam" %%% "common" % "1.0.0",
      "io.circe" %%% "circe-parser" % "0.15.0-M1",
      "io.circe" %%% "circe-generic" % "0.15.0-M1",
      ("org.scala-js" %%% "scalajs-dom" % "2.0.0"),
    )
  )
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)

lazy val commonSettings = List(
  organization := "com.github.lavrov",
  scalaVersion := "3.1.0",
  externalResolvers ++= List(
    "server packages" at "https://maven.pkg.github.com/TorrentDamDev/server",
    "bittorrent packages" at "https://maven.pkg.github.com/TorrentDamDev/bittorrent",
  ),
  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
)