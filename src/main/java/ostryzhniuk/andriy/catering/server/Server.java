package ostryzhniuk.andriy.catering.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommand;
import ostryzhniuk.andriy.catering.ordering.view.dto.DtoOrdering;
import ostryzhniuk.andriy.catering.server.mysql.DB_Connector;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static ostryzhniuk.andriy.catering.commands.ClientCommandTypes.UPDATE_ORDERING;


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
    private ObjectInputStream objectIsSocket;

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
            Object inputObject;
            LOGGER.info(this.startDateTime + " run");
            ClientCommand clientCommand;
            Object responseObject;

            while ((inputObject = objectIsSocket.readObject()) != null) {
                clientCommand = (ClientCommand) inputObject;
                responseObject = clientCommand.processCommand();
//                List list = (List) responseObject;
//                LOGGER.info("list size: " + list.size());
                this.objectOsSocket.writeObject(responseObject);
            }
        } catch (Throwable t) {
            LOGGER.error(this.startDateTime + " Thread is dead", t);
        }
    }
}
