package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server;

import java.io.*;
import java.net.Socket;

public class ServerHandler extends Thread {
    public final Socket socket;
    public int id;
    public final int myId;
    PrintWriter out;
    BufferedReader in;

    public ServerHandler(Socket socket, int myId) {
        this.socket = socket;
        this.myId = myId;
    }

    @Override
    public void run() {
        try {

            //what is sent back to the other server
            out = new PrintWriter(socket.getOutputStream(), true);

            //what the server receive
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            out.println(myId);

            id = Integer.parseInt(in.readLine());

            listener();

        } catch (IOException e) {
            interrupt();
            e.printStackTrace();
        }
    }

    public void listener() throws IOException {
        String input;

        while (true) {
            if ((input = in.readLine()) != null) {

            }
        }
    }

    public String sendMessage(String msg) {
        out.println(msg);
        String resp = null;
        try {
            resp = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            resp = "";
        }
        return resp;
    }

    @Override
    public void interrupt() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.interrupt();
    }
}