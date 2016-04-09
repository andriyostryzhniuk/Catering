package ostryzhniuk.andriy.catering.example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        try (
                Socket kkSocket = new Socket("localhost", 2000);
        ) {
            LOGGER.info("I connected!");
            PrintWriter socketPrintWriter = new PrintWriter(kkSocket.getOutputStream(), true);

            BufferedReader socketBufferedReader = new BufferedReader(
                    new InputStreamReader(kkSocket.getInputStream()));

            BufferedReader bufferedReaderIn = new BufferedReader(new InputStreamReader(System.in));

            String fromServer;
            String fromUser;

            socketPrintWriter.println("Hi server!!");
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
