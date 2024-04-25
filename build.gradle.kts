import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    `maven-publish`
}

group = "cool.but"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.0")
    implementation("com.dtflys.forest:forest-spring-boot3-starter:1.5.36")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// 自定义源码打包任务
tasks.register<Jar>("sourcesJar") {
    // 指明要打的 jar 包名称
    // 最终打包的名称是 <projectName>-<version>-sources.jar
    archiveBaseName.set("${project.name}-${project.version}")
    archiveClassifier.set("sources")
    // 设置打包哪些文件
    from(sourceSets["main"].allSource)
}

// 自定义文档打包任务
tasks.register<Jar>("javadocJar") {
    // 指明要打的 jar 包名称
    // 最终打包的名称是 <projectName>-<version>-javadoc.jar
    archiveBaseName.set("${project.name}-${project.version}")
    archiveClassifier.set("javadoc")
    // 设置打包哪些文件
    from(tasks.getByName<Javadoc>("javadoc").destinationDir)
}


publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/butfool/ObsidianRestfulAPI")
            credentials {
                username = System.getenv("GITHUB_REPO_USER")
                password = System.getenv("GITHUB_REPO_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
            // 附带源码
            artifact(tasks.getByName<Jar>("sourcesJar"))
            // 附带文档
            artifact(tasks.getByName<Jar>("javadocJar"))
        }
    }
}