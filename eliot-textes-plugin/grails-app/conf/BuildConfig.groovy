grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.plugin.location.'eliot-tice-plugin' = "../eliot-tice-plugin"

grails.project.dependency.resolution = {
  // inherit Grails' default dependencies
  inherits("global") {
    // uncomment to disable ehcache
    // excludes 'ehcache'
    excludes "xml-apis"
  }
  log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  repositories {
    grailsCentral()

  }
  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

  }

  plugins {
    build(":tomcat:$grailsVersion",
          ":release:1.0.0",
          ":hibernate:$grailsVersion") {
      export = false
    }

    compile(":codenarc:0.15") {
      export = false
    }

    compile(":gmetrics:0.3.1") {
      excludes "groovy-all"
      export = false
    }
  }
}
