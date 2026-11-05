package org.hivevisionftc.vision;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import java.util.ArrayList;
import java.util.List;

/** {@link DetectionSource} backed by a Limelight 3A running a neural detector pipeline. */
public final class LimelightSource implements DetectionSource {
    private final Limelight3A limelight;
    private final VisionConfig config;
    private long stalenessMs = Long.MAX_VALUE;

    /** Selects the configured pipeline, sets the polling rate and starts the camera. */
    public LimelightSource(Limelight3A limelight, VisionConfig config) {
        this.limelight = limelight;
        this.config = config;

        limelight.pipelineSwitch(config.getLimelightPipeline());
        limelight.setPollRateHz(config.getLimelightPollingRateHz());
        limelight.start();
    }

    @Override
    public List<Detection> fetchLatestDetections(Pose robotPose) {
        List<Detection> detections = new ArrayList<>();
        LLResult result = limelight.getLatestResult();

        if (result == null || !result.isValid()) {
            stalenessMs = Long.MAX_VALUE;
            return detections;
        }
        stalenessMs = result.getStaleness();

        for (LLResultTypes.DetectorResult found : result.getDetectorResults()) {
            Pose fieldPose =
                    FieldProjector.project(robotPose, config, found.getTargetXDegrees(), found.getTargetYDegrees());
            if (fieldPose == null) {
                continue;
            }
            detections.add(new Detection(
                    fieldPose,
                    found.getTargetXDegrees(),
                    found.getTargetYDegrees(),
                    found.getConfidence(),
                    found.getTargetArea(),
                    found.getClassId()));
        }
        return detections;
    }

    @Override
    public long stalenessMs() {
        return stalenessMs;
    }
}
