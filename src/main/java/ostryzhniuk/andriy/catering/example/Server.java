package ostryzhniuk.andriy.catering.example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

public class Server implements Runnable {

    private final Socket clientSocket;
    private final OutputStream socketOutputStream;
    private final InputStream socketInputStream;
    private final PrintWriter socketPrintWriter;
    private final BufferedReader socketBufferedReader;
    private final InputStreamReader socketInputStreamReader;
    private final OutputStreamWriter socketOutputStreamWriter;
    private final LocalDateTime startDateTime;

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        try (
                ServerSocket serverSocket = new ServerSocket(2000);
        ) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("Seems like connected");
                Server server = new Server(clientSocket);
                Thread thread = new Thread(server);
                thread.start();
            }
        } catch (IOException e) {
            LOGGER.error(null, e);
            throw new UncheckedIOException(e);
        }
    }

    public Server(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.socketOutputStream = clientSocket.getOutputStream();
        this.socketInputStream = clientSocket.getInputStream();
        this.socketInputStreamReader = new InputStreamReader(this.socketInputStream);
        this.socketPrintWriter = new PrintWriter(this.socketOutputStream, true);
        this.socketBufferedReader = new BufferedReader(this.socketInputStreamReader);
        this.socketOutputStreamWriter = new OutputStreamWriter(this.socketOutputStream);
        this.startDateTime = LocalDateTime.now();
        LOGGER.info("Server {} for Client has been created", this.startDateTime);
    }


    @Override
    public void run() {
        try {
            LOGGER.info(this.startDateTime + " run");
            String inputLine;
            try {
                while ((inputLine = socketBufferedReader.readLine()) != null) {
                    socketPrintWriter.println("got from client stream: " + inputLine);
                    LOGGER.info(this.startDateTime + " got from client " + inputLine);
//                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Throwable t) {
            LOGGER.error(this.startDateTime + " Thread is dead", t);
        }
    }
}
