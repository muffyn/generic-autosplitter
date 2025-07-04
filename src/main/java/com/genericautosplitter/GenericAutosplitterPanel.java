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
    private JLabel l_time;
    private JTextField tf_time;
    private JPanel tickFrame;
    private JButton b_toggleoffset;
    private JButton b_startreset;
    private JPanel offsetFrame;
    private JPanel controlFrame;

    @Inject
    GenericAutosplitterPanel(Client client, GenericAutosplitterConfig config, GenericAutosplitterPlugin splitter, LivesplitController livesplitController) {
        this.client = client;
        this.config = config;
        this.splitter = splitter;
        this.livesplitController = livesplitController;
    }

    public void startPanel() {
        getParent().setLayout(new BorderLayout());
        getParent().add(this, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        final JPanel layout = new JPanel();
        BoxLayout boxLayout = new BoxLayout(layout, BoxLayout.Y_AXIS);
        layout.setLayout(boxLayout);
        add(layout, BorderLayout.NORTH);

        // status
        JPanel statusFrame = new JPanel();
        statusFrame.setLayout(new GridBagLayout());
        statusFrame.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.CYAN), "Status"));

        status = new JLabel("Not connected");
        status.setForeground(Color.RED);
        statusFrame.add(status);

        // connect buttons
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

        // control buttons
        controlFrame = new JPanel();
        controlFrame.setLayout(new GridLayout(5, 1));
        controlFrame.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.ORANGE), "Controller"));

        b_startreset = new JButton("Start run");
        b_startreset.addActionListener(e -> startOrReset());
        JButton b_split = new JButton("Split");
        b_split.addActionListener(e -> splitter.split());
        JButton b_undo = new JButton("Undo split");
        b_undo.addActionListener(e -> splitter.undo());
        JButton b_skip = new JButton("Skip split");
        b_skip.addActionListener(e -> splitter.skip());
        JButton b_endrun = new JButton("Complete all remaining splits");
        b_endrun.addActionListener(e -> splitter.endRun());

        b_startreset.setFocusable(false);
        controlFrame.add(b_startreset, BorderLayout.CENTER);
        b_split.setFocusable(false);
        controlFrame.add(b_split, BorderLayout.CENTER);
        b_undo.setFocusable(false);
        controlFrame.add(b_undo, BorderLayout.CENTER);
        b_skip.setFocusable(false);
        controlFrame.add(b_skip, BorderLayout.CENTER);
        b_endrun.setFocusable(false);
        controlFrame.add(b_endrun, BorderLayout.CENTER);

        // offset
        offsetFrame = new JPanel();
        offsetFrame.setLayout(new GridLayout(4, 1));
        offsetFrame.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.LIGHT_GRAY), "Offset"));

        l_time = new JLabel();
        offsetFrame.add(l_time, BorderLayout.WEST);

        tickFrame = new JPanel();
        tickFrame.setLayout(new GridLayout(1, 2));
        JLabel l_tick = new JLabel("Ticks:");
        tickFrame.add(l_tick);
        tf_time = new JTextField();
        tf_time.addActionListener(e -> updateTextField());
        tickFrame.add(tf_time);
        offsetFrame.add(tickFrame);
        tickFrame.setVisible(false);

        b_toggleoffset = new JButton("");
        b_toggleoffset.addActionListener(e -> splitter.toggleOffset());
        b_toggleoffset.setFocusable(false);
        offsetFrame.add(b_toggleoffset);

        JButton b_gametime = new JButton("Set offset to Time Played");
        b_gametime.addActionListener(e -> setOffsetToTimePlayed());
        b_gametime.setFocusable(false);
        offsetFrame.add(b_gametime);

        // build panel

        layout.add(statusFrame);
        layout.add(Box.createRigidArea(new Dimension(0, 15)));
        layout.add(connectionFrame);
        layout.add(Box.createRigidArea(new Dimension(0, 15)));
        layout.add(controlFrame);
        layout.add(Box.createRigidArea(new Dimension(0, 15)));
        layout.add(offsetFrame);

        offsetFrame.setVisible(false);
        controlFrame.setVisible(false);
    }

    protected void setConnected() {
        status.setText("Connected");
        status.setForeground(Color.GREEN);
        offsetFrame.setVisible(true);
        controlFrame.setVisible(true);
    }

    protected void setDisconnected() {
        status.setText("Not connected");
        status.setForeground(Color.RED);
        resetRun();
        offsetFrame.setVisible(false);
        controlFrame.setVisible(false);
    }

    protected void updateTextField() {
        try {
            int ticks = Integer.parseInt(tf_time.getText());
            setTime(ticks);
            splitter.offset = ticks;
        } catch (NumberFormatException ignored) {
        }
    }

    protected void loadOffset(int offset) {
        tf_time.setText(offset + "");
    }

    protected void setTime(int ticks) {
        if (splitter.useOffset) {
            int time = ticks * 6;
            String timeStr = String.format("%02d:%02d:%02d.%01d", (time / 10) / 3600, ((time / 10) / 60) % 60, (time / 10) % 60, time % 10);
            l_time.setText("Next timer will start at " + timeStr);
        }
        tf_time.setText(ticks + "");
    }

    protected void enableOffset() {
        tickFrame.setVisible(true);
        updateTextField();
        b_toggleoffset.setText("Disable offset");
    }

    protected void disableOffset() {
        tickFrame.setVisible(false);
        l_time.setText("Next timer will start at 0.0s");
        b_toggleoffset.setText("Enable offset");
    }

    protected void startRun() {
        b_startreset.setText("Reset");
        splitter.startRun();
        offsetFrame.setVisible(false);
    }

    protected void resetRun() {
        b_startreset.setText("Start run");
        splitter.reset();
        offsetFrame.setVisible(true);
    }

    protected void startOrReset() {
        if (!splitter.started) {
            startRun();
        } else {
            resetRun();
        }
    }

    protected void setOffsetToTimePlayed() {
        int offset = splitter.getTimePlayed() * 100;
        splitter.useOffset = true;
        loadOffset(offset);
        enableOffset();

    }

}