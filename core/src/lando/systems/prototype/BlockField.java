package lando.systems.prototype;

import com.badlogic.gdx.math.Vector2;
import lando.systems.prototype.Block.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Brian Ploeckelman created on 1/17/2015.
 */
public class BlockField {

    public static final  int   FIELD_WIDTH      = 6;
    public static final  int   FIELD_HEIGHT     = 12;
    public static final  float FIELD_START_X    = View.VIEW_WIDTH - FIELD_WIDTH * Block.SIZE;
    public static final  float FIELD_START_Y    = View.BLOCK_QUEUE_MARGIN_TOP;
    private static final int   NUM_MATCH_BLOCKS = 3;

    Block[][] blocks;

    public BlockField() {
        Vector2 position = new Vector2();
        blocks = new Block[FIELD_HEIGHT][FIELD_WIDTH];
        for (int y = 0; y < blocks.length; ++y) {
            for (int x = 0; x < blocks[0].length; ++x) {
                position.set(FIELD_START_X + x * Block.SIZE,
                             FIELD_START_Y + y * Block.SIZE);
                blocks[y][x] = new Block(BlockType.EMPTY, position.cpy());
                blocks[y][x].rowIndex = y;
                blocks[y][x].colIndex = x;
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
            lastEmptyBlock.type  = block.type;
            lastEmptyBlock.state = State.LANDED;
//            lastEmptyBlock.matched = false;
            checkForMatches(lastEmptyBlock);
            return true;
        }
        return false;
    }

    private List<Block> matchedBlocks = new ArrayList<Block>();
    public void checkForMatches(Block... blocksToCheck) {
        if (blocksToCheck.length == 0) return;

        for (Block block : blocksToCheck) {
            boolean didMatch = false;

            matchedBlocks.clear();
            matchedBlocks.add(block);
            block.matched = true;

            // Walk left
            for (int col = block.colIndex - 1; col >= 0; --col) {
                if (doBlocksMatch(block, blocks[block.rowIndex][col])) {
                    matchedBlocks.add(   blocks[block.rowIndex][col]);
                } else {
                    break;
                }
            }
            // Walk right
            for (int col = block.colIndex + 1; col < blocks[0].length; ++col) {
                if (doBlocksMatch(block, blocks[block.rowIndex][col])) {
                    matchedBlocks.add(   blocks[block.rowIndex][col]);
                } else {
                    break;
                }
            }

            // Do we have enough blocks in a row?
            if (matchedBlocks.size() < NUM_MATCH_BLOCKS) {
                for (Block matched : matchedBlocks) {
                    matched.matched = false;
                }
            } else {
                didMatch = true;
            }

            matchedBlocks.clear();
            matchedBlocks.add(block);
            block.matched = true;

            // Walk down
            for (int row = block.rowIndex - 1; row >= 0; --row) {
                if (doBlocksMatch(block, blocks[row][block.colIndex])) {
                    matchedBlocks.add(   blocks[row][block.colIndex]);
                } else {
                    break;
                }
            }
            // Walk up
            for (int row = block.rowIndex + 1; row < blocks.length; ++row) {
                if (doBlocksMatch(block, blocks[row][block.colIndex])) {
                    matchedBlocks.add(   blocks[row][block.colIndex]);
                } else {
                    break;
                }
            }

            // Do we have enough blocks in a column?
            if (matchedBlocks.size() < NUM_MATCH_BLOCKS) {
                for (Block matched : matchedBlocks) {
                    matched.matched = false;
                }
            } else {
                didMatch = true;
            }
            matchedBlocks.clear();

            if (didMatch) {
                block.matched = true;
            }
        }

        // Process matching blocks from list: animate and reset their states
        for (Block[] blockRow : blocks) {
            for (Block block : blockRow) {
                if (block.matched) {
                    block.matched = false;
                    block.type = BlockType.EMPTY;
                }
            }
        }

        // TODO(brian): Update the player's score, track num row/col matches and their size, apply multiplier for longer runs

        // Update the board by shifting blocks to the right to fill empty spaces
        // Add any shifted blocks to a list of blocks to be checked again
        List<Block> blocksToRecheck = new ArrayList<Block>();
        for (Block[] blockRow : blocks) {
            for (int i = blockRow.length - 1; i >= 0; --i) {
                if (BlockType.EMPTY.equals(blockRow[i].type)) {
                    for (int j = i - 1; j >= 0; --j) {
                        if (!BlockType.EMPTY.equals(blockRow[j].type)) {
                            blockRow[i].type = blockRow[j].type;
                            blockRow[j].type = BlockType.EMPTY;
                            blocksToRecheck.add(blockRow[i]);
                            break;
                        }
                    }
                }
            }
        }

        // Recheck any blocks that moved during the board update
        if (!blocksToRecheck.isEmpty()) {
            checkForMatches(blocksToRecheck.toArray(new Block[blocksToRecheck.size()]));
        }
    }

    // -------------------------------------------------------------------------
    // Private implementation
    // -------------------------------------------------------------------------

    private Block getLastEmptyBlockInRow(int row) {
        Block lastEmptyBlock = null;
        if (row >= 0 && row < blocks.length) {
            for (Block block : blocks[row]) {
                if (BlockType.EMPTY.equals(block.type)) {
                    lastEmptyBlock = block;
                }
            }
        }
        return lastEmptyBlock;
    }

    private boolean doBlocksMatch(Block block1, Block block2) {
        if (block1.type.equals(block2.type)) {
            block1.matched = true;
            block2.matched = true;
            return true;
        }
        return false;
    }


}
