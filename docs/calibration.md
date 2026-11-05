---
layout: default
---

# Calibration

Distance and field position are computed from the camera's mounting geometry, so every value in
`VisionConfig` needs to be measured. A few millimeters of error in height or pitch becomes inches of error at range.

| Method | Meaning | Units |
|--------|---------|-------|
| `cameraHeightIn` | Floor to the center of the camera lens | inches |
| `targetHeightIn` | Floor to the center of the game element | inches |
| `cameraPitchDeg` | Tilt of the camera above horizontal. **Negative when it looks down.** | degrees |
| `cameraForwardIn` | Lens position ahead of the robot center (negative if behind) | inches |
| `cameraLeftIn` | Lens position left of the robot center (negative if right) | inches |
| `minConfidence` | Lowest detection confidence to keep (0 to 1) | |
| `maxStalenessMs` | Oldest data to keep | milliseconds |
| `limelightPipeline` | Detector pipeline index, as numbered in the Limelight web UI | index |
| `limelightPollingRateHz` | How often the SDK reads the camera (1 to 250) | Hz |

`limelightPipeline` and `limelightPollingRateHz` are applied when `HiveVision` is constructed. Changing them on a
`VisionConfig` afterwards has no effect, so set them before you build the `HiveVision`.

If the robot is modeled in CAD, take the measurements from there and check them against the real robot.

## Measuring

- **Height.** Measure straight up from the floor to the middle of the lens. Do the same for the game element.
- **Pitch.** Hold a digital angle gauge against the camera housing. A camera tilted 25 degrees toward the floor is
  `-25`.
- **Offsets.** Measure from the robot's center of rotation to the lens. Forward is positive, left is positive.

## Frames

The robot frame has +X forward and +Y to the left. Field positions use the same axes as Pedro Pathing, with the
robot's heading in radians counter-clockwise from +X.

The camera reports `tx` (positive to the right) and `ty` (positive up) as angles from the crosshair.
`FieldProjector` intersects that ray with the plane at the element's height, then moves the result from the robot
frame into the field frame using the robot pose passed to `fetchDetections`.

Elements whose ray never reaches the element's height, such as anything above the horizon, are dropped.

## Example

```java
VisionConfig config = new VisionConfig()
        .cameraHeightIn(9.25)
        .targetHeightIn(3.0)
        .cameraPitchDeg(-25.0)
        .cameraForwardIn(4.5)
        .cameraLeftIn(-1.2)     // 1.2" right of center
        .minConfidence(0.40)
        .maxStalenessMs(120);
```
