grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    test   : [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false],
    // configure settings for the run-app JVM
    run    : [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false],
    // configure settings for the run-war JVM
    war    : [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
  // inherit Grails' default dependencies
  inherits("global") {
    // specify dependency exclusions here; for example, uncomment this to disable ehcache:
    // excludes 'ehcache'
    excludes 'grails-docs'
  }
  log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  checksums true // Whether to verify checksums on resolve
  legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

  repositories {
    inherits true // Whether to inherit repository definitions from plugins

    mavenRepo 'http://repo.grails.org/grails/repo'
    grailsPlugins()
    grailsHome()
    mavenLocal()
    grailsCentral()
    mavenCentral()
    // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
    mavenRepo "http://repository.codehaus.org"
    //mavenRepo "http://download.java.net/maven/2/"
    //mavenRepo "http://repository.jboss.com/maven2/"

    mavenRepo 'http://dev.smartshiftsolution.com/maven2/'
    mavenRepo 'https://repo1.maven.org/maven2/'
    mavenRepo 'https://repo.grails.org/grails/plugins'
    mavenRepo 'http://maven.qncentury.com/nexus/content/repositories/thirdparty'
  }

  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
    // runtime 'mysql:mysql-connector-java:5.1.29'
    test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"

    compile group: 'org.apache.poi', name: 'poi-ooxml-schemas', version: '3.8'
    compile group: 'org.apache.poi', name: 'poi-ooxml', version: '3.8'
    compile(group: 'org.apache.poi', name: 'poi', version: '3.8') {
      excludes 'servlet-api', 'commons-logging', 'log4j'
    }
    compile 'commons-net:commons-net:3.3'

    compile 'net.sf.jasperreports:jasperreports:4.0.2'
//    compile 'com.lowagie:itext:2.1.5'
    runtime(group: 'net.sourceforge.barbecue', name: 'barbecue', version: '1.5-beta1') {
      excludes 'servlet-api'
    }
    compile 'com.github.jponge:lzma-java:1.2'
    compile 'joda-time:joda-time:2.6'
    compile 'c3p0:c3p0:0.9.1.2'
  }

  plugins {
    // plugins for the build system only
    build ":tomcat:8.0.20"

    // plugins for the compile step
    compile ":scaffolding:2.1.2"
    compile ':cache:1.1.8'
    compile ":asset-pipeline:2.1.3"
    compile ':spring-security-core:2.0-RC4'

    // plugins needed at runtime but not for compilation
    runtime ":hibernate4:4.3.6.1" // or ":hibernate:3.6.10.18"
    runtime ":database-migration:1.4.0"
    runtime ":jquery:1.11.1"
//    runtime ":jquery-ui:1.10.3"
    runtime ':twitter-bootstrap:3.3.1'

    // Uncomment these to enable additional asset-pipeline capabilities
    //compile ":sass-asset-pipeline:1.9.0"
    compile ":less-asset-pipeline:2.0.8"
    //compile ":coffee-asset-pipeline:1.8.0"
    //compile ":handlebars-asset-pipeline:1.3.0.3"

    compile ":quartz:1.0.2"
    compile ":mail:1.0.7"

    // compile ":scala:2.11.5"
  }
}
