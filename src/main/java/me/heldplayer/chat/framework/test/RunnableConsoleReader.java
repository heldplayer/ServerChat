
package me.heldplayer.chat.framework.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import me.heldplayer.chat.framework.ServerConnection;
import me.heldplayer.chat.framework.config.ServerEntry;

class RunnableConsoleReader implements Runnable {

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;

        try {
            while ((input = reader.readLine()) != null) {
                if (input.equalsIgnoreCase("stop")) {
                    ConnectionTest.connections.stopListening();
                    System.out.println("Stopped");
                }
                else if (input.equalsIgnoreCase("save")) {
                    ConnectionTest.connections.save();
                    ;
                    System.out.println("Saved");
                }
                else if (input.equalsIgnoreCase("load")) {
                    ConnectionTest.connections.load();
                    System.out.println("Loaded");
                }
                else if (input.equalsIgnoreCase("connect")) {
                    System.err.print("Ip: ");
                    String ip = reader.readLine();
                    System.err.print("Port: ");
                    String port = reader.readLine();
                    ServerEntry entry = new ServerEntry();
                    entry.setIp(ip);
                    entry.setPort(Integer.parseInt(port));
                    ConnectionTest.connections.addConnection(new ServerConnection(ConnectionTest.connections, entry));
                    System.out.println("Connecting to " + ip + ":" + port);
                }
                else {
                    System.out.println(input);
                }

                Thread.sleep(10L);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
