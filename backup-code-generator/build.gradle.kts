import org.beryx.jlink.JPackageImageTask
import org.beryx.jlink.data.JlinkPluginExtension
import org.gradle.crypto.checksum.Checksum
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem

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

    // Plugin to work with IntelliJ IDEA.
    idea

    // Plugin to create a native image.
    id("org.beryx.jlink") version "3.0.1"

    // Plugin to make checksums.
    id("org.gradle.crypto.checksum") version "1.4.0"
}

group = "cypher.enforcers"
version = "1.0.0"

// Adds the module version to the module-info.java file.
tasks.named<JavaCompile>("compileJava") {
    options.javaModuleVersion = provider { version as String }
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

val os: DefaultOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()

configurations {
    /*
     I don't have access to a mac with an M1 processor, and
     GitHub Actions only has a mac VM with an Intel processor.
     So when running the mac VM on GitHub Actions, I add the M1
     JavaFX jars, along with all the other dependencies this
     application needs into this newly created configuration.
     Then, I create a new custom runtime image with the M1
     JavaFX jars rather than the ones with intel.
     From there, I just used the JLink plugin to make a new
     installer.

     This gives me an installer for both M1 and Intel.
     Since you can't make cross-platform installers (For example,
     can't make a linux installer while on Windows) so I just make
     two installers when on the VM for Mac provided by GitHub Actions.
     */
    val isCI = providers.gradleProperty("isCI")
    if (os.isMacOsX && isCI.isPresent) {
       create("m1Configuration") {
           extendsFrom(configurations.runtimeClasspath.get())

           // Copy all the runtime attributes.
           // These are used to grab the correct JavaFX jars
           // based on the OS and architecture, along with
           // any other attributes that might be needed.
           val runtimeAttributes = configurations.runtimeClasspath.get().attributes
           runtimeAttributes.keySet().forEach { key ->
               // Got an error when using the attribute, so I avoided it by
               // not copying it.
               if (key.name != "org.gradle.jvm.version") {
                   attributes.attribute(key as Attribute<Any>, runtimeAttributes.getAttribute(key) as Any)
               }
           }

           attributes {
               attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE, objects.named<OperatingSystemFamily>(OperatingSystemFamily.MACOS))
               attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, objects.named<MachineArchitecture>(MachineArchitecture.ARM64))
           }
       }
    }

}

dependencies {
    // Logging API.
    implementation("org.slf4j:slf4j-api:2.0.9")

    // Logging implementation.
    implementation("ch.qos.logback:logback-classic:1.4.11") {
        // This is because logback brings in a transitive
        // dependency to SLF4J, even though we have already
        // imported it. No need for it to be imported twice.
        exclude("org.slf4j", "slf4j-api")
    }

    // ValidatorFX.
    implementation("net.synedra:validatorfx:0.4.2")

    // ControlsFX.
    implementation("org.controlsfx:controlsfx:11.1.2")

    // SQLite support.
    implementation("org.xerial:sqlite-jdbc:3.43.2.2")

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
        because("Only needed to run tests in a version of IntelliJ IDEA that bundles older versions.")
    }

    // Used to extract filename from an url.
    implementation("commons-io:commons-io:2.15.0")

    /*
    Only want to add these jars when on GitHub Actions, since someone
    using this project on an M1 will get more jars than they need.
     */
    val isCI = providers.gradleProperty("isCI")
    if (os.isMacOsX && isCI.isPresent) {
        val m1Configuration: Configuration = configurations.named("m1Configuration").get()

        m1Configuration("org.openjfx:javafx-controls:21")
        m1Configuration("org.openjfx:javafx-fxml:21")
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    withSourcesJar()
    withJavadocJar()

    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.ADOPTIUM // To use OpenJDK.
    }
}

application {
    // Define the main module for the application.
    mainModule = "backup.code.generator"

    // Define the main class for the application.
    mainClass = "cypher.enforcers.Launcher"
}

val cleanUpTask: TaskProvider<Delete> = tasks.register<Delete>("cleanUpTestFiles") {
    group = "verification"
    description = "Cleans up the files created by the" +
            "tests."

    // This is to remove files copied from resources.
    // These files are modified after the tests are complete, so they
    // need to be copied again to return to their original state.
    delete(fileTree(layout.projectDirectory) {
        include("*.db")
        exclude("database.db")
    })
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()

    // Clean up old files.
    finalizedBy(cleanUpTask)

    // Show tests that failed and passed.
    testLogging {
        events("passed", "failed")
    }
}

javafx {
    // This is the equivalent of doing
    // --module-path=path/to/javafx/lib/folder --add-modules=javafx.fxml,
    // javafx.controls in the run configuration of IntelliJ IDEA.

    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}

tasks.named<Delete>("clean") {
    // This is to remove the log file.
    delete(layout.projectDirectory.file("logs.log"))
}

// To work with projects inside IntelliJ IDEA.
idea {
    module {
        inheritOutputDirs = true
    }
}

jlink {
    options.set(listOf(
        "--compress",
        "zip-9",
        "--no-header-files",
        "--no-man-pages",
        "--strip-debug"
    ))

    jpackage {
        appVersion = version.toString()
        imageName = "Backup Code Generator"
        installerOptions.addAll(
            listOf(
                "--app-version", version.toString(),
                "--description", "A way to manage two factor authentication codes for all your social media accounts.",
                "--vendor", "Cypher Enforcers",
                "--about-url", "https://github.com/Hannan201/Cypher-Enforcers/tree/feature/internal-changes",
            )
        )

        val isCI = providers.gradleProperty("isCI")
        if (os.isMacOsX && isCI.isPresent) {
            installerName = project.name + "-intel"
        }
    }

    if (os.isWindows) {
        jpackage {
            installerType = "msi"
            installerOptions.addAll(
                listOf(
                    "--win-dir-chooser",
                    "--win-help-url", "https://github.com/Hannan201/Cypher-Enforcers/tree/feature/internal-changes",
                    "--win-menu",
                    "--win-menu-group", "Cypher Enforcers",
                    "--win-shortcut-prompt",
                    "--win-about-url", "https://github.com/Hannan201/Cypher-Enforcers/tree/feature/internal-changes"
                )
            )
        }
    } else if (os.isMacOsX) {
        jpackage {
            installerType = "dmg"
            installerOptions.addAll(
                listOf(
                    "--mac-package-identifier", "cypher.enforcers.backup.code.generator",
                    "--mac-package-name", "Code Generator",
                    "--mac-app-category", "public.app-category.utilities"
                )
            )
        }
    } else if (os.isLinux) {
        jpackage {
            installerOptions.addAll(
                listOf(
                    "--linux-package-name", "backup-code-generator-cypher-enforcers",
                    "--linux-shortcut"
                )
            )
        }
    }

}

tasks.named<JPackageImageTask>("jpackageImage") {
    doLast {
        /*
        On Windows, JPackage for some reason makes the native image file
        (.exe on Windows) read only, so Gradle can't delete it when using
        the clean task.
        The code below modifies the permission of the produced file,
        so it can be deleted on Windows.
         */
        if (os.isWindows) {
            layout.buildDirectory.file(
                jpackageData.imageOutputDir.name
                + File.separator
                + jpackageData.imageName
                + File.separator
                + jpackageData.imageName + ".exe"
            ).get().asFile.setWritable(true)
        }
    }
}

// Task to create an uber or fat jar.
val uberJarTask: TaskProvider<Jar> = tasks.register<Jar>("uberJar") {
    group = "build"
    description = "Creates an uberJar."

    destinationDirectory = layout.buildDirectory.dir("uberJars")
    archiveClassifier = "uber"

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    }) {
    }

    exclude("module-info.class")
    exclude("**/module-info.class")
    duplicatesStrategy = DuplicatesStrategy.WARN

    manifest {
        attributes("Main-Class" to "cypher.enforcers.uberjar.UberJarLauncher")
    }

    val isCI = providers.gradleProperty("isCI")
    doFirst {
        if (isCI.isPresent) {
            if (os.isWindows) {
                archiveClassifier = "uber-win"
            } else if (os.isLinux) {
                archiveClassifier = "uber-linux"
            } else if (os.isMacOsX) {
                archiveClassifier = "uber-mac-intel"
            }
        }
    }

}

// Disclaimer: This task only works on a Mac when on GitHub Actions.
//
// Modify the JLink extension to use the M1 data.
val m1JPackageTask: TaskProvider<Task> = tasks.register("jpackageM1") {
    group = "build"
    description = "Create an installer for MacOS with the M1 Architecture."

    val ext: JlinkPluginExtension? = project.extensions.findByType(JlinkPluginExtension::class)
    doFirst {
        ext?.configuration = "m1Configuration"
        ext?.jpackage {
            outputDir = "jpackage-m1"
            installerName = project.name + "-m1"
        }
    }

    finalizedBy(tasks.getByPath(":backup-code-generator:jpackage"))
}

// Disclaimer: This task only works on a Mac when on GitHub Actions.
//
// Task to create an uber or fat jar for M1.
val m1UberJarTask: TaskProvider<Jar> = tasks.register<Jar>("uberJarM1") {
    val isCI = providers.gradleProperty("isCI")

    if (!os.isMacOsX || !isCI.isPresent) {
        return@register
    }

    group = "build"
    description = "Creates an uberJar for a mac using the M1 processor."

    destinationDirectory = layout.buildDirectory.dir("m1UberJar")
    archiveClassifier = "uber-mac-m1"

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)

    val m1Configuration: NamedDomainObjectProvider<Configuration> =
        configurations.named("m1Configuration")

    dependsOn(m1Configuration)

    from({
        m1Configuration.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    }) {
    }

    exclude("module-info.class")
    exclude("**/module-info.class")
    duplicatesStrategy = DuplicatesStrategy.WARN

    manifest {
        attributes("Main-Class" to "cypher.enforcers.uberjar.UberJarLauncher")
    }
}

// Generate checksums for jars (that don't
// depend on the OS) that will be uploaded when
// on release.
tasks.register<Checksum>("generateJarsChecksums") {
    val jarTasks: List<String> = listOf("jar", "sourcesJar", "javadocJar")

    jarTasks.forEach {
        inputFiles.from(tasks.getByName(it).outputs.files)
    }

    appendFileNameToChecksum = true
    outputDirectory = layout.buildDirectory.dir("checksums")
}

// Generate checksums for files (that do
// depend on the OS) that will be uploaded when
// on release.
tasks.register<Checksum>("generateAppChecksums") {
    inputFiles.from(uberJarTask.get().outputs.files)
    dependsOn(tasks.getByPath(":backup-code-generator:jpackage").path)
    inputFiles.from(
        layout.buildDirectory.dir("jpackage")
            .get().asFileTree
            .matching {
                include("*.msi")
                include("*.dmg")
                include("*.deb")
                include("*.rpm")
            }
    )

    appendFileNameToChecksum = true
    outputDirectory = layout.buildDirectory.dir("checksums")
}

// Generate checksums for files needed on macOS for the
// M1 processor.
tasks.register<Checksum>("generateM1Checksums") {
    inputFiles.from(m1UberJarTask.get().outputs.files)
    dependsOn(tasks.getByPath(":backup-code-generator:jpackageM1").path)
    dependsOn(tasks.getByPath(":backup-code-generator:jpackage").path)
    inputFiles.from(
        layout.buildDirectory.dir("jpackage-m1")
            .get().asFileTree
            .matching {
                include("*.dmg")
            }
    )

    appendFileNameToChecksum = true
    outputDirectory = layout.buildDirectory.dir("checksums")
}
