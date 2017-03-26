name := "dockerised-scala-aws"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-http" % "10.0.5")

enablePlugins(DockerPlugin)

dockerfile in docker := {
  // The assembly task generates a fat JAR file
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  new Dockerfile {
    from("openjdk:8-jre-alpine")

    add(artifact, artifactTargetPath)

    expose(8080)

    entryPoint("java", "-jar", artifactTargetPath)
  }
}

imageNames in docker := Seq(
  ImageName(s"${organization.value}/${name.value}:latest"),
  ImageName(
    namespace = Some(organization.value),
    repository = name.value,
    tag = Some("v" + version.value)
  )
)


