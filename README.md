# HiveVision

Camera-based detection, target filtering, field positioning, and Weaver path routing library for FTC robots.

`HiveVision` turns Limelight 3A neural network detections into field coordinates and, optionally, obstacle-avoiding paths for autonomous. It knows the Biobuzz game elements (`POLLEN`, `NECTAR_RED`, `NECTAR_BLUE`) and works with [Pedro Pathing](https://github.com/pedropathing/core) and [Marrow Weaver](https://marrow.skeletonarmyftc.com/).

---

## Modules

`HiveVision` is split into two lightweight artifacts:

| Artifact | Description | Dependencies |
|----------|-------------|--------------|
| `io.github.sidhuharjas:core` | Perception pipeline, Limelight 3A integration, detection filtering, and camera-to-field coordinate projection. | FTC SDK, provided by your robot project |
| `io.github.sidhuharjas:weaver` | Optional path routing module that converts detection lists into obstacle-avoiding [Marrow Weaver](https://marrow.skeletonarmyftc.com/) bezier paths. | `:core`, `Marrow core` |

---

## Installation

HiveVision is **not published yet**, so `io.github.sidhuharjas:core:1.0.0` does not resolve from Maven Central.
Build the archives from this repository and drop them into your robot project's `TeamCode/libs/`:

```bash
./gradlew assembleRelease
```

| Archive | Location |
|---------|----------|
| `core-release.aar` | `core/build/outputs/aar/` |
| `weaver-release.aar` | `weaver/build/outputs/aar/` (optional) |

```gradle
dependencies {
    implementation files('libs/core-release.aar')
    implementation files('libs/weaver-release.aar') // optional, needs :core as well
}
```

The AARs leave the FTC SDK out on purpose: your TeamCode module already provides it. `:weaver` also needs
[Marrow](https://marrow.skeletonarmyftc.com/), so keep `mavenCentral()` in `repositories` and add
`implementation 'com.skeletonarmyftc.marrow:core:1.2.2'` next to it (Marrow is not transitive).

Once the artifacts are published, the plain coordinates work instead:

```gradle
implementation 'io.github.sidhuharjas:core:1.0.0'
implementation 'io.github.sidhuharjas:weaver:1.0.0'
```

---

## Quickstart

### 1. Configure

```java
VisionConfig config = new VisionConfig()
        .cameraHeightIn(9.2)        // lens height above the floor
        .targetHeightIn(3.0)        // center of the game element above the floor
        .cameraPitchDeg(-25.0)      // negative looks down
        .cameraForwardIn(4.0)       // lens ahead of robot center
        .cameraLeftIn(0.0)          // lens left of robot center
        .minConfidence(0.44)
        .maxStalenessMs(120)
        .limelightPipeline(3)
        .limelightPollingRateHz(150);

Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");
HiveVision vision = new HiveVision(limelight, config)
        .setAllowedClasses(BiobuzzClasses.NECTAR_RED, BiobuzzClasses.NECTAR_BLUE);
```

### 2. Find targets

```java
// Pedro Pathing 3.x accessors; 2.x used follower.getPose().getX()
Pose robotPose = new Pose(follower.pose().x(), follower.pose().y(), follower.pose().heading());

List<Detection> detections = vision.fetchDetections(robotPose); // nearest first

if (!detections.isEmpty()) {
    Detection target = detections.get(0);
    telemetry.addData("Class", target.getType());
    telemetry.addData("Field X", target.getX());
    telemetry.addData("Field Y", target.getY());
}
```

### 3. Plan a route (optional, needs `:weaver`)

```java
import com.skeletonarmy.marrow.weaver.PedroPathingConverter; // Marrow, not com.pedropathing

PathResult path = WeaverRoute.from(robotPose)
        .avoid(new CircleZone(new Point(72, 72), 18.0))
        .maxTargets(4)
        .plan(detections);

if (path != null) {
    follower.follow(PedroPathingConverter.toPath(path));
}
```

---

## Calibration

Field positions come from the camera's mounting geometry, so measure it carefully. See the
[calibration guide](https://sidhuharjas.github.io/HVlibBeta/calibration/) for the procedure, units, and frames.

---

## Documentation & Examples

- **[Docs site](https://sidhuharjas.github.io/HVlibBeta/)** — quickstart, module reference, calibration, and Javadoc.
- **[weaver/README.md](weaver/README.md)** — Quick guide to using the `WeaverRoute` extension.
- **[docs/examples/](docs/examples/)** — Sample OpModes demonstrating integration with Pedro Pathing.

---

## Building and Testing

```bash
# Run unit test suites (JVM-based, no camera required)
./gradlew test

# Format code according to Spotless Java rules
./gradlew spotlessApply

# Check code formatting
./gradlew spotlessCheck

# Build release library archives (AARs only; the publish tasks also attach sources and javadocs)
./gradlew assembleRelease
```

---

## Publishing to Maven Central

Publishing uses the Deployer plugin configured in `build.gradle.kts`.

| Gradle Task | Description |
|-------------|-------------|
| `./gradlew deployLocal` | Publishes artifacts to Maven Local (`~/.m2`) for testing. |
| `./gradlew deployNexusSnapshot` | Publishes snapshot builds to Sonatype Nexus. |
| `./gradlew deployCentralPortal` | Publishes release builds to Maven Central. |

The deploy tasks need `SIGNING_KEY`, `SIGNING_PASSWORD`, `CENTRAL_PORTAL_USER`, and `CENTRAL_PORTAL_PASSWORD`
secrets. Without them they fail during signing configuration, before anything is uploaded.

---

## License

MIT License. See [LICENSE](LICENSE) for details.
