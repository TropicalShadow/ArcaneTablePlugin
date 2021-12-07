import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "me.tropicalshadow"
version = "1.4-SNAPSHOT"

repositories {
    mavenCentral()

    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "minecraft-repo"
        url = uri("https://libraries.minecraft.net/")
    }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }

}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.21")


}

tasks {
    withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveFileName.set("ArcaneTable.jar")
        relocate("kotlin", "com.github.tropicalshadow.arcanetable.dependencies.kotlin")
        relocate("kotlinx", "com.github.tropicalshadow.arcanetable.dependencies.kotlinx")
        relocate("org.jetbrains", "com.github.tropicalshadow.arcanetable.dependencies.jetbrains")
        relocate("org.intellij", "com.github.tropicalshadow.arcanetable.dependencies.jetbrains.intellij")
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