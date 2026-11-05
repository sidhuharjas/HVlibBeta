package org.hivevisionftc.vision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class FieldProjectorTest {
    private static final double EPS = 1e-6;

    /** Camera 10" up, tilted 45 degrees down, looking at things on the floor, 2" ahead of robot center. */
    private static VisionConfig config() {
        return new VisionConfig().cameraHeightIn(10).cameraPitchDeg(-45).cameraForwardIn(2);
    }

    @Test
    void crosshairHitsTheFloorAsFarAheadAsTheCameraIsHigh() {
        Pose found = FieldProjector.project(new Pose(0, 0, 0), config(), 0, 0);

        assertEquals(12.0, found.getX(), EPS, "10\" ahead of the camera, which is 2\" ahead of center");
        assertEquals(0.0, found.getY(), EPS);
    }

    @Test
    void positiveTxIsToTheRightOfTheRobot() {
        Pose found = FieldProjector.project(new Pose(0, 0, 0), config(), 45, 0);

        assertEquals(12.0, found.getX(), EPS);
        assertEquals(-Math.hypot(10, 10), found.getY(), EPS);
    }

    @Test
    void cameraOffsetToTheLeftShiftsTheResultLeft() {
        Pose found = FieldProjector.project(new Pose(0, 0, 0), config().cameraLeftIn(3), 0, 0);

        assertEquals(3.0, found.getY(), EPS);
    }

    @Test
    void resultFollowsTheRobotPoseAndHeading() {
        Pose found = FieldProjector.project(new Pose(5, 5, Math.PI / 2), config(), 0, 0);

        assertEquals(5.0, found.getX(), EPS, "facing +Y, so forward is field +Y");
        assertEquals(17.0, found.getY(), EPS);
    }

    @Test
    void targetHeightRaisesThePlaneTheRayIsIntersectedWith() {
        Pose found = FieldProjector.project(new Pose(0, 0, 0), config().targetHeightIn(4), 0, 0);

        assertEquals(8.0, found.getX(), EPS, "only 6\" of drop left, so 6\" ahead of the camera");
    }

    @Test
    void aRayThatNeverReachesTheTargetHeightIsDropped() {
        VisionConfig level = new VisionConfig().cameraHeightIn(10).cameraPitchDeg(0);

        assertNull(FieldProjector.project(new Pose(0, 0, 0), level, 0, 5));
    }

    @Test
    void aCameraBelowTheTargetCannotLocateIt() {
        VisionConfig low =
                new VisionConfig().cameraHeightIn(2).targetHeightIn(3).cameraPitchDeg(-45);

        assertNull(FieldProjector.project(new Pose(0, 0, 0), low, 0, 0));
    }
}
