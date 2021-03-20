

val ui = project
  .settings(
    name := "Laminar + Bulma",
    version := "1-SNAPSHOT",
    normalizedName := "laminar-bulma",
    organization := "com.github.lavrov",
    scalaVersion := "2.13.5",
    libraryDependencies += "com.raquo" %%% "laminar" % "0.12.1",
    scalaJSUseMainModuleInitializer := true,
  )
  .enablePlugins(ScalaJSPlugin)

