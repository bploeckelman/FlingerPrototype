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

    public void clampToRow(Block flung, Vector2 touch) {
        Block block;
        for (int i = 0; i < blocks.length; ++i) {
            block = blocks[i][0];
            if (touch.y >= block.position.y
             && touch.y <= block.position.y + Block.SIZE) {
                flung.position.y = block.position.y;
                flung.rowIndex = i;
                break;
            }
        }
    }

    public boolean isRowFull(int row) {
        return (getLastEmptyBlockInRow(row) == null);
    }

    public boolean checkForLanding(Block block) {
        if (block.rowIndex < 0 || block.rowIndex >= blocks.length) {
            return false;
        }

        final Block lastEmptyBlock = getLastEmptyBlockInRow(block.rowIndex);
        if (lastEmptyBlock == null) {
            return false;
        }

        if (block.position.x >= lastEmptyBlock.position.x) {
            lastEmptyBlock.setType(block.getType());
            lastEmptyBlock.state = Block.State.LANDED;
            return true;
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Private implementation
    // -------------------------------------------------------------------------

    private Block getLastEmptyBlockInRow(int row) {
        Block lastEmptyBlock = null;
        for (Block block : blocks[row]) {
            if (BlockType.EMPTY.equals(block.getType())) {
                lastEmptyBlock = block;
            }
        }
        return lastEmptyBlock;
    }

}
