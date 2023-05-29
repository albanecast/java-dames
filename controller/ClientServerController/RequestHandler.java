package atelier3.controller.ClientServerController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class RequestHandler extends Thread {
    private final ServerController serverController;
    private final Socket socket;

    public RequestHandler(Socket socket, ServerController serverController) {
        this.socket = socket;
        this.serverController = serverController;

        // Mettre à jour le nombre de joueurs connectés
        serverController.clientAdd(socket);
        serverController.playerCount++;
    }

    /**
     * Envoie une mise à jour à tous les clients
     *
     * @param message message à envoyer
     */
    private void sendUpdate(String message) {
        try (DataOutputStream broadcast = new DataOutputStream(socket.getOutputStream())) {
            for (Socket client : serverController.getClients()) {
                broadcast.writeUTF(message);
                broadcast.flush();
            }
        } catch (IOException e) {
            System.out.println("[SERVER] Erreur : " + e.getMessage());
        }
    }


    public void run() {
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            boolean isRunning = true;
            serverController.clientAdd(socket);
            Socket client = serverController.getClient();
            if (client != null) {
                serverController.sendPlayerConnected(client);
            }

            out.writeUTF("[SERVER] Client connecté");

            if (ServerController.getClientNbr() == 1) {
                out.writeUTF("[SERVER] color WHITE");
            } else {
                out.writeUTF("[SERVER] color BLACK");
            }

            while (isRunning) {
                String line = in.readUTF();

                if (line.equals("exit")) {
                    System.out.println("[SERVER] Connexion arrêtée par le client [1/2]");
                    break;
                } else if (line.contains("move")) {
                    System.out.println("[MOVE]: " + line);
                    System.out.println("[MOVE]: " + serverController.getClients());
                    sendUpdate(line);
                }

                if (line.equals("Player connected")) {
                    System.out.println("[SERVER] Player 1 connected");
                    sendUpdate("Player connected");
                }

                System.out.println("[Listening] " + line);
            }

            serverController.clientRemove(socket);
        } catch (SocketException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
