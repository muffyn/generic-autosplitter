package com.genericautosplitter;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "Generic Autosplitter",
	description = "Connects to LiveSplit and sends splits on arbitrary events"
)
public class GenericAutosplitterPlugin extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(GenericAutosplitterPlugin.class);

	// The number of quests completed. If this increases during a run, we've completed the quest.
	private int questsComplete;
	private int ticks;

	// The variables to interact with livesplit
	PrintWriter writer;
	BufferedReader reader;

	@Inject
	private Client client;

	@Inject
	private GenericAutosplitterConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	// side panel
	private NavigationButton navButton;
	private GenericAutosplitterPanel panel;

	// is the timer running?
	private boolean started = false;
	private boolean paused = false;

	private List<Pair<Integer, Integer>> itemList;
	private List<Pair<Integer, Integer>> varbList;
	private List<Pair<Integer, Integer>> varpList;

	@Provides
	GenericAutosplitterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GenericAutosplitterConfig.class);
	}

	@Override
	protected void startUp()
	{
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/generic_autosplitter_icon.png");
		panel = new GenericAutosplitterPanel(client, writer, reader, config, this);
		navButton = NavigationButton.builder().tooltip("Generic Autosplitter")
				.icon(icon).priority(6).panel(panel).build();
		clientToolbar.addNavigation(navButton);

		itemList = new ArrayList<>();
		varbList = new ArrayList<>();
		varpList = new ArrayList<>();

		panel.startPanel();
	}

	@Override
	protected void shutDown()
	{
		sendMessage("pause");
		itemList = null;
		varbList = null;
		varpList = null;
		clientToolbar.removeNavigation(navButton);
		panel.disconnect();  // terminates active socket
	}

	private void setupSplits(String configStr) {
		itemList = new ArrayList<>();
		varbList = new ArrayList<>();
		varpList = new ArrayList<>();

		final String[] configList = configStr.split("\n");

		for (String line : configList) {
			final String[] args = line.split(",");
			final Pair<Integer, Integer> pair;
			try {
				int type = Integer.parseInt(args[1]);
				if (type == 0) {
					if (args.length < 4) { // default 1 item
						pair = new Pair<>(Integer.parseInt(args[2]), 1);
					} else {
						pair = new Pair<>(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
					}
					itemList.add(pair);
				} else if (type == 1) {
					pair = new Pair<>(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
					varbList.add(pair);
				} else if (type == 2) {
					pair = new Pair<>(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
					varpList.add(pair);
				} else {
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Autosplit: could not parse line: " + line, null);
				}
			} catch (Exception e) {
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Autosplit: could not parse line: " + line, null);
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		if (!started) {
			started = true;
			sendMessage("reset");
			sendMessage("initgametime");
			sendMessage("starttimer");
			logger.info("Starting");

			final String configStr;
			//FIXME
			//setupSplits(configStr);

		} else if (started) {
			logger.info("Final split achieved");
			started = false;
			sendMessage("getcurrenttimerphase");
			switch (receiveMessage()) {
				case "Running":
					sendMessage("pause");
					break;
				case "NotRunning:":
				case "Paused":
				case "Ended":
				default:
					break;
			}
		}
	}
	/*
	private void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOADING ||
				event.getGameState() == GameState.LOGGED_IN ||
				event.getGameState() == GameState.CONNECTION_LOST) {
			if (paused) {
				paused = false;

				myOverlay.seconds = getStoredSeconds();
				myOverlay.minutes = getStoredMinutes();
				if (myOverlay.minutes == 0) {
					myOverlay.minutes = client.getVarcIntValue(526);
				}

				// there is a 1.2 second penalty every time you hop
				myOverlay.seconds += 12;
				if (myOverlay.seconds >= 600) {
					myOverlay.minutes += 1;
					myOverlay.seconds -= 600;
				}

				// after penalty, see if we were desynced
				int timePlayed = client.getVarcIntValue(526);
				if (timePlayed > myOverlay.minutes) {
					myOverlay.minutes = timePlayed;
					myOverlay.seconds = 0;
				}

			}
		} else if (!paused) {
			paused = true;
			setStoredSeconds(myOverlay.seconds);
			setStoredMinutes(myOverlay.minutes);
		}
	}*/
	/*
	@Subscribe
	private void onGameStateChanged(GameStateChanged event) {
		if (started) {
			if (event.getGameState() == GameState.LOADING ||
					event.getGameState() == GameState.LOGGED_IN ||
					event.getGameState() == GameState.CONNECTION_LOST) {
				if (paused) {
					logger.info("Resuming");
					sendMessage("resume");
					paused = false;
				}
			} else if (!paused) {
				logger.info("Pausing");
				sendMessage("pause");
				paused = true;
			}
		}
	}
	*/


	public void completeRun() {
		started = false;
		sendMessage("getcurrenttimerphase");
		String msg = receiveMessage();
		logger.info("Completing");

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
						return;
					}
					break;
				case "Paused":
					sendMessage("resume");
					break;
				case "Ended":
					sendMessage("unsplit");
					break;
				case "NotRunning":
					return;
			}
			sendMessage("getcurrenttimerphase");
			msg = receiveMessage();
		}

	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event) {
		final ItemContainer itemContainer = event.getItemContainer();
		if (itemContainer != client.getItemContainer(InventoryID.INVENTORY)) {
			return;
		}

		for (Pair<Integer, Integer> pair : itemList) {
			if (itemContainer.count(pair.first) >= pair.second) {
				split();
				itemList.remove(pair);
			}
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event) {
		for (Pair<Integer, Integer> pair : varbList) {
			if (client.getVarbitValue(pair.first) == pair.second) {
				split();
				varbList.remove(pair);
			}
		}

		for (Pair<Integer, Integer> pair : varpList) {
			if (client.getVarpValue(pair.first) == pair.second) {
				split();
				varpList.remove(pair);
			}
		}
	}

	private void sendMessage(String message) {
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

	public void split() {
		logger.info("Splitting");
		sendMessage("pausegametime");
		sendMessage("setgametime " + BigDecimal.valueOf((ticks + 1) * 0.6).setScale(1, RoundingMode.HALF_UP));
		sendMessage("split");
		sendMessage("unpausegametime");
	}
}
