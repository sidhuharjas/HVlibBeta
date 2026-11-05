# Module reference

HiveVision is two artifacts:

- **`:core`** (`org.hivevisionftc.vision`): Limelight access, detection filtering, and camera-to-field projection.
- **`:weaver`** (`org.hivevisionftc.vision.weaver`): plans [Marrow Weaver](https://marrow.skeletonarmyftc.com/)
  routes through detections. Optional.

## Core

### `HiveVision`

```java
HiveVision(Limelight3A limelight, VisionConfig config)
HiveVision(DetectionSource source, VisionConfig config)

HiveVision setAllowedClasses(BiobuzzClasses... classes)
List<Detection> fetchDetections(Pose robotPose)
```

`fetchDetections` returns a new list, nearest first. It is empty when the data is older than `maxStalenessMs`. It
drops detections below `minConfidence` and, if `setAllowedClasses` was called, detections of other classes.
Calling `setAllowedClasses()` with no arguments allows everything again.

### `VisionConfig`

Camera geometry and thresholds, set through chained calls. See [calibration.md](calibration.md) for what each value
means and how to measure it.

| Method | Default |
|--------|---------|
| `cameraPitchDeg`, `cameraHeightIn`, `cameraForwardIn`, `cameraLeftIn`, `targetHeightIn` | `0` |
| `minConfidence` | `0.5` |
| `maxStalenessMs` | `1000` |
| `limelightPipeline` | `0` |
| `limelightPollingRateHz` | `100` (1 to 250) |

### `Detection`

A detection in field coordinates.

| Method | Returns |
|--------|---------|
| `getPose()`, `getX()`, `getY()` | Field position, inches |
| `getType()`, `getClassId()` | `BiobuzzClasses` and the detector's raw id |
| `getConfidence()`, `getArea()` | Detector output |
| `getTx()`, `getTy()` | Camera angles, degrees |

### `Pose`

Immutable position (inches) and heading (radians). Has `distanceTo(Pose)`.

### `BiobuzzClasses`

`POLLEN` (0), `NECTAR_RED` (1), `NECTAR_BLUE` (2), `UNKNOWN`. Look one up with `BiobuzzClasses.fromId(int)`.

### `DetectionSource`, `LimelightSource`, `FieldProjector`

`DetectionSource` is the camera backend interface. `LimelightSource` implements it for a Limelight 3A, and
`HiveVision` creates one for you. Implement the interface yourself to feed in another camera, or fake detections in
tests. `FieldProjector.project(...)` is the projection math, exposed for testing.

## Weaver

### `WeaverRoute`

```java
PathResult path = WeaverRoute.from(robotPose)
        .avoid(new CircleZone(new Point(72, 72), 18))
        .maxTargets(3)
        .plan(detections);
```

| Method | Effect |
|--------|--------|
| `from(Pose)` | Required. Where the route starts. |
| `avoid(Zone...)`, `avoid(Collection)` | Zones the route bends around. |
| `maxTargets(int)` | Visit at most this many detections. Zero or less means no limit. |
| `ordered()` | Visit in the order given instead of the cheapest order. |
| `end(Pose)` | Finish at this position. The heading is ignored: routes end on the path tangent. |
| `plan(List<Detection>)` | Returns a `PathResult`, or `null` if the list is empty. |
