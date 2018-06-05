name := """play-ahp"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.8"
incOptions := incOptions.value.withNameHashing(true)
updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)

libraryDependencies ++= {
  val ngVersion="2.2.1"
  Seq(
  javaJdbc,
  javaJpa,
  "org.hibernate" % "hibernate-entitymanager" % "5.1.0.Final",
  //"org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
   "org.postgresql" % "postgresql" % "42.2.2",
  "org.apache.commons" % "commons-math3" % "3.0",
  cache,
  javaWs,
  "com.typesafe.play" %% "play-mailer" % "5.0.0-M1",
  "org.mockito" % "mockito-core" % "1.10.19",
  //angular2 dependencies
    "org.webjars.npm" % "angular__common" % ngVersion,
    "org.webjars.npm" % "angular__compiler" % ngVersion,
    "org.webjars.npm" % "angular__core" % ngVersion,
    "org.webjars.npm" % "angular__forms" % ngVersion,
    "org.webjars.npm" % "angular__router" % "3.0.1",
    "org.webjars.npm" % "angular__http" % ngVersion,
    "org.webjars.npm" % "angular__platform-browser-dynamic" % ngVersion,
    "org.webjars.npm" % "angular__platform-browser" % ngVersion,
    "org.webjars.npm" % "systemjs" % "0.19.31",
    "org.webjars.npm" % "todomvc-common" % "1.0.2",
    "org.webjars.npm" % "rxjs" % "5.0.0-beta.9",
    "org.webjars.npm" % "es6-promise" % "3.1.2",
    "org.webjars.npm" % "es6-shim" % "0.35.1",
    "org.webjars.npm" % "reflect-metadata" % "0.1.8",
    "org.webjars.npm" % "zone.js" % "0.6.23",
    "org.webjars.npm" % "core-js" % "2.4.0",
    "org.webjars.npm" % "symbol-observable" % "1.0.1",
    "org.webjars.npm" % "bootstrap" % "3.3.7",
    "org.webjars.npm" % "typescript" % "2.0.0",
	"org.webjars.npm" % "angular2-in-memory-web-api" % "0.0.21",
    //tslint dependency
    "org.webjars.npm" % "tslint-eslint-rules" % "1.5.0",
    "org.webjars.npm" % "codelyzer" % "0.0.28",
    "org.webjars.npm" % "types__jasmine" % "2.2.26-alpha" % "test",
    "org.webjars.npm" % "ng2-charts" % "1.4.0"  
    //test
//  "org.webjars.npm" % "jasmine-core" % "2.4.1"
)
}
dependencyOverrides += "org.webjars.npm" % "minimatch" % "3.0.0"
resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)

// the typescript typing information is by convention in the typings directory
// It provides ES6 implementations. This is required when compiling to ES5.
typingsFile := Some(baseDirectory.value / "typings" / "index.d.ts")

// use the webjars npm directory (target/web/node_modules ) for resolution of module imports of angular2/core etc
resolveFromWebjarsNodeModulesDir := true

// use the combined tslint and eslint rules plus ng2 lint rules
(rulesDirectories in tslint) := Some(List(
  tslintEslintRulesDir.value,
  ng2LintRulesDir.value
))
routesGenerator := InjectedRoutesGenerator

PlayKeys.externalizeResources := false

fork in run := false