package lando.systems.prototype;

import com.badlogic.gdx.math.Vector2;
import org.omg.CORBA.UNKNOWN;

/**
 * Brian Ploeckelman created on 1/16/2015.
 */
public class Block {

    public static final int SIZE = 32;

    private BlockType type;
    public  State     state;
    public  Vector2   position;
    public  Vector2   velocity;
    public  int       rowIndex;

    public enum State {
        QUEUED,
        DROPPING,
        FLINGING,
        LANDED,
        UNKNOWN
    }

    public Block(BlockType type) {
        this(type, new Vector2());
    }

    public Block(BlockType type, Vector2 position) {
        this(type, position, State.UNKNOWN);
    }

    public Block(BlockType type, Vector2 position, State state) {
        this.type     = type;
        this.state    = state;
        this.position = position;
        this.velocity = new Vector2();
        this.rowIndex = -1;
    }

    public final BlockType getType() {
        return type;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

    // -------------------------------------------------------------------------
    // Public interface
    // -------------------------------------------------------------------------



    // -------------------------------------------------------------------------
    // Private implementation
    // -------------------------------------------------------------------------



}
