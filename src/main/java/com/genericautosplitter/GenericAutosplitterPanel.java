package com.genericautosplitter;

import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class GenericAutosplitterPanel extends PluginPanel
{
    private final Client client;
    private final GenericAutosplitterConfig config;
    private final GenericAutosplitterPlugin splitter;
    private final LivesplitController livesplitController;
    private JLabel status;

    @Inject
    GenericAutosplitterPanel(Client client, GenericAutosplitterConfig config, GenericAutosplitterPlugin splitter, LivesplitController livesplitController){
        this.client = client;
        this.config = config;
        this.splitter = splitter;
        this.livesplitController = livesplitController;
    }

    public void startPanel(){
        getParent().setLayout(new BorderLayout());
        getParent().add(this, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        final JPanel layout = new JPanel();
        BoxLayout boxLayout = new BoxLayout(layout, BoxLayout.Y_AXIS);
        layout.setLayout(boxLayout);
        add(layout, BorderLayout.NORTH);

        JPanel statusFrame = new JPanel();
        statusFrame.setLayout(new GridBagLayout());
        statusFrame.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.CYAN), "Status"));

        status = new JLabel("Not connected");
        status.setForeground(Color.RED);
        statusFrame.add(status);


        JPanel connectionFrame = new JPanel();
        connectionFrame.setLayout(new GridLayout(2, 1));
        connectionFrame.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.CYAN), "Connection"));

        JButton b_connect = new JButton("Connect");
        JButton b_disconnect = new JButton("Disconnect");
        b_connect.setFocusable(false);
        b_disconnect.setFocusable(false);

        b_connect.addActionListener(e -> splitter.connect());
        b_disconnect.addActionListener(e -> splitter.disconnect());

        connectionFrame.add(b_connect);
        connectionFrame.add(b_disconnect);


        JPanel controllerFrame = new JPanel();
        controllerFrame.setLayout(new GridLayout(6, 1));
        controllerFrame.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.CYAN), "LiveSplit Controller"));

        JButton[] controllerButtons = {new JButton("Split"), new JButton("Reset"), new JButton("Undo split"),
                new JButton("Skip split"), new JButton("Pause"), new JButton("Resume")};
        String[] controls = {"startorsplit", "reset", "unsplit", "skipsplit", "pause", "resume"};

        for (int i = 0; i < controllerButtons.length; i++){
            int finalI = i; // because lambda forces my hand
            controllerButtons[i].addActionListener(e -> livesplitController.sendMessage(controls[finalI]));

            controllerButtons[i].setFocusable(false);
            controllerFrame.add(controllerButtons[i], BorderLayout.CENTER);
        }

        JPanel debugFrame = new JPanel();
        debugFrame.setLayout(new GridLayout(4, 1));
        debugFrame.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.ORANGE), "Debug Controller"));

        JButton b_setoffset = new JButton("Toggle offset");
        b_setoffset.addActionListener(e -> splitter.setUseOffset());
        JButton b_startrun = new JButton("Start run");
        b_startrun.addActionListener(e -> splitter.startRun());
        JButton b_splitrun = new JButton("Split (game time)");
        b_splitrun.addActionListener(e -> splitter.split());
        JButton b_stoprun = new JButton("Complete run");
        b_stoprun.addActionListener(e -> splitter.stopRun());


        debugFrame.add(b_setoffset, BorderLayout.CENTER);
        debugFrame.add(b_startrun, BorderLayout.CENTER);
        debugFrame.add(b_splitrun, BorderLayout.CENTER);
        debugFrame.add(b_stoprun, BorderLayout.CENTER);

        layout.add(statusFrame);
        layout.add(Box.createRigidArea(new Dimension(0, 15)));
        layout.add(connectionFrame);
        layout.add(Box.createRigidArea(new Dimension(0, 15)));
        layout.add(controllerFrame);
        layout.add(Box.createRigidArea(new Dimension(0, 15)));
        layout.add(debugFrame);
    }

    protected void set_connected(){
        status.setText("Connected");
        status.setForeground(Color.GREEN);
    }

    protected void set_disconnected(){
        status.setText("Not connected");
        status.setForeground(Color.RED);
    }
}