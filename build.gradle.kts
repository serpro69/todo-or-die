import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager
import java.net.URI

plugins {
    kotlin("multiplatform") version "1.4.20"
    `maven-publish`
}

group = findProperty("group").toString()
version = findProperty("version").toString()

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = URI("https://kotlin.bintray.com/kotlinx/")
    }
}

base {
    archivesBaseName = "todo-or-die"
}

kotlin {
    jvm {
//        fun jdkPath(version: Int): String {
//            fun envOrProperty(name: String): String? = System.getenv(name) ?: findProperty(name) as String?
//
//            return envOrProperty("JDK_$version") ?: version.takeIf { it < 9 }?.let { envOrProperty("JDK_1$version") }
//            ?: error("Specify path to JDK $version in JDK_$version environment variable or Gradle property")
//        }
//
//        val JDK_8 by ext(jdkPath(8))

        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
        }

        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
//                jdkHome = JDK_8
            }
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js {
        nodejs {}

        compilations.all {
            kotlinOptions {
                sourceMap = true
                moduleKind = "umd"
                metaInfo = true
            }
        }
    }

    val nativeTarget = when {
        HostManager.hostIsMac -> macosX64("macos")
        HostManager.hostIsLinux -> linuxX64("linux")
        HostManager.hostIsMingw -> mingwX64("mingw")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        targets.withType<KotlinNativeTarget> {
            named("${name}Main") {
                kotlin.srcDir("src/nativeMain/kotlin")
                resources.srcDir("src/nativeMain/resources")
            }

            named("${name}Test") {
                kotlin.srcDir("src/nativeTest/kotlin")
                resources.srcDir("src/nativeTest/resources")
            }
        }
    }
}

publishing {
//        https://github.com/gradle/gradle/issues/11412#issuecomment-555413327
    System.setProperty("org.gradle.internal.publish.checksums.insecure", "true")

    val vcs: String by project
    val bintrayOrg: String by project
    val bintrayRepository: String by project
    val bintrayPackage: String by project

    repositories {
        maven {
            name = "bintray"
            url =
                URI("https://api.bintray.com/maven/$bintrayOrg/$bintrayRepository/$bintrayPackage/;publish=0;override=0")
            credentials {
                username = findProperty("bintrayUser") as String?
                password = findProperty("bintrayKey") as String?
            }
        }
    }

    publications {
        val kotlinMultiplatform by getting

        kotlinMultiplatform.closureOf<Publication> {
            artifacts {
                add(kotlinMultiplatform.name, "sources") {
                    builtBy("metadataSourcesJar")
                }
            }
        }
    }
    publications.withType<MavenPublication> {
        pom {
            name.set(project.name)
            description.set(project.description)
            url.set(vcs)
            licenses {
                license {
                    name.set("MIT")
                    url.set("$vcs/blob/master/LICENCE.md")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set(bintrayOrg)
                    name.set("Sergii Prodan")
                }
            }
            scm {
                connection.set("$vcs.git")
                developerConnection.set("$vcs.git")
                url.set(vcs)
            }
        }
    }
}

val taskPrefixes = when {
    HostManager.hostIsLinux -> listOf(
        "publishLinux",
        "publishJs",
        "publishJvm",
        "publishMetadata",
        "publishKotlinMultiplatform"
    )
    HostManager.hostIsMac -> listOf("publishMacos", "publishIos")
    HostManager.hostIsMingw -> listOf("publishMingw")
    else -> error("Unknown host, abort publishing.")
}

val publishTasks = tasks.withType<PublishToMavenRepository>().matching { task ->
    taskPrefixes.any { task.name.startsWith(it) }
}

tasks.register("publishAll") { dependsOn(publishTasks) }
