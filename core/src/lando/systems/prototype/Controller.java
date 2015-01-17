package lando.systems.prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

/**
 * Brian Ploeckelman created on 1/16/2015.
 */
public class Controller extends InputAdapter implements Disposable {

    private final Model model;
    private final View  view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view  = view;
        Gdx.input.setInputProcessor(this);
    }

    // -------------------------------------------------------------------------
    // Public interface
    // -------------------------------------------------------------------------

    public void render() {
        processInput();

        model.update(Gdx.graphics.getDeltaTime());

        view.render();
    }

    public void dispose() {
        model.dispose();
        view.dispose();
    }

    // -------------------------------------------------------------------------
    // Private implementation
    // -------------------------------------------------------------------------

    private Vector3 screenTouch = new Vector3();
    private Vector3 worldTouch  = new Vector3();

    private void processInput() {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.justTouched()) {
            screenTouch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            worldTouch = view.camera.unproject(screenTouch);

            if (screenTouch.x < View.DROP_REGION_WIDTH) {
                model.handleFling(worldTouch.x, worldTouch.y);
            }
        }

        // handle other polled input here...
    }

}
