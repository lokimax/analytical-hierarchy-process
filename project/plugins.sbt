// The Play plugin

// Web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.7")
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.0")
addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.2")

// Play enhancer - this automatically generates getters/setters for public fields
// and rewrites accessors of these fields to use the getters/setters. Remove this
// plugin if you prefer not to have this feature, or disable on a per project
// basis using disablePlugins(PlayEnhancer) in your build.sbt
addSbtPlugin("com.typesafe.sbt" % "sbt-play-enhancer" % "1.1.0")

// Play Ebean support, to enable, uncomment this line, and enable in your build.sbt using
// enablePlugins(PlayEbean).
// addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "1.0.0")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.8")

// provides server side compilation of typescript to ecmascript 5 or 3
addSbtPlugin("name.de-vries" % "sbt-typescript" % "0.3.0-beta.4")

// checks your typescript code for error prone constructions
addSbtPlugin("name.de-vries" % "sbt-tslint" % "3.13.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.10")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

resolvers += Resolver.typesafeRepo("releases")
resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)