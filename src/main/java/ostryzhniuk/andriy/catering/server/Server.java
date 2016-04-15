package ostryzhniuk.andriy.catering.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommand;
import ostryzhniuk.andriy.catering.server.mysql.DB_Connector;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;


public class Server implements Runnable {

    private final Socket clientSocket;
    private final OutputStream socketOutputStream;
    private final InputStream socketInputStream;
    private final PrintWriter printWriterSocket;
    private final BufferedReader bufferedReaderSocket;
    private final InputStreamReader isReaderSocket;
    private final OutputStreamWriter osWriterSocket;
    private final LocalDateTime startDateTime;
    private final ObjectOutputStream objectOsSocket;
    private final ObjectInputStream objectIsSocket;

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        try (
                ServerSocket serverSocket = new ServerSocket(2000);
        ) {
            while (true) {
                DB_Connector.getDataSource();
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
        this.isReaderSocket = new InputStreamReader(this.socketInputStream);
        this.printWriterSocket = new PrintWriter(this.socketOutputStream, true);
        this.bufferedReaderSocket = new BufferedReader(this.isReaderSocket);
        this.osWriterSocket = new OutputStreamWriter(this.socketOutputStream);
        this.startDateTime = LocalDateTime.now();
        this.objectIsSocket = new ObjectInputStream(this.socketInputStream);
        this.objectOsSocket = new ObjectOutputStream(this.socketOutputStream);
        LOGGER.info("Server {} for Client has been created", this.startDateTime);
    }


    @Override
    public void run() {
        try {
            LOGGER.info(this.startDateTime + " run");
//            String inputLine;
            ClientCommand clientCommand = (ClientCommand) this.objectIsSocket.readObject();
            Object responseObject = clientCommand.processCommand();
            this.objectOsSocket.writeObject(responseObject);

//            try {
//                while ((inputLine = bufferedReaderSocket.readLine()) != null) {
//                    printWriterSocket.println("got from client stream: " + inputLine);
//                    LOGGER.info(this.startDateTime + " got from client " + inputLine);
////                    break;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        } catch (Throwable t) {
            LOGGER.error(this.startDateTime + " Thread is dead", t);
        }
    }
}
