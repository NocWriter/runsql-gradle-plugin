plugins {
    id("java-gradle-plugin")
    id("com.adarshr.test-logger") version ("2.0.0")
    id("com.gradle.plugin-publish") version ("0.10.1")
}

group = "com.nocwriter.runsql"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
}

//
// Create a new source set for integration tests.
//
sourceSets {
    create("iTest") {
        java {
            // NOTE: By default, integration tests does not require access to plugin's sources ('src/main/java').
            // However,in case any of the integration tests DOES requires access to plugin sources, uncomment the
            // following lines:
            compileClasspath += sourceSets.main.get().compileClasspath
            runtimeClasspath += sourceSets.main.get().runtimeClasspath
        }
    }
}

//
// Plugin configuration.
//
gradlePlugin {
    // Register a plugin.
    plugins {
        create("runSQLPlugin") {
            id = "com.nocwriter.runsql"
            displayName = "Run SQL scripts"
            description = "This plugins execute a SQL script on a database."
            version = "1.0"
            implementationClass = "com.nocwriter.runsql.gradle.RunSQLPlugin"
        }
    }

    // We must add the test sources available to the plugin, so it can have access to the plugin runtime.
    testSourceSets(sourceSets["test"], sourceSets["iTest"])
}

//
// Gradle plugin publishing configuration.
//
pluginBundle {
    website = "https://github.com/NocWriter/runsql-gradle-plugin"
    vcsUrl = "https://github.com/NocWriter/runsql-gradle-plugin"
    tags = listOf("sql", "run", "execute", "execution", "plugin")
}

//
// In order to use the automatically created 'iTestImplementation' by the dependencies, we need to extract it into
// a local variable.
//
val iTestImplementation: Configuration = configurations["iTestImplementation"]

//
// Plugin dependencies.
//
dependencies {
    //
    // Apache Commons.
    //
    implementation("org.apache.commons:commons-lang3:3.9")

    //
    // JUnit platform/test utilities.
    //
    testImplementation(enforcedPlatform("org.junit:junit-bom:5.6.0"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.15.0")
    testImplementation("org.hsqldb:hsqldb:2.5.0")

    iTestImplementation(enforcedPlatform("org.junit:junit-bom:5.6.0"))
    iTestImplementation("org.junit.jupiter:junit-jupiter-api")
    iTestImplementation("org.junit.jupiter:junit-jupiter-engine")
    iTestImplementation("org.junit.jupiter:junit-jupiter")
    iTestImplementation("org.assertj:assertj-core:3.15.0")
    iTestImplementation("org.hsqldb:hsqldb:2.5.0")
    iTestImplementation("org.hsqldb:hsqldb:2.5.0:sources")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

//
// New test task to run integration tests.
//
val integrationTest = task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["iTest"].output.classesDirs
    classpath = sourceSets["iTest"].runtimeClasspath
}

//
// Test listener to display tests status and logging.
//
testlogger {
    theme = com.adarshr.gradle.testlogger.theme.ThemeType.STANDARD
    showExceptions = true
    showStackTraces = true
    showFullStackTraces = false
    showCauses = true
    slowThreshold = 2000
    showSummary = true
    showSimpleNames = false
    showPassed = true
    showSkipped = true
    showFailed = true
    showStandardStreams = false
    showPassedStandardStreams = true
    showSkippedStandardStreams = true
    showFailedStandardStreams = true
}
