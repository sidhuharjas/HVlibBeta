package org.hivevisionftc.vision;

/** An immutable 2D pose: field inches and a heading in radians (counter-clockwise from +X). */
public final class Pose {
    private final double x;
    private final double y;
    private final double heading;

    public Pose(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
    }

    public Pose(double x, double y) {
        this(x, y, 0);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeading() {
        return heading;
    }

    /** Straight-line distance, in inches, from this pose to {@code other}. */
    public double distanceTo(Pose other) {
        return Math.hypot(other.x - x, other.y - y);
    }

    @Override
    public String toString() {
        return "Pose(x=" + x + ", y=" + y + ", heading=" + heading + ")";
    }
}
