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

        JPanel debugFrame = new JPanel();
        debugFrame.setLayout(new GridLayout(7, 1));
        debugFrame.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.ORANGE), "Debug Controller"));

        JButton b_setoffset = new JButton("Toggle offset");
        b_setoffset.addActionListener(e -> splitter.setUseOffset());
        JButton b_startrun = new JButton("Start run");
        b_startrun.addActionListener(e -> splitter.startRun());
        JButton b_split = new JButton("Split");
        b_split.addActionListener(e -> splitter.split());
        JButton b_undo = new JButton("Undo split");
        b_undo.addActionListener(e -> splitter.undo());
        JButton b_skip = new JButton("Skip split");
        b_skip.addActionListener(e -> splitter.skip());
        JButton b_endrun = new JButton("Complete all remaining splits");
        b_endrun.addActionListener(e -> splitter.endRun());
        JButton b_reset = new JButton("Reset");
        b_reset.addActionListener(e -> splitter.reset());

        b_setoffset.setFocusable(false);
        debugFrame.add(b_setoffset, BorderLayout.CENTER);
        b_startrun.setFocusable(false);
        debugFrame.add(b_startrun, BorderLayout.CENTER);
        b_split.setFocusable(false);
        debugFrame.add(b_split, BorderLayout.CENTER);
        b_endrun.setFocusable(false);
        debugFrame.add(b_endrun, BorderLayout.CENTER);
        b_undo.setFocusable(false);
        debugFrame.add(b_undo, BorderLayout.CENTER);
        b_skip.setFocusable(false);
        debugFrame.add(b_skip, BorderLayout.CENTER);
        b_reset.setFocusable(false);
        debugFrame.add(b_reset, BorderLayout.CENTER);

        layout.add(statusFrame);
        layout.add(Box.createRigidArea(new Dimension(0, 15)));
        layout.add(connectionFrame);
        layout.add(Box.createRigidArea(new Dimension(0, 15)));
        layout.add(debugFrame);
    }

    protected void set_connected() {
        status.setText("Connected");
        status.setForeground(Color.GREEN);
    }

    protected void set_disconnected() {
        status.setText("Not connected");
        status.setForeground(Color.RED);
    }
}