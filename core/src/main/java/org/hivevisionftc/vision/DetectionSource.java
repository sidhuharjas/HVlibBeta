package org.hivevisionftc.vision;

import java.util.List;

/** A camera backend that produces detections. */
public interface DetectionSource {
    /**
     * Returns the latest detections, located in field coordinates from the given robot pose. Detections that cannot
     * be located on the field are left out.
     */
    List<Detection> fetchLatestDetections(Pose robotPose);

    /** Age, in milliseconds, of the data behind the last {@link #fetchLatestDetections} call. */
    long stalenessMs();
}
