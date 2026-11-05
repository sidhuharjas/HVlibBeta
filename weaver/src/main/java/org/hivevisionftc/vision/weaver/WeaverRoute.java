package org.hivevisionftc.vision.weaver;

import com.skeletonarmy.marrow.weaver.PathResult;
import com.skeletonarmy.marrow.weaver.Weaver;
import com.skeletonarmy.marrow.weaver.WeaverBuilder;
import com.skeletonarmy.marrow.zones.Point;
import com.skeletonarmy.marrow.zones.Zone;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.hivevisionftc.vision.Detection;
import org.hivevisionftc.vision.Pose;

/**
 * Plans a Marrow Weaver route through a list of detections.
 *
 * <pre>{@code
 * List<Detection> detections = vision.fetchDetections(pose);
 *
 * PathResult path = WeaverRoute.from(pose)
 *         .avoid(new CircleZone(new Point(72, 72), 18))
 *         .maxTargets(4)
 *         .plan(detections);
 *
 * if (path != null) follower.follow(PedroPathingConverter.toPath(path));
 * }</pre>
 *
 * <p>Detections must already be in field coordinates, which is what {@code HiveVision.fetchDetections} returns.
 */
public final class WeaverRoute {
    private final Pose start;
    private final List<Zone> obstacles = new ArrayList<>();
    private Pose end;
    private int maxTargets;
    private boolean ordered;

    private WeaverRoute(Pose start) {
        this.start = start;
    }

    /** Starts a route at {@code start} (field inches, heading in radians). */
    public static WeaverRoute from(Pose start) {
        if (start == null) {
            throw new IllegalArgumentException("start must not be null");
        }
        return new WeaverRoute(start);
    }

    /** Adds zones, in field inches, that the route must bend around. */
    public WeaverRoute avoid(Zone... zones) {
        return avoid(Arrays.asList(zones));
    }

    /** Adds zones, in field inches, that the route must bend around. */
    public WeaverRoute avoid(Collection<? extends Zone> zones) {
        obstacles.addAll(zones);
        return this;
    }

    /** Visits at most {@code n} detections, keeping the best ones. Zero or less means no limit. */
    public WeaverRoute maxTargets(int n) {
        maxTargets = n;
        return this;
    }

    /** Visits detections in the order given instead of letting Weaver pick the cheapest order. */
    public WeaverRoute ordered() {
        ordered = true;
        return this;
    }

    /** Ends the route at {@code end} instead of at the last detection. */
    public WeaverRoute end(Pose end) {
        this.end = end;
        return this;
    }

    /**
     * Plans the route.
     *
     * @param detections detections in field coordinates
     * @return the planned route, or {@code null} if there are no detections
     * @throws RuntimeException whatever Weaver throws for degenerate geometry; catch it to fall back
     */
    public PathResult plan(List<Detection> detections) {
        if (detections.isEmpty()) {
            return null;
        }

        WeaverBuilder builder = Weaver.builder();
        if (ordered) {
            builder.ordered();
        }
        if (end != null) {
            builder.end(toPoint(end));
        }

        // Marrow treats a maxTargets of zero or less as "no limit", so it can be passed through as-is.
        return builder.start(toPoint(start))
                .targets(toPoints(detections))
                .maxTargets(maxTargets)
                .obstacles(obstacles)
                .generate();
    }

    /** Converts detections to Weaver targets, keeping their order. The heading follows the direction of travel. */
    static List<Point> toPoints(Collection<Detection> detections) {
        List<Point> points = new ArrayList<>(detections.size());
        for (Detection detection : detections) {
            points.add(new Point(detection.getX(), detection.getY()));
        }
        return points;
    }

    private static Point toPoint(Pose pose) {
        return new Point(pose.getX(), pose.getY(), pose.getHeading());
    }
}
