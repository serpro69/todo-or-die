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
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
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
        val nativeMain by getting
        val nativeTest by getting
    }

    plugins.withId("maven-publish") {
//        https://github.com/gradle/gradle/issues/11412#issuecomment-555413327
        System.setProperty("org.gradle.internal.publish.checksums.insecure", "true")

        configure<PublishingExtension> {
            val vcs: String by project
            val bintrayOrg: String by project
            val bintrayRepository: String by project
            val bintrayPackage: String by project

            repositories {
                maven {
                    name = "bintray"
                    url = URI("https://api.bintray.com/maven/$bintrayOrg/$bintrayRepository/$bintrayPackage/;publish=0;override=0")
                    credentials {
                        username = findProperty("bintrayUser") as String?
                        password = findProperty("bintrayKey") as String?
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
    }
}
