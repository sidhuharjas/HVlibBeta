# HiveVision Weaver

Optional module that plans a [Marrow Weaver](https://marrow.skeletonarmyftc.com/automations/weaver) route
through a list of detections.

Not on Maven Central yet: build `weaver-release.aar` with `./gradlew assembleRelease` and add it to
`TeamCode/libs/` (the main README covers this). Once published:

```gradle
implementation 'io.github.sidhuharjas:weaver:1.0.0'   // pulls in :core and Marrow
```

```java
import com.skeletonarmy.marrow.weaver.PedroPathingConverter; // Marrow, not com.pedropathing

List<Detection> detections = vision.fetchDetections(robotPose);

PathResult path = WeaverRoute.from(robotPose)
        .avoid(new CircleZone(new Point(72, 72), 18))   // optional obstacles
        .maxTargets(4)                                  // optional cap
        .plan(detections);

if (path != null) {
    follower.follow(PedroPathingConverter.toPath(path));
}
```

- `plan(...)` returns `null` when the list is empty.
- Detections must be in field coordinates, which is what `fetchDetections(...)` returns.
- `ordered()` visits detections in the order given, and `end(pose)` sets where the route finishes (position
  only, since the route ends on the path tangent).
