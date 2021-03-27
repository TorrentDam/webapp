

val ui = project
  .settings(
    name := "Laminar + Bulma",
    version := "1-SNAPSHOT",
    normalizedName := "laminar-bulma",
    organization := "com.github.lavrov",
    scalaVersion := "2.13.5",
    libraryDependencies ++= List(
      "com.raquo" %%% "laminar" % "0.12.1",
      "com.raquo" %%% "waypoint" % "0.3.0",
      "io.laminext" %%% "websocket" % "0.12.1",
      "io.laminext" %%% "websocket-circe" % "0.12.1",
      "com.lihaoyi" %%% "upickle" % "1.3.8",
    ),
    libraryDependencies ++= List(
      "com.github.torrentdam" %%% "protocol" % "0.5.0",
    ),
    externalResolvers ++= List(
      "server packages" at "https://maven.pkg.github.com/TorrentDam/server",
      "bittorrent packages" at "https://maven.pkg.github.com/TorrentDam/bittorrent",
    ),
    scalaJSUseMainModuleInitializer := true,
  )
  .enablePlugins(ScalaJSPlugin)

