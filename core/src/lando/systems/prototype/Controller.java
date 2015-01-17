package lando.systems.prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
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

    private void processInput() {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.justTouched()) {
            if (Gdx.input.getX() < View.DROP_REGION_WIDTH) {
                model.flingBlock();
            }
        }

        // handle other polled input here...
    }

}
