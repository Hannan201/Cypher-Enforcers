/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.3/userguide/building_java_projects.html in the Gradle documentation.
 */

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application

    // Plugin to work with JavaFX
    id("org.openjfx.javafxplugin") version "0.1.0"

    // Plugin to work with Intellij IDEA.
    idea
}

group = "cypher.enforcers"
version = "1.0.0"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Logging API.
    implementation("org.slf4j:slf4j-api:2.0.9")

    // Logging implementation.
    implementation("ch.qos.logback:logback-classic:1.3.7") {
        // This is because logback brings in a transitive
        // dependency to SLF4J, even though we have already
        // imported it. No need for it to be imported twice.
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    // ValidatorFX.
    implementation("net.synedra:validatorfx:0.4.2")

    // ControlsFX.
    implementation("org.controlsfx:controlsfx:11.1.2")

    // SQLite support.
    implementation("org.xerial:sqlite-jdbc:3.43.2.0")

    // Junit5 in our case needs two modules.
    // Junit jupiter -> This is for the @Test annotations and the assertions
    // such as asserTrue, assertEquals, etc.
    // Junit platform -> This is what launches the testing framework on the
    // JVM.
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Using runtime only because this dependency is only needed
    // when running the tests.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher") {
        because("Only needed to run tests in a version of IntelliJ IDEA that bundles older versions")
    }

    // Used to extract filename from a url.
    implementation("commons-io:commons-io:2.14.0")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.ADOPTIUM) // To use OpenJDK.
    }
}

application {
    // Define the main module for the application.
    mainModule.set("cypher.enforcers")

    // Define the main class for the application.
    mainClass.set("cypher.enforcers.Launcher")
}

val cleanUpTask: TaskProvider<Delete> = tasks.register<Delete>("cleanUpTestFiles") {
    // This is to remove files that were copied from resources.
    // These files are modified after the tests are complete, so they
    // need to be copied again to return to their original state.
    delete(fileTree(layout.buildDirectory.dir("classes/java")) {
        include("*.db")
        include("*.pfx")
    })
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()

    // clean up old files.
    dependsOn(cleanUpTask)

    // Show tests that failed and passed.
    testLogging {
        events("passed", "failed")
    }
}

javafx {
    // This is the equivalent of doing
    // --module-path=path/to/javafx/lib/folder --add-modules=javafx.fxml,
    // javafx.controls in the run configuration of Intellij IDEA.

    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}

tasks.named<Delete>("clean") {
    // This is to remove the log file.
    delete(layout.projectDirectory.file("logs.log"))
}