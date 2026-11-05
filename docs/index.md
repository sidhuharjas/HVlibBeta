---
title: Overview
layout: default
---

# HiveVision

Camera-based detection, target filtering, field positioning, and Weaver path routing for FTC robots.

`HiveVision` turns Limelight 3A neural network detections into field coordinates and, optionally,
obstacle-avoiding paths for autonomous. It knows the Biobuzz game elements (`POLLEN`, `NECTAR_RED`,
`NECTAR_BLUE`) and works with [Pedro Pathing](https://github.com/pedropathing/core) and
[Marrow Weaver](https://marrow.skeletonarmyftc.com/).

## Modules

| Artifact | Description |
|----------|-------------|
| `org.hivevisionftc:core` | Perception pipeline, Limelight 3A integration, detection filtering, and camera-to-field projection. |
| `org.hivevisionftc:weaver` | Optional: turns detection lists into obstacle-avoiding Marrow Weaver paths. |

## Documentation

- [Module reference](MODULES.md) - the full API for `:core` and `:weaver`.
- [Calibration](calibration.md) - measuring the camera geometry, units, and coordinate frames.

## Installing

HiveVision is not on Maven Central yet. Build the archives with `./gradlew assembleRelease` and add
them to your robot project's `TeamCode/libs/`. The README in this repository has the exact dependency
lines.
