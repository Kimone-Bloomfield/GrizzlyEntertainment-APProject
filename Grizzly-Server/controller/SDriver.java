package controller;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.ServerSocket;
import java.net.Socket;

public class SDriver {
    private static final Logger logger = LogManager.getLogger(SDriver.class);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1024);
        int clientCount = 0;

        System.out.println("Server started at " + new java.util.Date());

        while (true) {
            clientCount++;
            Socket connectionSocket = serverSocket.accept();

            System.out.println("Serving client" + clientCount);
            logger.info("Serving Client" + clientCount);

            Server server = new Server(connectionSocket, clientCount);
            server.start();
        }
    }
}


