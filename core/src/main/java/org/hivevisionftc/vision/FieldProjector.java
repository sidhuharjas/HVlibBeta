package org.hivevisionftc.vision;

/** Turns the camera angles of a detection into a position on the field. */
public final class FieldProjector {
    private FieldProjector() {}

    /**
     * Projects a camera ray onto the plane at the target's height.
     *
     * @param robotPose robot pose on the field (inches, radians)
     * @param config camera mounting geometry
     * @param txDeg horizontal angle from the crosshair, in degrees (positive is right)
     * @param tyDeg vertical angle from the crosshair, in degrees (positive is up)
     * @return the field position, or {@code null} if the ray never reaches the target's height (it points level or
     *     upward relative to the target)
     */
    public static Pose project(Pose robotPose, VisionConfig config, double txDeg, double tyDeg) {
        // Ray in the camera frame: x forward, y left, z up.
        double rayForward = 1.0;
        double rayLeft = -Math.tan(Math.toRadians(txDeg));
        double rayUp = Math.tan(Math.toRadians(tyDeg));

        // Rotate by the camera pitch (positive pitches the lens up) into a level, robot-aligned frame.
        double pitch = Math.toRadians(config.getCameraPitchDeg());
        double forward = rayForward * Math.cos(pitch) - rayUp * Math.sin(pitch);
        double up = rayForward * Math.sin(pitch) + rayUp * Math.cos(pitch);
        double left = rayLeft;

        double drop = config.getCameraHeightIn() - config.getTargetHeightIn();
        if (up >= 0 || drop <= 0) {
            return null;
        }

        double scale = drop / -up;
        double robotForward = config.getCameraForwardIn() + scale * forward;
        double robotLeft = config.getCameraLeftIn() + scale * left;

        double cos = Math.cos(robotPose.getHeading());
        double sin = Math.sin(robotPose.getHeading());
        return new Pose(
                robotPose.getX() + robotForward * cos - robotLeft * sin,
                robotPose.getY() + robotForward * sin + robotLeft * cos);
    }
}
