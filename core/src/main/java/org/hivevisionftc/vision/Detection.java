package org.hivevisionftc.vision;

/** A single detected game element, located in field coordinates. */
public final class Detection {
    private final Pose pose;
    private final double tx;
    private final double ty;
    private final double confidence;
    private final double area;
    private final int classId;

    /**
     * @param pose where the element sits on the field (inches)
     * @param tx horizontal angle from the camera crosshair to the element, in degrees (positive is right)
     * @param ty vertical angle from the camera crosshair to the element, in degrees (positive is up)
     * @param confidence detector confidence, from 0 to 1
     * @param area how much of the image the element fills, as reported by the camera
     * @param classId the detector's class id
     */
    public Detection(Pose pose, double tx, double ty, double confidence, double area, int classId) {
        this.pose = pose;
        this.tx = tx;
        this.ty = ty;
        this.confidence = confidence;
        this.area = area;
        this.classId = classId;
    }

    /** Field position of the element, in inches. */
    public Pose getPose() {
        return pose;
    }

    public double getX() {
        return pose.getX();
    }

    public double getY() {
        return pose.getY();
    }

    public double getTx() {
        return tx;
    }

    public double getTy() {
        return ty;
    }

    public double getConfidence() {
        return confidence;
    }

    public double getArea() {
        return area;
    }

    public int getClassId() {
        return classId;
    }

    public BiobuzzClasses getType() {
        return BiobuzzClasses.fromId(classId);
    }
}
