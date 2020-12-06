import java.net.URI

plugins {
    kotlin("multiplatform") version "1.4.20"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.5"
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

//  https://github.com/gradle/gradle/issues/11412#issuecomment-555413327
    System.setProperty("org.gradle.internal.publish.checksums.insecure", "true")

    publishing {
        repositories {
            maven {
                val bintrayUser = findProperty("bintrayUser") as String?
                val bintrayKey = findProperty("bintrayKey") as String?

                setUrl("https://api.bintray.com/maven/serpro69/maven/todo-or-die/;publish=1;override=1")

                credentials {
                    username = bintrayUser
                    password = bintrayKey
                }
            }
        }

        publications {
            filterIsInstance<MavenPublication>().forEach { publication ->
                publication.pom {
                    name.set(project.name)
                    description.set(project.description)
                    packaging = "jar"
                    url.set("https://github.com/serpro69/${project.name}")
                    developers {
                        developer {
                            id.set("serpro69")
                            name.set("Sergii Prodan")
                            email.set("serpro@disroot.org")
                        }
                    }
                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://github.com/serpro69/${project.name}/blob/master/LICENCE.md")
                        }
                    }
                    scm {
                        connection.set("scm:git:https://github.com/serpro69/${project.name}.git")
                        developerConnection.set("scm:git:git@github.com:serpro69/${project.name}.git")
                        url.set("https://github.com/serpro69/${project.name}")
                    }
                }
            }
        }
    }
}