package lando.systems.prototype;

import com.badlogic.gdx.math.Vector2;

/**
 * Brian Ploeckelman created on 1/16/2015.
 */
public class Block {

    public static final int SIZE = 32;

    // TODO(brian): add isEmpty() convenience method that checks BlockType.EMPTY.equals(type)

    public BlockType type;
    public State     state;
    public Vector2   position;
    public Vector2   velocity;
    public int       rowIndex;
    public int       colIndex;
    public boolean   matched;

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
        this.colIndex = -1;
        this.matched  = false;
    }

    // -------------------------------------------------------------------------
    // Public interface
    // -------------------------------------------------------------------------



    // -------------------------------------------------------------------------
    // Private implementation
    // -------------------------------------------------------------------------



}
