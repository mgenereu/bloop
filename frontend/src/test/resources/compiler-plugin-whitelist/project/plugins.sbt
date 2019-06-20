val typesafeConfig = "com.typesafe" % "config" % "1.3.2"
val metaconfigCore = "com.geirsson" %% "metaconfig-core" % "0.6.0"
val metaconfigConfig = "com.geirsson" %% "metaconfig-typesafe-config" % "0.6.0"
val metaconfigDocs = "com.geirsson" %% "metaconfig-docs" % "0.6.0"
val circeDerivation = "io.circe" %% "circe-derivation" % "0.9.0-M3"
val bsp4j = "ch.epfl.scala" % "bsp4j" % "2.0.0-M4"

// Let's add our sbt plugin to the sbt too ;)
unmanagedSourceDirectories in Compile ++= {
  val baseDir =
    baseDirectory.value.getParentFile.getParentFile.getParentFile.getParentFile.getParentFile.getParentFile
  val integrationsMainDir = baseDir / "integrations"
  if (!integrationsMainDir.exists()) Nil
  else {
    val pluginMainDir = integrationsMainDir / "sbt-bloop" / "src" / "main"
    val clientMainDir = integrationsMainDir / "bsp4j-client" / "src" / "main"
    List(
      baseDir / "config" / "src" / "main" / "scala",
      baseDir / "config" / "src" / "main" / "scala-2.11-12",
      clientMainDir / "scala",
      pluginMainDir / "scala",
      pluginMainDir / s"scala-sbt-${sbt.Keys.sbtBinaryVersion.value}"
    )
  }
}

libraryDependencies ++= List(
  bsp4j,
  typesafeConfig,
  metaconfigCore,
  metaconfigDocs,
  metaconfigConfig,
  circeDerivation
)

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "0.6.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.26")
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.3.7")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.1")
addSbtPlugin("com.softwaremill.clippy" % "plugin-sbt" % "0.5.3")
