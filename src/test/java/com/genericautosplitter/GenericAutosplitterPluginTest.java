package com.genericautosplitter;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GenericAutosplitterPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GenericAutosplitterPlugin.class);
		RuneLite.main(args);
	}
}