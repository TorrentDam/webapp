enablePlugins(
  ScalablyTypedConverterExternalNpmPlugin,
)

name := "webapp"
scalaVersion := "2.13.2"

scalacOptions ++= Seq(
  "-language:higherKinds",
  "-Ymacro-annotations",
)

// This is an application with a main method
scalaJSUseMainModuleInitializer := false

import org.scalajs.linker.interface.ModuleSplitStyle
scalaJSLinkerConfig ~= (_
  .withModuleSplitStyle(ModuleSplitStyle.SmallestModules)
  .withModuleKind(ModuleKind.ESModule)
)

externalNpm := baseDirectory.value
stFlavour := Flavour.Slinky

libraryDependencies ++= Seq(
  "com.github.torrentdam" %%% "protocol" % Versions.protocol,
  "me.shadaj" %%% "slinky-web" % Versions.slinky,
  "org.typelevel" %%% "cats-effect" % Versions.`cats-effect`,
  "org.scodec" %%% "scodec-bits" % Versions.`scodec-bits`,
  "org.typelevel" %%% "squants" % Versions.squants,
  "tech.sparse" %%% "trail" % Versions.trail,
)

resolvers += Resolver.bintrayRepo("lavrov", "maven")

lazy val Versions = new {
  val protocol = "0.2.0"
  val `cats-effect` = "3.0.0-M2"
  val `scodec-bits` = "1.1.14"
  val slinky = "0.6.5"
  val squants = "1.6.0"
  val trail = "0.3.0"
}
