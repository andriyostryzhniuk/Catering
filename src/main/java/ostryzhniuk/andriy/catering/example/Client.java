package ostryzhniuk.andriy.catering.example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.dto.DtoOrdering;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 2000);
        ) {
            LOGGER.info("I connected!");
            OutputStream socketOutputStream = socket.getOutputStream();
            PrintWriter socketPrintWriter = new PrintWriter(socketOutputStream, true);

            BufferedReader socketBufferedReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            BufferedReader bufferedReaderIn = new BufferedReader(new InputStreamReader(System.in));

            String fromServer;
            String fromUser;

            DtoOrdering dtoOrdering = new DtoOrdering(new Date(), "some text", 24.0, 2.0, 8.0);

            ObjectOutputStream objectSocketOS = new ObjectOutputStream(socketOutputStream);

//            socketPrintWriter.println("Hi server!!");
            objectSocketOS.writeObject(dtoOrdering);
            objectSocketOS.writeObject(dtoOrdering);

            while ((fromServer = socketBufferedReader.readLine()) != null) {
                LOGGER.info("fromServer " + fromServer);
//                break;

                fromUser = bufferedReaderIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    socketPrintWriter.println(fromUser);
                }
            }

        } catch (UnknownHostException e) {
            throw new UncheckedIOException(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

}
