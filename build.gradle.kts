import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {

    `java-gradle-plugin`;
    id("jacoco");
    id("info.solidsoft.pitest") version "1.9.11";
    id("com.github.johnrengelman.shadow") version "8.1.1";
    kotlin("jvm") version "1.9.22"
    kotlin("kapt") version "1.9.10"
}

jacoco {
    toolVersion = "0.8.8"
}



tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}


repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/");
    maven("https://jitpack.io");
    maven("https://repo.mikeprimm.com/");
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/");
    maven("https://oss.sonatype.org/content/repositories/snapshots");
    maven("https://oss.sonatype.org/content/repositories/central");
    maven("https://repo1.maven.org/maven2/com/github/seeseemelk/MockBukkit-v1.19");
    maven("https://repo.essentialsx.net/releases/");
}

dependencies {

    compileOnly("org.apache.commons:commons-lang3:3.14.0")

    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT");
    compileOnly("com.github.MilkBowl:VaultAPI:1.7");
    compileOnly("us.dynmap:dynmap-api:3.1-beta-2");

    /*
    * Supported Versions 1.18-1.20
    *
    * Source: https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-1-16/
    *
    * */

    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT");
    compileOnly("net.essentialsx:EssentialsX:2.20.1");
    testImplementation("net.essentialsx:EssentialsX:2.20.1");

    implementation("com.google.code.gson:gson:2.13.1");
    implementation("org.danilopianini:gson-extras:0.2.1");

    testImplementation("org.mockito:mockito-core:5.2.0")
    implementation("org.junit.jupiter:junit-jupiter:5.11.3");

    implementation("com.github.seeseemelk:MockBukkit-v1.19:3.1.0")
    implementation("com.github.MilkBowl:VaultAPI:1.7");
    implementation("us.dynmap:dynmap-api:3.1-beta-2");
    implementation("org.javatuples:javatuples:1.2");
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.yaml:snakeyaml:2.0")

    // Ebean ORM
    implementation("io.ebean:ebean:13.23.1")
    // Ebean DDL / Migrations
    implementation("io.ebean:ebean-ddl-generator:13.23.1")
    // SQLite JDBC (recommended for plugins)
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
    // JPA API
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    kapt("io.ebean:ebean-agent:13.23.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(18))
}



tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    jvmArgs = listOf("-Duser.language=nl", "-Duser.country=NL")
}


tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}


tasks {
    shadowJar {
        archiveClassifier.set("")
        isZip64 = true

        dependencies {
            include(dependency("org.danilopianini:gson-extras"))
        }

    }

    build {
        dependsOn(shadowJar)
    }
}
