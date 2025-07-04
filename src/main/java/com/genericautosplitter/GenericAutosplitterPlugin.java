package com.genericautosplitter;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NotificationFired;
import net.runelite.client.events.PluginMessage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;

@Slf4j
@PluginDescriptor(
	name = "Generic Autosplitter",
	description = "Syncs LiveSplit to your time played and sends splits on Watchdog events"
)
public class GenericAutosplitterPlugin extends Plugin
{
	protected static final Logger logger = LoggerFactory.getLogger(GenericAutosplitterPlugin.class);

	protected int ticks;
	protected int offset;
	protected boolean useOffset;

	private int minutes = 0;

	@Inject
	private Client client;

	@Inject
	private GenericAutosplitterConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ClientToolbar clientToolbar;

	// side panel
	private NavigationButton navButton;
	private GenericAutosplitterPanel panel;
	private LivesplitController livesplitController;

	// is the timer running?
    boolean started = false;
	boolean paused = false;
	protected long before;
	protected GameState lastState;

	@Provides
	GenericAutosplitterConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(GenericAutosplitterConfig.class);
	}

	@Override
	protected void startUp() {
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/autosplitter_icon.png");
		livesplitController = new LivesplitController(client, config, this);
		panel = new GenericAutosplitterPanel(client, config,this, livesplitController);
		navButton = NavigationButton.builder().tooltip("Generic Autosplitter")
				.icon(icon).priority(6).panel(panel).build();
		clientToolbar.addNavigation(navButton);
		panel.startPanel();
	}

	@Override
	protected void shutDown() {

		disconnect();
		clientToolbar.removeNavigation(navButton);

	}

	public void connect() {
		livesplitController.connect();

		if (livesplitController.connected) {
			livesplitController.pause();
			panel.setConnected();

			offset = loadTime();
			panel.loadOffset(offset);
			if (useOffset) {
				panel.enableOffset();
			} else {
				panel.disableOffset();
			}
		}
	}

	public void disconnect() {
		if (started && !paused) {
			pause();
			started = false;
		}

		saveTime(ticks);
		livesplitController.disconnect();
		panel.setDisconnected();

		if (started) {
			saveTime(ticks);
		} else {
			saveTime(getTimePlayed() * 100);
		}
	}

	/*
	 * Event handlers
	 */

	@Subscribe
	public void onGameTick(GameTick event) {
		if (started && !paused) {
			ticks += 1;
		}
	}

	@Subscribe
	public void onNotificationFired(NotificationFired event) {
		final String msg = event.getMessage();
		if (msg.equalsIgnoreCase("autosplitter:split")) {
			split();
		}
	}

	// watchdog does not yet implement this
	@Subscribe
	public void onPluginMessage(PluginMessage event) {
		if ("autosplitter".equalsIgnoreCase(event.getNamespace())) {
			Map<String, Object> data = event.getData();
		}
	}

	@Subscribe
	public void onVarClientIntChanged(VarClientIntChanged event) {
		if (client.getVarcIntValue(526) > minutes) {
			minutes = client.getVarcIntValue(526);
			split(); //debug
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event) {
		GameState state = event.getGameState();
		long now = Instant.now().toEpochMilli();

		if (started) {
			if (lastState == GameState.LOADING || lastState == GameState.CONNECTION_LOST) {
				// figure out how many GameTick events we lost, round it down, and add it to total
				// bug: sometimes off by 1, maybe just server lag
				int lostTicks = (int) Math.floor((now - before) / (1000.0f * 0.6f));
				logger.info("Giving a {}t adjustment", lostTicks);
				ticks += lostTicks;
				setTime();
			}

			/* debug */
			long duration = now - before;
			float decimal = duration / 1000.0f;
			logger.info("[{}t | {}s] {}", (decimal / 0.6f), decimal, lastState);
			/* end debug */

			if (state == GameState.HOPPING) {
				// bug: the timer pauses for 1 tick too long relative to real time each world hop
				ticks += 1;
			}

			if (paused && (state == GameState.LOGGED_IN || state == GameState.LOADING || state == GameState.CONNECTION_LOST)) {
				resume();

			} else if (!paused && (state == GameState.HOPPING || state == GameState.LOGIN_SCREEN)) {
				pause();
			}
		}

		before = now;
		lastState = state;
	}

	/*
	 * Run control flow methods
	 */

	public void startRun() {
		if (started) {
			return;
		}
		started = true;
		paused = false;
		before = Instant.now().toEpochMilli();

		if (useOffset) {
			ticks = offset;
		} else {
			ticks = 0;
		}

		livesplitController.startRun();
		setTime();

		GameState state = client.getGameState();
		if (state == GameState.LOGIN_SCREEN || state == GameState.HOPPING || state == GameState.LOGGING_IN) {
			pause();
		}
	}

	public void split() {
		livesplitController.split();
	}

	public void pause() {
		livesplitController.pause();
		setTime();
		paused = true;
	}

	public void resume() {
		setTime();
		livesplitController.resume();
		paused = false;
	}

	public void undo() {
		livesplitController.undo();
	}

	public void skip() {
		livesplitController.skip();
	}

	public void endRun() {
		// does not automatically reset, in case the user wishes to undo
		livesplitController.endRun();
	}

	public void reset() {
		livesplitController.reset();
	}

	public void setTime() {
		String time = buildTimeStr(ticks);
		livesplitController.setGameTime(time);
		saveTime(ticks);
	}

	public void toggleOffset() {
		if (useOffset) {
			useOffset = false;
			panel.disableOffset();
		} else {
			useOffset = true;
			panel.enableOffset();
		}
	}

	public String buildTimeStr(int duration) {
		return BigDecimal.valueOf((duration) * 0.6).setScale(1, RoundingMode.HALF_UP).toString();
	}

	public int getTimePlayed() {
		return client.getVarcIntValue(526);
	}

	private void saveTime(int duration) {
		configManager.setRSProfileConfiguration("autosplitter", "duration", duration);
	}

	private int loadTime() {
		try {
			int time = Integer.parseInt(configManager.getRSProfileConfiguration("autosplitter", "duration"));
			if (time == 0) {
				time = getTimePlayed() * 100;
			}
			useOffset = true; // found previous paused run or account age
			return time;
		} catch (Exception e) {
			useOffset = false; // no previous data found
			return getTimePlayed() * 100;
		}
	}
}
