package lando.systems.prototype;

import com.badlogic.gdx.math.Vector2;

/**
 * Brian Ploeckelman created on 1/17/2015.
 */
public class BlockField {

    public static final int   FIELD_WIDTH   = 6;
    public static final int   FIELD_HEIGHT  = 12;
    public static final float FIELD_START_X = View.VIEW_WIDTH - FIELD_WIDTH * Block.SIZE;
    public static final float FIELD_START_Y = View.BLOCK_QUEUE_MARGIN_TOP;

    Block[][] blocks;

    public BlockField() {
        Vector2 position = new Vector2();
        blocks = new Block[FIELD_HEIGHT][FIELD_WIDTH];
        for (int y = 0; y < blocks.length; ++y) {
            for (int x = 0; x < blocks[0].length; ++x) {
                position.set(FIELD_START_X + x * Block.SIZE,
                             FIELD_START_Y + y * Block.SIZE);
                blocks[y][x] = new Block(BlockType.EMPTY, position.cpy());
            }
        }
    }

    public final Block[][] getBlocks() { return blocks; }

    // -------------------------------------------------------------------------
    // Public interface
    // -------------------------------------------------------------------------



    // -------------------------------------------------------------------------
    // Private implementation
    // -------------------------------------------------------------------------



}
