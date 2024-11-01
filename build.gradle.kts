

plugins {

    `java-gradle-plugin`;
    id("jacoco")
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
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT");
    compileOnly("com.github.MilkBowl:VaultAPI:1.7");
    compileOnly("us.dynmap:dynmap-api:3.1-beta-2");
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT");

    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation("org.mockito:mockito-core:5.2.0")
    implementation("org.junit.jupiter:junit-jupiter:5.11.0");

    testImplementation("org.opentest4j:opentest4j:1.3.0")
    implementation("com.github.seeseemelk:MockBukkit-v1.19:3.1.0")
    implementation("com.github.MilkBowl:VaultAPI:1.7");
    implementation("us.dynmap:dynmap-api:3.1-beta-2");
    implementation("org.javatuples:javatuples:1.2");
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(18))
}


tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

