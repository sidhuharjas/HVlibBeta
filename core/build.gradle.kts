android {
    namespace = "org.hivevisionftc"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        // The suites run on the JVM; Android framework calls (Log, etc.) return defaults instead of throwing.
        unitTests.isReturnDefaultValues = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    // Supplied by the robot project at runtime.
    compileOnly("org.firstinspires.ftc:RobotCore:12.0.0")
    compileOnly("org.firstinspires.ftc:Hardware:12.0.0")

    testImplementation("org.firstinspires.ftc:RobotCore:12.0.0") { isTransitive = false }
    testImplementation("org.firstinspires.ftc:Hardware:12.0.0") { isTransitive = false }
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}
