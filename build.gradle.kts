import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "2.1.0"
}


repositories {
    mavenCentral()

    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://libraries.minecraft.net/")

}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.21")
}

tasks {
    runServer {
        minecraftVersion("1.20.2")
    }

    test{
        workingDir = file("run")
    }

    withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveFileName.set("ArcaneTable-"+project.version+".jar")
        exclude("DebugProbesKt.bin")
        exclude("META-INF/**")
    }

    processResources {
        filter<ReplaceTokens>("tokens" to mapOf("version" to project.version))
    }
}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}