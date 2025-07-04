package com.genericautosplitter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("autosplitter")
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
			description = "Instructions",
			position = 1
	)
	String instructionsSection = "instructionsSect";

	@ConfigItem(
			keyName = "instructions",
			name = "Instructions",
			description = "Instructions",
			position = 1,
			section = "instructionsSect"
	)
	default String instructions()
	{
		return "Better instructions are in the readme (right click plugin -> support)\n" +
				"Open LiveSplit. Right click it and start TCP Server. Open the RuneLite sidebar and press connect. " +
				"To add automatic splits, first make sure you have splits showing on LiveSplit. " +
				"Get the Watchdog plugin from the plugin hub. Create any trigger you want, " +
				"and give it a PluginMessage trigger with the namespace \"Autosplitter\" and name \"split\"";

	}

}
