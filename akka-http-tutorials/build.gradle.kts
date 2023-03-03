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
    implementation("ch.qos.logback:logback-classic:1.4.1")
    implementation("org.slf4j:log4j-over-slf4j:1.7.14")
    // Akka Actor
    implementation("com.typesafe.akka:akka-actor-typed_2.13:2.7.0")
    // Akka Stream
    implementation("com.typesafe.akka:akka-stream-typed_2.13:2.7.0")
    // Akka HTTP
    implementation("com.typesafe.akka:akka-http_2.13:10.2.10")
    implementation("com.typesafe.akka:akka-http-jackson_2.13:10.2.10")
    // test
    testImplementation(kotlin("test"))
    testImplementation("com.typesafe.akka:akka-actor-testkit-typed_2.13:2.7.0")

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