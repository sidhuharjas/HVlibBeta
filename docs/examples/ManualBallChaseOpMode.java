package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.skeletonarmy.marrow.weaver.PathResult;
import com.skeletonarmy.marrow.weaver.PedroPathingConverter;
import com.skeletonarmy.marrow.zones.CircleZone;
import com.skeletonarmy.marrow.zones.Point;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import java.util.List;
import org.hivevisionftc.vision.BiobuzzClasses;
import org.hivevisionftc.vision.Detection;
import org.hivevisionftc.vision.HiveVision;
import org.hivevisionftc.vision.Pose;
import org.hivevisionftc.vision.VisionConfig;
import org.hivevisionftc.vision.weaver.WeaverRoute;

/**
 * Autonomous OpMode demonstrating continuous detection loop, obstacle avoidance,
 * and path execution using HiveVision and WeaverRoute.
 */
@Autonomous(name = "HiveVision Loop with Obstacles")
public class ManualBallChaseOpMode extends LinearOpMode {

    @Override
    public void runOpMode() {
        Follower follower = SampleFollowerFactory.create(hardwareMap);
        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");

        VisionConfig config = new VisionConfig()
                .cameraPitchDeg(-25.0)
                .cameraHeightIn(9.2)
                .targetHeightIn(3.0)
                .cameraForwardIn(4.0)
                .cameraLeftIn(0.0)
                .minConfidence(0.40)
                .maxStalenessMs(120);

        HiveVision vision = new HiveVision(limelight, config)
                .setAllowedClasses(BiobuzzClasses.NECTAR_RED, BiobuzzClasses.NECTAR_BLUE);

        telemetry.addData("Status", "Ready - press START");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            follower.update();

            // Pedro Pathing 3.x accessors; 2.x used follower.getPose().getX()
            Pose currentPose = new Pose(
                    follower.pose().x(),
                    follower.pose().y(),
                    follower.pose().heading());

            List<Detection> detections = vision.fetchDetections(currentPose);

            if (!detections.isEmpty()) {
                Detection closest = detections.get(0);
                telemetry.addData("Closest Element", closest.getType());
                telemetry.addData("Field X", closest.getX());
                telemetry.addData("Field Y", closest.getY());

                if (!follower.isBusy()) {
                    PathResult path = WeaverRoute.from(currentPose)
                            .avoid(new CircleZone(new Point(72, 72), 18.0))
                            .maxTargets(3)
                            .plan(detections);

                    if (path != null) {
                        follower.follow(PedroPathingConverter.toPath(path));
                    }
                }
            } else {
                telemetry.addData("Status", "Searching for targets...");
            }

            telemetry.update();
        }
    }
}
