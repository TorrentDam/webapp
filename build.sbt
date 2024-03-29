import org.scalajs.linker.interface.ModuleInitializer

lazy val webapp = project
  .in(file("."))
  .aggregate(app)

lazy val app = project
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
      ModuleInitializer.mainMethod("Main", "init").withModuleID("main"),
    )
  )
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)

lazy val commonSettings = List(
  organization := "io.github.torrentdam.webapp",
  scalaVersion := "3.2.2",
  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
)