package org.hivevisionftc.vision;

/**
 * Game element classes the neural detector can report. The ids must match the class order the model was trained with.
 *
 * <p>For a new season, add a sibling enum with the new game elements.
 */
public enum BiobuzzClasses {
    POLLEN(0),
    NECTAR_RED(1),
    NECTAR_BLUE(2),
    UNKNOWN(-1);

    private final int id;

    BiobuzzClasses(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /** Returns the class with the given detector id, or {@link #UNKNOWN} if there is none. */
    public static BiobuzzClasses fromId(int id) {
        for (BiobuzzClasses type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
