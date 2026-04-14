plugins {
    kotlin("jvm") version "1.9.23"
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.appium:java-client:9.2.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.19.1")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}