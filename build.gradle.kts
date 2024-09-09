

plugins {

    `java-gradle-plugin`;
}



repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/");
    maven("https://jitpack.io");
    maven("https://repo.mikeprimm.com/");
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT");
    compileOnly("com.github.MilkBowl:VaultAPI:1.7");
    compileOnly("us.dynmap:dynmap-api:3.1-beta-2");

    implementation("org.junit.jupiter:junit-jupiter:5.11.0");
    implementation("com.github.seeseemelk:MockBukkit-v1.21:3.107.0");
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.test {
    useJUnitPlatform()
}