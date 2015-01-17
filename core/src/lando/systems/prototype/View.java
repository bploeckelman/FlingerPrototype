package lando.systems.prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import java.util.List;

/**
 * Brian Ploeckelman created on 1/16/2015.
 */
public class View implements Disposable {

    public static final int   VIEW_WIDTH              = 800;
    public static final int   VIEW_HEIGHT             = 480;
    public static final int   BLOCK_SIZE              = 32;
    public static final float DROP_REGION_WIDTH       = VIEW_WIDTH / 3;
    public static final float BLOCK_PADDING           = 8f;
    public static final float BLOCK_QUEUE_MARGIN_TOP  = 16;
    public static final float BLOCK_QUEUE_POSITION_X  = DROP_REGION_WIDTH / 2 -
                                                        BLOCK_SIZE / 2;
    public static final float BLOCK_QUEUE_POSITION_Y  = VIEW_HEIGHT -
                                                        BLOCK_QUEUE_MARGIN_TOP -
                                                        BLOCK_SIZE;

    Model              model;
    SpriteBatch        batch;
    Texture            spritesheet;
    TextureRegion      blockTexture;
    TextureRegion      emptyTexture;
    TextureRegion      dropRegionTexture;
    OrthographicCamera camera;

    public View(Model model) {
        this.model = model;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT);
        spritesheet = new Texture("spritesheet.png");
        TextureRegion[][] regions = TextureRegion.split(spritesheet,
                                                        BLOCK_SIZE,
                                                        BLOCK_SIZE);
        blockTexture      = regions[0][0];
        emptyTexture      = regions[0][1];
        dropRegionTexture = regions[0][2];
    }

    // -------------------------------------------------------------------------
    // Public interface
    // -------------------------------------------------------------------------

    public void render() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawDropRegion();
        drawBlockQueue(model.getBlockQueue());
        drawBlockField(model.getBlockField());
        drawBlockDrop(model.isDropping());
        batch.end();
    }

    @Override
    public void dispose() {
        spritesheet.dispose();
        batch.dispose();
    }

    // -------------------------------------------------------------------------
    // Private implementation
    // -------------------------------------------------------------------------

    private void drawBlockQueue(List<BlockType> blockQueue) {
        float x = BLOCK_QUEUE_POSITION_X;
        float y = BLOCK_QUEUE_POSITION_Y;

        for (BlockType blockType : blockQueue) {
            drawBlock(batch, blockType, x, y);
            x += BLOCK_SIZE + BLOCK_PADDING;
        }
        batch.setColor(Color.WHITE);
    }

    private void drawBlockField(BlockType[][] blockField) {
        final float FIELD_START_X = VIEW_WIDTH - blockField[0].length * BLOCK_SIZE;
        final float FIELD_START_Y = BLOCK_QUEUE_MARGIN_TOP;

        for (int y = 0; y < blockField.length; ++y) {
            for (int x = 0; x < blockField[0].length; ++x) {
                drawBlock(batch,
                          blockField[y][x],
                          FIELD_START_X + x * BLOCK_SIZE,
                          FIELD_START_Y + y * BLOCK_SIZE);
            }
        }
        batch.setColor(Color.WHITE);
    }

    private void drawBlockDrop(boolean dropping) {
        if (!dropping) return;

        final Vector2 position = model.getDropPosition();
        drawBlock(batch, model.getDropBlock(), position.x, position.y);
        batch.setColor(Color.WHITE);
    }

    private void drawBlock(SpriteBatch batch, BlockType blockType, float x, float y) {
        batch.setColor(blockType.getColor());
        batch.draw(blockTexture, x, y, BLOCK_SIZE, BLOCK_SIZE);
    }

    private void drawDropRegion() {
        batch.draw(dropRegionTexture,
                   0,
                   0,
                   DROP_REGION_WIDTH,
                   BLOCK_QUEUE_POSITION_Y - 2 * BLOCK_QUEUE_MARGIN_TOP);
    }

}
