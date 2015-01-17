package lando.systems.prototype;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import lando.systems.prototype.accessors.ColorAccessor;
import lando.systems.prototype.accessors.Vector2Accessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Brian Ploeckelman created on 1/16/2015.
 */
public class Model implements Disposable {

    private static final int   BLOCK_QUEUE_SIZE   = 6;
    private static final int   BLOCK_FIELD_WIDTH  = 6;
    private static final int   BLOCK_FIELD_HEIGHT = 12;
    private static final float DROP_GRAVITY       = 128;
    private static final float DROP_DELAY         = 3;
    private static final float SWING_FREQUENCY    = 4;
    private static final float SWING_AMPLITUDE    = View.DROP_REGION_WIDTH / 2 -
                                                    View.BLOCK_SIZE * 2;
    private static final float SWING_CENTER_X     = View.BLOCK_QUEUE_POSITION_X;
    private static final float FLING_SPEED        = 512;

    TweenManager    tweens;
    List<BlockType> blockQueue;
    BlockType[][]   blockField;

    // TODO(brian): create block class with position info built in
    Vector2   flingVelocity;
    Vector2   dropPosition;
    BlockType dropBlock;
    boolean   dropping;
    float     dropAccum;
    float     swingAccum;

    public Model() {
        tweens = new TweenManager();
        Tween.registerAccessor(Color.class, new ColorAccessor());
        Tween.registerAccessor(Vector2.class, new Vector2Accessor());

        blockQueue = new ArrayList<BlockType>(BLOCK_QUEUE_SIZE);
        for (int i = 0; i < BLOCK_QUEUE_SIZE; ++i) {
            blockQueue.add(i, BlockType.getRandom());
        }

        blockField = new BlockType[BLOCK_FIELD_HEIGHT][BLOCK_FIELD_WIDTH];
        for (int y = 0; y < blockField.length; ++y) {
            for (int x = 0; x < blockField[0].length; ++x) {
                blockField[y][x] = BlockType.EMPTY;
            }
        }

        flingVelocity = new Vector2();
        dropPosition  = new Vector2();
        dropBlock     = null;
        dropping      = false;
        dropAccum     = DROP_DELAY;
        swingAccum    = 0;
    }

    public final TweenManager tween() {
        return tweens;
    }

    public final List<BlockType> getBlockQueue() {
        return blockQueue;
    }

    public final BlockType[][] getBlockField() {
        return blockField;
    }

    public final boolean isDropping() {
        return dropping;
    }

    public final Vector2 getDropPosition() {
        return dropPosition;
    }

    public final BlockType getDropBlock() {
        return dropBlock;
    }

    // -------------------------------------------------------------------------
    // Public interface
    // -------------------------------------------------------------------------

    public void update(float deltaTime) {
        tweens.update(deltaTime);

        dropAccum += deltaTime;
        if (dropAccum >= DROP_DELAY && !dropping) {
            dropAccum -= DROP_DELAY;
            dropBlock();
        }

        if (dropping) {
            if (dropPosition.y < -View.BLOCK_SIZE
             || dropPosition.x >  View.VIEW_WIDTH) {
                dropping = false;
                swingAccum = 0;
                flingVelocity.set(0, 0);
            }

            float newX, newY;
            if (flingVelocity.x != 0) {
                newX = dropPosition.x + flingVelocity.x * deltaTime;
                newY = dropPosition.y; // TODO(brian): clamp to closest field row y pos
            } else {
                swingAccum += SWING_FREQUENCY * deltaTime;
                if (swingAccum > MathUtils.PI2) {
                    swingAccum = 0;
                }
                newX = SWING_AMPLITUDE * MathUtils.sin(swingAccum) + SWING_CENTER_X;
                newY = dropPosition.y - DROP_GRAVITY * deltaTime;
            }
            dropPosition.x = newX;
            dropPosition.y = newY;
        }
    }

    public void flingBlock() {
        if (dropping && flingVelocity.x == 0) {
            flingVelocity.x = FLING_SPEED;
        }
    }

    @Override
    public void dispose() {}

    // -------------------------------------------------------------------------
    // Private implementation
    // -------------------------------------------------------------------------

    private void dropBlock() {
        dropBlock = blockQueue.get(0);
        for (int i = 0; i < blockQueue.size() - 1; ++i) {
            blockQueue.set(i, blockQueue.get(i + 1));
        }
        blockQueue.set(blockQueue.size() - 1, BlockType.getRandom());

        dropPosition.set(View.BLOCK_QUEUE_POSITION_X,
                         View.BLOCK_QUEUE_POSITION_Y - View.BLOCK_SIZE);
        dropping = true;
    }

}
