plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    id("io.ktor.plugin") version "3.0.3"
    id("org.flywaydb.flyway") version "9.22.3"
    application
}

flyway {
    url = "jdbc:postgresql://localhost:5432/cattery"
    user = "cattery"
    password = "cattery"
    locations = arrayOf("classpath:db/migration")
}

group = "com.cattery"
version = "1.0.0"

application {
    mainClass.set("com.cattery.ApplicationKt")
}

repositories {
    mavenCentral()
}

val ktorVersion = "3.0.3"
val exposedVersion = "0.56.0"
val flywayVersion = "9.22.3"

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.h2database:h2:2.3.232")
    implementation("com.zaxxer:HikariCP:6.2.1")

    implementation("org.flywaydb:flyway-core:$flywayVersion")

    implementation("org.mindrot:jbcrypt:0.4")
    implementation("com.auth0:java-jwt:4.4.0")

    implementation("ch.qos.logback:logback-classic:1.5.12")

    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<JavaExec> {
    standardInput = System.`in`
}

tasks.register<Exec>("stopServer") {
    group = "application"
    description = "Освободить порт 8080 (Windows)"
    commandLine(
        "powershell",
        "-NoProfile",
        "-Command",
        """
        ${'$'}p = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue |
            Select-Object -ExpandProperty OwningProcess -Unique
        if (${'$'}p) { ${'$'}p | ForEach-Object { Stop-Process -Id ${'$'}_ -Force -ErrorAction SilentlyContinue } }
        else { Write-Host 'Порт 8080 свободен' }
        """.trimIndent(),
    )
    isIgnoreExitValue = true
}

tasks.register<JavaExec>("runDev") {
    group = "application"
    description = "H2 in-memory"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set(application.mainClass)
    standardInput = System.`in`
}

tasks.register<JavaExec>("runProd") {
    group = "application"
    description = "PostgreSQL"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set(application.mainClass)
    args = listOf("-config=application-prod.conf")
    standardInput = System.`in`
}
