

val ui = project
  .settings(
    name := "Laminar + Bulma",
    version := "1-SNAPSHOT",
    normalizedName := "laminar-bulma",
    organization := "com.github.lavrov",
    scalaVersion := "2.13.5",
    libraryDependencies += "com.raquo" %%% "laminar" % "0.12.1",
    libraryDependencies ++= List(
      "com.github.torrentdam" %% "protocol" % "0.3.0",
    ),
    externalResolvers ++= List(
      "server packages" at "https://maven.pkg.github.com/TorrentDam/server",
      "bittorrent packages" at "https://maven.pkg.github.com/TorrentDam/bittorrent",
    ),
    scalaJSUseMainModuleInitializer := true,
  )
  .enablePlugins(ScalaJSPlugin)

