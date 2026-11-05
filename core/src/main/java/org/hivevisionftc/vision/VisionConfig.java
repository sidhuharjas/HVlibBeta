package org.hivevisionftc.vision;

/**
 * Camera mounting geometry and detection thresholds. Every setter returns {@code this}, so calls can be chained.
 *
 * <p>Distances are inches and angles are degrees. The robot frame has +X forward and +Y to the left. See
 * {@code docs/calibration.md} for how to measure each value.
 */
public final class VisionConfig {
    private double cameraPitchDeg = 0;
    private double cameraHeightIn = 0;
    private double cameraForwardIn = 0;
    private double cameraLeftIn = 0;
    private double targetHeightIn = 0;
    private long maxStalenessMs = 1000;
    private double minConfidence = 0.5;
    private int limelightPipeline = 0;
    private int limelightPollingRateHz = 100;

    /** Pitch of the camera above the horizontal, in degrees. Negative if the camera looks down. */
    public VisionConfig cameraPitchDeg(double cameraPitchDeg) {
        this.cameraPitchDeg = cameraPitchDeg;
        return this;
    }

    /** Height of the camera lens above the floor. */
    public VisionConfig cameraHeightIn(double cameraHeightIn) {
        this.cameraHeightIn = cameraHeightIn;
        return this;
    }

    /** How far the camera lens sits ahead of the robot center. Negative if it is behind. */
    public VisionConfig cameraForwardIn(double cameraForwardIn) {
        this.cameraForwardIn = cameraForwardIn;
        return this;
    }

    /** How far the camera lens sits to the left of the robot center. Negative if it is to the right. */
    public VisionConfig cameraLeftIn(double cameraLeftIn) {
        this.cameraLeftIn = cameraLeftIn;
        return this;
    }

    /** Height of the center of the game element above the floor. The camera must be above this. */
    public VisionConfig targetHeightIn(double targetHeightIn) {
        this.targetHeightIn = targetHeightIn;
        return this;
    }

    /** Detections older than this are discarded. */
    public VisionConfig maxStalenessMs(long maxStalenessMs) {
        this.maxStalenessMs = maxStalenessMs;
        return this;
    }

    /**
     * Detections below this confidence (0 to 1) are discarded.
     *
     * <p>The Limelight also applies its own threshold in the web UI. If that one is higher, the camera never reports
     * the detection and this value cannot bring it back.
     */
    public VisionConfig minConfidence(double minConfidence) {
        if (minConfidence < 0 || minConfidence > 1) {
            throw new IllegalArgumentException("minConfidence must be between 0 and 1, was " + minConfidence);
        }
        this.minConfidence = minConfidence;
        return this;
    }

    /** Limelight pipeline index to run. */
    public VisionConfig limelightPipeline(int limelightPipeline) {
        this.limelightPipeline = limelightPipeline;
        return this;
    }

    /** How often, from 1 to 250 Hz, the control hub asks the Limelight for new data. */
    public VisionConfig limelightPollingRateHz(int limelightPollingRateHz) {
        if (limelightPollingRateHz < 1 || limelightPollingRateHz > 250) {
            throw new IllegalArgumentException(
                    "limelightPollingRateHz must be between 1 and 250, was " + limelightPollingRateHz);
        }
        this.limelightPollingRateHz = limelightPollingRateHz;
        return this;
    }

    public double getCameraPitchDeg() {
        return cameraPitchDeg;
    }

    public double getCameraHeightIn() {
        return cameraHeightIn;
    }

    public double getCameraForwardIn() {
        return cameraForwardIn;
    }

    public double getCameraLeftIn() {
        return cameraLeftIn;
    }

    public double getTargetHeightIn() {
        return targetHeightIn;
    }

    public long getMaxStalenessMs() {
        return maxStalenessMs;
    }

    public double getMinConfidence() {
        return minConfidence;
    }

    public int getLimelightPipeline() {
        return limelightPipeline;
    }

    public int getLimelightPollingRateHz() {
        return limelightPollingRateHz;
    }
}
