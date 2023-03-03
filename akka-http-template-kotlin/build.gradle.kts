import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    application
}

group = "me.lhe"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.typesafe.akka:akka-http_2.13:10.1.11")
    implementation("com.typesafe.akka:akka-http-jackson_2.13:10.1.11")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.+")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.10.+")
    implementation("com.typesafe.akka:akka-actor-typed_2.13:2.6.4")
    implementation("com.typesafe.akka:akka-stream_2.13:2.6.4")
    implementation("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("com.typesafe.akka:akka-http-testkit_2.13:10.1.11")
    testImplementation("com.typesafe.akka:akka-actor-testkit-typed_2.13:2.6.4")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}
