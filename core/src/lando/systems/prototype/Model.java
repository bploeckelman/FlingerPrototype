package lando.systems.prototype;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import lando.systems.prototype.accessors.ColorAccessor;
import lando.systems.prototype.accessors.Vector2Accessor;

import java.util.LinkedList;

/**
 * Brian Ploeckelman created on 1/16/2015.
 */
public class Model implements Disposable {

    private static final int   QUEUE_SIZE      = 6;
    private static final int   FIELD_WIDTH     = 6;
    private static final int   FIELD_HEIGHT    = 12;
    private static final float DROP_GRAVITY    = 64;
    private static final float DROP_DELAY      = 1.25f;
    private static final float FLING_SPEED     = 512;
    private static final float SWING_FREQUENCY = 3;
    private static final float SWING_AMPLITUDE = View.DROP_REGION_WIDTH / 2 -
                                                 Block.SIZE * 2;
    private static final float SWING_CENTER_X  = View.BLOCK_QUEUE_POSITION_X;
    private static final float FIELD_START_X   = View.VIEW_WIDTH -
                                                 FIELD_WIDTH * Block.SIZE;
    private static final float FIELD_START_Y   = View.BLOCK_QUEUE_MARGIN_TOP;

    TweenManager      tweens;
    LinkedList<Block> blockQueue;
    LinkedList<Block> blocksInPlay;
    Block[][]         blockField;

    float dropAccum;
    float swingAccum;

    public Model() {
        tweens = new TweenManager();
        Tween.registerAccessor(Color.class, new ColorAccessor());
        Tween.registerAccessor(Vector2.class, new Vector2Accessor());

        Vector2 position = new Vector2();

        blockQueue = new LinkedList<Block>();
        float bx = View.BLOCK_QUEUE_POSITION_X;
        float by = View.BLOCK_QUEUE_POSITION_Y;
        for (int i = 0; i < QUEUE_SIZE; ++i) {
            position.set(bx, by);
            blockQueue.add(new Block(BlockType.getRandom(), position.cpy()));
            bx += Block.SIZE + View.BLOCK_PADDING;
        }

        blocksInPlay = new LinkedList<Block>();

        blockField = new Block[FIELD_HEIGHT][FIELD_WIDTH];
        for (int y = 0; y < blockField.length; ++y) {
            for (int x = 0; x < blockField[0].length; ++x) {
                position.set(FIELD_START_X + x * Block.SIZE,
                             FIELD_START_Y + y * Block.SIZE);
                blockField[y][x] = new Block(BlockType.EMPTY, position.cpy());
            }
        }

        dropAccum  = DROP_DELAY;
        swingAccum = 0;
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

    // TODO(brian): add fling handling with a GestureDetector implementation in Controller
    public void handleFling(float worldTouchX, float worldTouchY) {
        flingBlock(worldTouchX, worldTouchY);
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

        // TODO(brian): each block should handle its own swing
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

    private Vector2   touch  = new Vector2();
    private Rectangle bounds = new Rectangle(0, 0, Block.SIZE, Block.SIZE);
    private void flingBlock(float worldTouchX, float worldTouchY) {
        touch.set(worldTouchX, worldTouchY);

        Block fling = null;
        for (Block block : blocksInPlay) {
            if (Block.State.DROPPING.equals(block.state)) {
                bounds.setPosition(block.position);
                if (bounds.contains(touch)) {
                    fling = block;
                    break;
                }
            }
        }

        if (fling != null) {
            fling.state = Block.State.FLINGING;
            fling.velocity.x = FLING_SPEED;

            // Clamp to closest field row y pos
            // TODO(brian): there should be a more elegant way to accomplish this
            final float FIELD_END_Y = FIELD_START_Y + FIELD_HEIGHT * Block.SIZE;
            for (float y = FIELD_START_Y; y < FIELD_END_Y; y += Block.SIZE) {
                if (touch.y >= y && touch.y <= y + Block.SIZE) {
                    fling.position.y = y;
                    break;
                }
            }
        }
    }

}
