package lando.systems.prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import java.util.List;

/**
 * Brian Ploeckelman created on 1/16/2015.
 */
public class View implements Disposable {

    public static final int   VIEW_WIDTH             = 800;
    public static final int   VIEW_HEIGHT            = 480;
    public static final float DROP_REGION_WIDTH      = VIEW_WIDTH / 3;
    public static final float BLOCK_PADDING          = 8f;
    public static final float BLOCK_QUEUE_MARGIN_TOP = 16;
    public static final float BLOCK_QUEUE_POSITION_X = DROP_REGION_WIDTH / 2 -
                                                       Block.SIZE / 2;
    public static final float BLOCK_QUEUE_POSITION_Y = VIEW_HEIGHT -
                                                       BLOCK_QUEUE_MARGIN_TOP -
                                                       Block.SIZE;

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
                                                        Block.SIZE,
                                                        Block.SIZE);
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
        drawBlocksInPlay(model.getBlocksInPlay());
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

    private void drawBlockQueue(List<Block> blockQueue) {
        for (Block block : blockQueue) {
            drawBlock(batch, block);
        }
        batch.setColor(Color.WHITE);
    }

    private void drawBlockField(Block[][] blockField) {
        for (Block[] blockRow : blockField) {
            for (Block block : blockRow) {
                drawBlock(batch, block);
            }
        }
        batch.setColor(Color.WHITE);
    }

    private void drawBlocksInPlay(List<Block> blocksInPlay) {
        for (Block block : blocksInPlay) {
            // Ignore blocks in a state that doesn't make sense
            // NOTE: this shouldn't ever happen
            if (!Block.State.DROPPING.equals(block.state)
             && !Block.State.FLINGING.equals(block.state)) {
                Gdx.app.error("WARN",
                              "Block in in-play list with state: " +
                              block.state.name());
                continue;
            }

            drawBlock(batch, block);
        }
        batch.setColor(Color.WHITE);
    }

    private void drawBlock(SpriteBatch batch, BlockType blockType, float x, float y) {
        batch.setColor(blockType.getColor());
        batch.draw(blockTexture, x, y, Block.SIZE, Block.SIZE);
    }

    private void drawBlock(SpriteBatch batch, Block block) {
        drawBlock(batch, block.getType(), block.position.x, block.position.y);
    }

    private void drawDropRegion() {
        batch.draw(dropRegionTexture,
                   0,
                   0,
                   DROP_REGION_WIDTH,
                   BLOCK_QUEUE_POSITION_Y - 2 * BLOCK_QUEUE_MARGIN_TOP);
    }

}
