package atelier3.launcher.clientServerLauncher;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import atelier3.controller.Mediator;
import atelier3.controller.ClientServerController.ClientController;
import atelier3.gui.GuiConfig;
import atelier3.gui.View;
import atelier3.model.BoardGame;
import atelier3.model.Coord;
import atelier3.nutsAndBolts.PieceSquareColor;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import atelier3.tools.communication.ClientChannel;
import atelier3.tools.communication.CommunicationChannel;
import atelier3.tools.communication.CommunicationConfig;



public class LauncherBlackClient extends Application {

	private EventHandler<MouseEvent> controllerClient;
	private View view;
	private BoardGame<Coord> model;


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LauncherBlackClient.launch();
	}

	public LauncherBlackClient(BoardGame<Coord> model) {
		this.model = model;
	}
	@Override
	public void init () throws Exception {
		super.init();

		CommunicationConfig config = new CommunicationConfig("127.0.0.1", 8889, 200);
		BlockingQueue<Object> msgQueue = new ArrayBlockingQueue<>(20);
		CommunicationChannel cltChannel = (CommunicationChannel) new ClientChannel(config, msgQueue);

		this.controllerClient = (EventHandler<MouseEvent>) new ClientController(cltChannel, PieceSquareColor.BLACK);


		this.view = new View(this.controllerClient);

		((Mediator) this.controllerClient).setView(this.view);
		((Mediator) this.controllerClient).setModel(this.model);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setScene(new Scene(this.view,  GuiConfig.HEIGHT, GuiConfig.HEIGHT));
		primaryStage.setTitle("Jeu de dames - Client noir");
		primaryStage.show();
	}
}
