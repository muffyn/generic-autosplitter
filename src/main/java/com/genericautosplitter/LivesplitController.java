package com.genericautosplitter;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LivesplitController {
    private final Client client;
    private final GenericAutosplitterConfig config;
    private final GenericAutosplitterPlugin splitter;
    private PrintWriter writer;
    private BufferedReader reader;
    private Socket socket;
    protected boolean connected = false;

    LivesplitController(Client client, GenericAutosplitterConfig config, GenericAutosplitterPlugin splitter) {
        this.client = client;
        this.config = config;
        this.splitter = splitter;
    }

    public void tryConnect() {
        try {
            socket = new Socket("localhost", config.port());
            writer = new PrintWriter(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;

        } catch (Exception e) {
            connected = false;
            if (client.getGameState() == GameState.LOGGED_IN) {
                String message = "Could not start socket, did you start the LiveSplit server?";
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
            }
        }
    }

    public void disconnect() {
        try {
            connected = false;
            socket.close();

        } catch (Exception ignored) {}
    }

    public void sendMessage(String message) {
        if (writer != null) {
            writer.write(message + "\r\n");
            writer.flush();
        }
    }

    private String receiveMessage() {
        if (reader != null) {
            try {
                return reader.readLine();
            } catch (IOException e) {
                return "ERROR";
            }
        }
        return "ERROR";
    }

    public void startRun() {
        sendMessage("initgametime");
        sendMessage("starttimer");
    }

    public void split() {

        pauseGameTime();

        splitter.setTime();
        sendMessage("split");

        unpauseGameTime();

    }

    public void skip() {
        sendMessage("skipsplit");
    }

    public void undo() {
        sendMessage("undosplit");
    }

    public void pauseGameTime() {
        sendMessage("pausegametime");
    }

    public void unpauseGameTime() {
        sendMessage("unpausegametime");
    }

    public void pause() {
        sendMessage("pause");
    }

    public void resume() {
        sendMessage("resume");
    }

    public void setGameTime(String time) {
        sendMessage("setgametime " + time);
    }

    public void endRun() {
        sendMessage("getcurrenttimerphase");
        String msg = receiveMessage();

        while (!msg.equals("ERROR")) {
            switch (msg) {
                case "Running":
                    sendMessage("getsplitindex");
                    String i = receiveMessage();
                    sendMessage("skipsplit");
                    sendMessage("getsplitindex");
                    String j = receiveMessage();
                    if (i.equals(j)) {
                        split();
                    }
                    break;
                case "Paused":
                    sendMessage("resume");
                    break;
                case "Ended":
                case "NotRunning":
                    return;
            }
            sendMessage("getcurrenttimerphase");
            msg = receiveMessage();
        }
    }

    public void reset() {
        sendMessage("reset");
    }
}
