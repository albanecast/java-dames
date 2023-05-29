package atelier3.launcher.clientServerLauncher;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import atelier3.controller.Mediator;
import atelier3.controller.ClientServerController.ServerController;
import atelier3.model.BoardGame;
import atelier3.model.Coord;
import atelier3.model.Model;
import javafx.application.Application;
import javafx.stage.Stage;
import atelier3.tools.communication.CommunicationChannel;
import atelier3.tools.communication.CommunicationConfig;
import atelier3.tools.communication.ServerChannel;


public class LauncherServer extends Application {


	private BoardGame<Coord> model;
	private BoardGame<Integer> controllerModel;

	private CommunicationConfig config = new CommunicationConfig("127.0.0.1", 8889,200);;
	private BlockingQueue<Object> msgQueue = new ArrayBlockingQueue<Object>(20);
	private CommunicationChannel srvChannel = new ServerChannel(config, msgQueue);

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LauncherServer.launch();
	}

	@Override
	public void init () throws Exception {
		super.init();
		this.model = new Model();
		this.controllerModel = (BoardGame<Integer>) new ServerController();


		((Mediator) this.controllerModel).setModel(this.model);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
	}
}

