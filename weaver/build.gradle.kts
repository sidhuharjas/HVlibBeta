android {
    namespace = "org.hivevisionftc.vision.weaver"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
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
    // Public API takes and returns core types, so core is an api dependency.
    api(project(":core"))

    // Public API returns Marrow's PathResult and takes its Zone, so consumers need it on their compile classpath.
    // Non-transitive: only the weaver/zones packages are used, which keeps Marrow's JSON layer (Jackson)
    // off the robot.
    api("com.skeletonarmyftc.marrow:core:1.2.2") { isTransitive = false }

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}
