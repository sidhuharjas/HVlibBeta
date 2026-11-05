package org.hivevisionftc.vision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class HiveVisionTest {
    private static final Pose ROBOT = new Pose(0, 0, 0);

    private static Detection at(double x, double y, double confidence, BiobuzzClasses type) {
        return new Detection(new Pose(x, y), 0, 0, confidence, 1, type.getId());
    }

    private static HiveVision seeing(long stalenessMs, VisionConfig config, Detection... detections) {
        List<Detection> list = Arrays.asList(detections);
        DetectionSource source = new DetectionSource() {
            @Override
            public List<Detection> fetchLatestDetections(Pose robotPose) {
                return list;
            }

            @Override
            public long stalenessMs() {
                return stalenessMs;
            }
        };
        return new HiveVision(source, config);
    }

    @Test
    void resultsAreSortedNearestFirst() {
        HiveVision vision = seeing(
                0,
                new VisionConfig(),
                at(30, 0, 0.9, BiobuzzClasses.POLLEN),
                at(10, 0, 0.9, BiobuzzClasses.POLLEN),
                at(20, 0, 0.9, BiobuzzClasses.POLLEN));

        List<Detection> found = vision.fetchDetections(ROBOT);

        assertEquals(10.0, found.get(0).getX(), 1e-9);
        assertEquals(20.0, found.get(1).getX(), 1e-9);
        assertEquals(30.0, found.get(2).getX(), 1e-9);
    }

    @Test
    void lowConfidenceDetectionsAreDropped() {
        HiveVision vision = seeing(
                0,
                new VisionConfig().minConfidence(0.5),
                at(10, 0, 0.49, BiobuzzClasses.POLLEN),
                at(20, 0, 0.5, BiobuzzClasses.POLLEN));

        List<Detection> found = vision.fetchDetections(ROBOT);

        assertEquals(1, found.size());
        assertEquals(20.0, found.get(0).getX(), 1e-9);
    }

    @Test
    void staleDataIsDiscarded() {
        VisionConfig config = new VisionConfig().maxStalenessMs(100);

        assertTrue(seeing(101, config, at(10, 0, 0.9, BiobuzzClasses.POLLEN))
                .fetchDetections(ROBOT)
                .isEmpty());
        assertEquals(
                1,
                seeing(100, config, at(10, 0, 0.9, BiobuzzClasses.POLLEN))
                        .fetchDetections(ROBOT)
                        .size());
    }

    @Test
    void onlyAllowedClassesAreKept() {
        HiveVision vision = seeing(
                0,
                new VisionConfig(),
                at(10, 0, 0.9, BiobuzzClasses.POLLEN),
                at(20, 0, 0.9, BiobuzzClasses.NECTAR_RED),
                at(30, 0, 0.9, BiobuzzClasses.NECTAR_BLUE));
        vision.setAllowedClasses(BiobuzzClasses.NECTAR_RED, BiobuzzClasses.NECTAR_BLUE);

        List<Detection> found = vision.fetchDetections(ROBOT);

        assertEquals(2, found.size());
        assertEquals(BiobuzzClasses.NECTAR_RED, found.get(0).getType());
        assertEquals(BiobuzzClasses.NECTAR_BLUE, found.get(1).getType());
    }

    @Test
    void clearingTheAllowedClassesAllowsEverythingAgain() {
        HiveVision vision = seeing(0, new VisionConfig(), at(10, 0, 0.9, BiobuzzClasses.POLLEN));
        vision.setAllowedClasses(BiobuzzClasses.NECTAR_RED);
        vision.setAllowedClasses();

        assertEquals(1, vision.fetchDetections(ROBOT).size());
    }

    @Test
    void unknownClassIdsMapToUnknown() {
        assertEquals(BiobuzzClasses.UNKNOWN, BiobuzzClasses.fromId(99));
        assertEquals(BiobuzzClasses.NECTAR_BLUE, BiobuzzClasses.fromId(2));
    }
}
