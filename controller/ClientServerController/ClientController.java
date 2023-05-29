package atelier3.controller.ClientServerController;

import atelier3.controller.Mediator;
import atelier3.controller.localController.Controller;
import atelier3.gui.GuiConfig;
import atelier3.gui.View;
import atelier3.model.BoardGame;
import atelier3.model.Coord;
import atelier3.model.Model;
import atelier3.nutsAndBolts.PieceSquareColor;
import atelier3.tools.communication.CommunicationChannel;
import atelier3.launcher.clientServerLauncher.LauncherWhiteClient;
import atelier3.launcher.clientServerLauncher.LauncherBlackClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;

public class ClientController extends Application {

    public static PieceSquareColor playerColor;
    private static Socket client;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;
    private Runnable future;

    private BoardGame<Coord> model;
    private Controller controller;
    private View view;

    private boolean isPlayer1Connected;
    private boolean isPlayer2Connected;


    public static void main(String[] args) {
        launch();
    }

    private CommunicationChannel communicationChannel;
    private PieceSquareColor pieceSquareColor;

    public ClientController(CommunicationChannel communicationChannel, PieceSquareColor pieceSquareColor) {
        // Initialisez le contrôleur avec les arguments fournis
        this.communicationChannel = communicationChannel;
        this.pieceSquareColor = pieceSquareColor;
    }

    public ClientController() {
    }

    public Socket getClient() {
        return client;
    }

    public void init() throws Exception {
        super.init();
        connectToServer();
        setupModelControllerView();
        setupServerListener();
    }

    private void connectToServer() throws IOException {
        client = new Socket("127.0.0.1", 8889);
        if (client.isConnected()) {
            dataInputStream = new DataInputStream(client.getInputStream());
            dataOutputStream = new DataOutputStream(client.getOutputStream());

            while (true) {
                // Vérifier s'il y a des données disponibles avant de lire à partir du flux
                if (dataInputStream.available() > 0) {
                    String line = dataInputStream.readUTF();

                    if (line.contains("start")) {
                        System.out.println("[CLIENT] Launching client");
                        break;
                    } else if (line.contains("color")) {
                        if (!isPlayer1Connected) {
                            playerColor = PieceSquareColor.WHITE;
                            isPlayer1Connected = true;
                        } else if (!isPlayer2Connected) {
                            playerColor = PieceSquareColor.BLACK;
                            isPlayer2Connected = true;
                        }
                        System.out.println("[CLIENT] Couleur donnée " + playerColor);

                        if (playerColor == PieceSquareColor.WHITE) {
                            Platform.runLater(() -> {
                                LauncherWhiteClient launcher = new LauncherWhiteClient();
                                try {
                                    launcher.start(new Stage());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                        else if (playerColor == PieceSquareColor.BLACK) {
                            Platform.runLater(() -> {
                                LauncherBlackClient launcher = new LauncherBlackClient(model);
                                try {
                                    launcher.start(new Stage());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        // Envoyer un message au serveur pour indiquer qu'un joueur est connecté
                        if (isPlayer1Connected || isPlayer2Connected) {
                            try {
                                dataOutputStream.writeUTF("Player connected");
                                dataOutputStream.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }




                    } else if (line.equals("Client connecté")) {
                        System.out.println("[CLIENT] Message serveur: " + line);
                        // Effectuer une action lorsque le message "Client connected" est reçu
                    }
                } else {
                    // Attendre un court instant pour permettre au flux d'entrée de se remplir
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("[CLIENT] Error connection");
        }
    }


    private void setupModelControllerView() {
        this.model = new Model();
        this.controller = new Controller(dataInputStream, dataOutputStream);
        ((Mediator) controller).setView(view);
        ((Mediator) controller).setModel(model);
    }

    @Override
    public void start(Stage primaryStage) {
        setupStage(primaryStage);
        startServerListenerThread();
    }

    private void setupStage(Stage stage) {
        stage.setScene(new Scene(this.view, GuiConfig.HEIGHT, GuiConfig.HEIGHT));
        stage.setTitle("Jeu de dames " + playerColor);
        stage.show();
    }

    private void startServerListenerThread() {
        Thread thread = new Thread(future);
        thread.start();
    }

    private void setupServerListener() {
        future = () -> {
            try {
                while (true) {
                    String line = dataInputStream.readUTF();

                    if (line.contains("move")) {
                        System.out.println("[CLIENT] Received move: " + line);
                        // Effectuer une action lorsque le message "move" est reçu
                    } else if (line.contains("color")) {
                        if (!isPlayer1Connected) {
                            playerColor = PieceSquareColor.WHITE;
                            isPlayer1Connected = true;
                        } else if (!isPlayer2Connected) {
                            playerColor = PieceSquareColor.BLACK;
                            isPlayer2Connected = true;
                        }
                        System.out.println("[CLIENT] Couleur donnée " + playerColor);
                    } else if (line.equals("Player connected")) {
                        System.out.println("[CLIENT] Player connected");
                    } else {
                        System.out.println("[CLIENT] Received message: " + line);
                        // Faites quelque chose lorsque vous recevez d'autres messages du serveur
                    }
                }
            } catch (SocketException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        dataInputStream.close();
        dataOutputStream.close();
        client.close();
    }




}
