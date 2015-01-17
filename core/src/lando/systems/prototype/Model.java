package lando.systems.prototype;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import lando.systems.prototype.accessors.ColorAccessor;
import lando.systems.prototype.accessors.Vector2Accessor;

import java.util.LinkedList;

/**
 * Brian Ploeckelman created on 1/16/2015.
 */
public class Model implements Disposable {

    private static final int   BLOCK_QUEUE_SIZE   = 6;
    private static final int   BLOCK_FIELD_WIDTH  = 6;
    private static final int   BLOCK_FIELD_HEIGHT = 12;
    private static final float DROP_GRAVITY       = 128;
    private static final float DROP_DELAY         = 1;
    private static final float SWING_FREQUENCY    = 4;
    private static final float SWING_AMPLITUDE    = View.DROP_REGION_WIDTH / 2 -
                                                    Block.SIZE * 2;
    private static final float SWING_CENTER_X     = View.BLOCK_QUEUE_POSITION_X;
    private static final float FLING_SPEED        = 512;

    TweenManager      tweens;
    LinkedList<Block> blockQueue;
    LinkedList<Block> blocksInPlay;
    Block[][]         blockField;

    float     dropAccum;
    float     swingAccum;

    public Model() {
        tweens = new TweenManager();
        Tween.registerAccessor(Color.class, new ColorAccessor());
        Tween.registerAccessor(Vector2.class, new Vector2Accessor());

        Vector2 position = new Vector2();

        blockQueue = new LinkedList<Block>();
        float bx = View.BLOCK_QUEUE_POSITION_X;
        float by = View.BLOCK_QUEUE_POSITION_Y;
        for (int i = 0; i < BLOCK_QUEUE_SIZE; ++i) {
            position.set(bx, by);
            blockQueue.add(new Block(BlockType.getRandom(), position.cpy()));
            bx += Block.SIZE + View.BLOCK_PADDING;
        }

        blocksInPlay = new LinkedList<Block>();

        final float FIELD_START_X = View.VIEW_WIDTH - BLOCK_FIELD_WIDTH * Block.SIZE;
        final float FIELD_START_Y = View.BLOCK_QUEUE_MARGIN_TOP;
        blockField = new Block[BLOCK_FIELD_HEIGHT][BLOCK_FIELD_WIDTH];
        for (int y = 0; y < blockField.length; ++y) {
            for (int x = 0; x < blockField[0].length; ++x) {
                position.set(FIELD_START_X + x * Block.SIZE,
                             FIELD_START_Y + y * Block.SIZE);
                blockField[y][x] = new Block(BlockType.EMPTY, position.cpy());
            }
        }

        dropAccum     = DROP_DELAY;
        swingAccum    = 0;
    }

    public final TweenManager tween() {
        return tweens;
    }

    public final LinkedList<Block> getBlockQueue() {
        return blockQueue;
    }

    public final LinkedList<Block> getBlocksInPlay() {
        return blocksInPlay;
    }

    public final Block[][] getBlockField() {
        return blockField;
    }

    // -------------------------------------------------------------------------
    // Public interface
    // -------------------------------------------------------------------------

    public void update(float deltaTime) {
        tweens.update(deltaTime);
        updateBlocks(deltaTime);
    }

    // TODO(brian): add pointer x,y and fling the touched block, if such a block exists and is DROPPING;
    public void flingBlock() {
        Block flingBlock = null;
        for (Block block : blocksInPlay) {
            if (Block.State.DROPPING.equals(block.state)) {
                flingBlock = block;
                break;
            }
        }

        if (flingBlock != null) {
            flingBlock.state = Block.State.FLINGING;
            flingBlock.velocity.x = FLING_SPEED;
        }
    }

    @Override
    public void dispose() {}

    // -------------------------------------------------------------------------
    // Private implementation
    // -------------------------------------------------------------------------

    private void updateBlocks(float deltaTime) {
        dropAccum += deltaTime;
        if (dropAccum >= DROP_DELAY) {
            dropAccum -= DROP_DELAY;
            dropBlock();
        }

        swingAccum += SWING_FREQUENCY * deltaTime;
        if (swingAccum >= MathUtils.PI2) {
            swingAccum = 0;
        }

        // TODO(brian): change blocksInPlay to ArrayList since I'm accessing by index here?
        Block block;
        for (int i = blocksInPlay.size() - 1; i >= 0; --i) {
            block = blocksInPlay.get(i);

            // Handle dropping blocks
            if (Block.State.DROPPING.equals(block.state)) {
                // If the block goes off screen, remove it
                if (block.position.y < -Block.SIZE) {
                    block.state = Block.State.UNKNOWN;
                    block.velocity.set(0, 0);
                    blocksInPlay.remove(i);
                } else {
                    block.position.x = SWING_AMPLITUDE *
                                       MathUtils.sin(swingAccum) +
                                       SWING_CENTER_X;
                    block.position.y -= DROP_GRAVITY * deltaTime;
                }
            }

            // Handle flinging blocks
            else if (Block.State.FLINGING.equals(block.state)) {
                // If the block goes off screen, remove it
                if (block.position.x > View.VIEW_WIDTH) {
                    block.state = Block.State.UNKNOWN;
                    block.velocity.set(0, 0);
                    blocksInPlay.remove(i);
                } else {
                    if (block.velocity.x != 0) {
                        block.position.x += block.velocity.x * deltaTime;
                        // TODO(brian): clamp to closest field row y pos
//                        block.position.y  = block.position.y;
                    }
                }
            }
        }
    }

    private void dropBlock() {
        // Remove first block from queue and put it in play
        Block dropBlock = blockQueue.removeFirst();
        dropBlock.state = Block.State.DROPPING;
        dropBlock.position.y -= Block.SIZE;
        blocksInPlay.add(dropBlock);

        // Add new block to the end of the queue
        Block newBlock = new Block(BlockType.getRandom(),
                                   blockQueue.getLast().position.cpy(),
                                   Block.State.QUEUED);
        newBlock.position.x += Block.SIZE + View.BLOCK_PADDING;
        blockQueue.add(newBlock);

        // Update queued block positions
        for (Block block : blockQueue) {
            block.position.x -= Block.SIZE + View.BLOCK_PADDING;
        }
    }

}
