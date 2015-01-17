package lando.systems.prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import java.util.Random;

/**
 * Brian Ploeckelman created on 1/16/2015.
 */
public enum BlockType {
    RED        (0),
    ORANGE     (1),
    YELLOW     (2),
    GREEN      (3),
    BLUE       (4),
    INDIGO     (5),
    VIOLET     (6),
    NUM_BLOCKS (7),
    EMPTY      (8);

    private int value;

    private BlockType(int value) {
        this.value = value;
    }

    private int getValue() {
        return value;
    }

    // -------------------------------------------------------------------------
    // Block Type -> Color conversion
    // -------------------------------------------------------------------------

    private static Color COLOR_INDIGO = new Color(0.294f, 0, 0.510f, 1);
    private static Color COLOR_VIOLET = new Color(0.5f, 0, 1, 1);

    public Color getColor() {
        switch (this) {
            case RED:
                return Color.RED;
            case ORANGE:
                return Color.ORANGE;
            case YELLOW:
                return Color.YELLOW;
            case GREEN:
                return Color.GREEN;
            case BLUE:
                return Color.BLUE;
            case INDIGO:
                return COLOR_INDIGO;
            case VIOLET:
                return COLOR_VIOLET;
            default:
                return Color.WHITE;
        }
    }

    // -------------------------------------------------------------------------
    // Static Utility Methods
    // -------------------------------------------------------------------------

    private static Random random = new Random();

    public static BlockType getRandom() {
        return fromInt(random.nextInt(NUM_BLOCKS.getValue()));
    }

    public static BlockType fromInt(int i) {
        for (BlockType blockTypeType : BlockType.values()) {
            if (blockTypeType.getValue() == i) {
                return blockTypeType;
            }
        }

        Gdx.app.error("WARN",
                      "Unable to get Block from int '" + i + "'");
        return RED;
    }

}
