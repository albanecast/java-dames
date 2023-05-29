package atelier3.launcher.clientServerLauncher;

import atelier3.controller.Mediator;
import atelier3.controller.localController.Controller;
import atelier3.gui.GuiConfig;
import atelier3.gui.View;
import atelier3.model.BoardGame;
import atelier3.model.Coord;
import atelier3.model.Model;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LauncherWhiteClient extends Application {

	private View view;

	public static void main(String[] args) {
		LauncherWhiteClient.launch();
	}

	@Override
	public void init() throws Exception {
		super.init();

		// Objet qui gère les aspects métier du jeu de dame :
		BoardGame<Coord> model = new Model();

		// Objet qui contrôle les actions de la vue et les transmet au model
		// il renvoie les réponses du model à la vue
		EventHandler<MouseEvent> controller = new Controller();

		// Fenêtre dans laquelle se dessine le damier est écoutée par controller
		this.view = new View(controller);

		// Controller doit pouvoir invoquer les méthodes du model
		// il enverra ensuite des instructions à view qui relaiera à son objet Board
		((Mediator) controller).setView(view);
		((Mediator) controller).setModel(model);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setScene(new Scene(this.view, GuiConfig.HEIGHT, GuiConfig.HEIGHT));
		primaryStage.setTitle("Joueur Blanc");
		primaryStage.show();
	}
}
