package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.skeletonarmy.marrow.weaver.PathResult;
import com.skeletonarmy.marrow.weaver.PedroPathingConverter;
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
 * Autonomous OpMode demonstrating target detection and Weaver route generation using HiveVision.
 */
@Autonomous(name = "HiveVision Weaver Auto")
public class AutoBallHuntOpMode extends LinearOpMode {

    @Override
    public void runOpMode() {
        Follower follower = SampleFollowerFactory.create(hardwareMap);
        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // Define vision configuration parameters
        VisionConfig config = new VisionConfig()
                .cameraPitchDeg(-25.0)
                .cameraHeightIn(9.2)
                .targetHeightIn(3.0)
                .cameraForwardIn(4.0)
                .cameraLeftIn(0.0)
                .minConfidence(0.44)
                .maxStalenessMs(120);

        // Initialize HiveVision session
        HiveVision vision = new HiveVision(limelight, config)
                .setAllowedClasses(BiobuzzClasses.NECTAR_RED, BiobuzzClasses.NECTAR_BLUE);

        telemetry.addData("Status", "Ready - press START");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            // Obtain current robot pose
            // Pedro Pathing 3.x accessors; 2.x used follower.getPose().getX()
            Pose robotPose = new Pose(
                    follower.pose().x(),
                    follower.pose().y(),
                    follower.pose().heading());

            // Fetch detections and generate a Weaver path route
            List<Detection> detections = vision.fetchDetections(robotPose);
            PathResult pathResult = WeaverRoute.from(robotPose).maxTargets(2).plan(detections);

            if (pathResult != null) {
                follower.follow(PedroPathingConverter.toPath(pathResult));
                while (opModeIsActive() && follower.isBusy()) {
                    follower.update();
                    telemetry.addData("Status", "Following generated route");
                    telemetry.update();
                }
            } else {
                telemetry.addData("Status", "No targets detected");
                telemetry.update();
            }
        }
    }
}
