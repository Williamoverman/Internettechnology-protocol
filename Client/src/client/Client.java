package client;

import utils.Connection;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter writer;
    private Scanner scanner;
    private Thread readerThread;

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {
        try {
            connect();
            startMessageListener();
            handleUserInput();
        } catch (IOException ex) {
            System.err.println("Connection error: " + ex.getMessage());
        } finally {
            disconnect();
        }
    }

    private static void connect() {
        try (Socket socket = new Socket(Connection.SERVER_ADDRESS, Connection.SERVER_PORT)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);

            String greeting = reader.readLine();
            System.out.println("Server: " + greeting);

            Thread readerThread = new Thread(() -> {
               try {
                   String line;
                   while ((line = reader.readLine()) != null) {
                        if (line.equalsIgnoreCase("ping"))
                            writer.println("PONG");
                        System.out.println("Server " + line);
                   }
               } catch (IOException e) {
                   System.out.println("Connection closed");
               }
            });
            readerThread.start();

            System.out.println("\nEnter commands (e.g., LOGON {\"username\":\"yourname\"}):");
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                if (command.equalsIgnoreCase("exit"))
                    break;
                writer.println(command);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void disconnect() {
        try {
            if (socket != null) socket.close();
            if (bufferedReader != null) bufferedReader.close();
            if (writer != null) writer.close();
            if (scanner != null) scanner.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}