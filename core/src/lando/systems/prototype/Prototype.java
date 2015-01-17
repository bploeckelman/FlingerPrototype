package lando.systems.prototype;

import com.badlogic.gdx.ApplicationAdapter;

public class Prototype extends ApplicationAdapter {

	Controller controller;

	@Override
	public void create() {
		Model model = new Model();
		View  view  = new View(model);
		controller = new Controller(model, view);
	}

	@Override
	public void render() {
		controller.render();
	}

	@Override
	public void dispose() {
		controller.dispose();
	}

}
