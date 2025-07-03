package com.genericautosplitter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("example")
public interface GenericAutosplitterConfig extends Config
{
	@ConfigItem(
			position = 0,
			keyName = "port",
			name = "Port",
			description = "Port for the LiveSplit server. (Restart required)"
	)
	default int port() {
		return 16834;
	}

	@ConfigSection(
			name = "Customization Instructions",
			description = "Instructions on how to edit a config",
			position = 1,
			closedByDefault = true
	)
	String instructionsSection = "instructionsSect";

	@ConfigItem(
			keyName = "instructions",
			name = "Instructions",
			description = "Instructions on how to edit a config",
			position = 1,
			section = "instructionsSect"
	)
	default String instructions()
	{
		return "Open LiveSplit. Right click it and start TCP Server. Open the RuneLite sidebar and press connect.\n" +
				"To add automatic splits, first make sure you have splits showing on LiveSplit.\n" +
				"Get the Watchdog plugin from the plugin hub. Create any trigger you want, and give it a NotificationEvent according to the readme.";
	}

}
