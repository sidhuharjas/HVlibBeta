package org.hivevisionftc.vision;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Entry point: fetches detections, drops the ones you don't want, and returns them in field coordinates. */
public final class HiveVision {
    private final DetectionSource source;
    private final VisionConfig config;
    private final Set<BiobuzzClasses> allowedClasses = EnumSet.noneOf(BiobuzzClasses.class);

    public HiveVision(DetectionSource source, VisionConfig config) {
        this.source = source;
        this.config = config;
    }

    public HiveVision(Limelight3A limelight, VisionConfig config) {
        this(new LimelightSource(limelight, config), config);
    }

    /** Restricts results to the given classes. With no classes given, everything is allowed again. */
    public HiveVision setAllowedClasses(BiobuzzClasses... classes) {
        allowedClasses.clear();
        allowedClasses.addAll(Arrays.asList(classes));
        return this;
    }

    /**
     * Fetches the latest detections, nearest first.
     *
     * <p>Detections are dropped if the data is older than {@link VisionConfig#getMaxStalenessMs()}, if the confidence
     * is below {@link VisionConfig#getMinConfidence()}, or if the class is not allowed.
     *
     * @param robotPose the robot's current pose, used to place detections on the field
     * @return a new list, possibly empty
     */
    public List<Detection> fetchDetections(Pose robotPose) {
        List<Detection> fetched = source.fetchLatestDetections(robotPose);
        if (source.stalenessMs() > config.getMaxStalenessMs()) {
            return new ArrayList<>();
        }

        List<Detection> kept = new ArrayList<>();
        for (Detection detection : fetched) {
            if (detection.getConfidence() >= config.getMinConfidence() && isAllowed(detection.getType())) {
                kept.add(detection);
            }
        }
        Collections.sort(kept, Comparator.comparingDouble(d -> d.getPose().distanceTo(robotPose)));
        return kept;
    }

    private boolean isAllowed(BiobuzzClasses type) {
        return allowedClasses.isEmpty() || allowedClasses.contains(type);
    }
}
