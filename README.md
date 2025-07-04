# Generic Autosplitter

* Keeps track of time through login/logout, world hop, connection lost, and loading screens
    * Does lag mitigation, though can be inaccurate +/- 1 tick each time you lag
* Pauses timer visually when world hopping, and full pauses (goes gray) when entirely logged out
* Lets you start the timer paused on the login screen, or start it with an offset
* Lets you set up custom autosplits through watchdog

Using this plugin requires LiveSplit 

Installation and setup guide can be found here:

[LiveSplit](https://livesplit.org/downloads/)

## How to use
Download LiveSplit.

Turn the plugin on and make sure the port in the plugin settings match your LiveSplit server port.

Start LiveSplit and start the LiveSplit server (right click LS -> control -> start tcp server).

Open the sidebar and click "Connect".
If the status turns green it means you have a connection to your LiveSplit server.
If it stays red something went wrong, most likely you did not start the LiveSplit server
or you have mismatching ports in the plugin settings and the LiveSplit server settings.

If your status is green you are good to go.

## Splits
Make sure you have `Compare Against > Game Time` selected on LiveSplit.

Be sure to **have a splits file open**, or when the program tries to split it will stop the timer.

## Watchdog

Download the Watchdog plugin from the plugin hub to modify what events the plugin will split on.

[Watchdog](https://runelite.net/plugin-hub/show/watchdog)

In Watchdog, create any alert, and give it a Plugin Message (`Advanced > Plugin Messages`) notification with Namespace `Autosplitter` and Method `split`. Nothing is required in data. When that alert fires, LiveSplit will split.

For example, to create a split on level up, use a game message alert with the blanks replaced:

`Congratulations, you've just advanced your _ level. You are now level _.` 

Watchdog is very powerful, so be creative with your splits. Watchdog will not work in most PvM areas, at Jagex's request, so you would have to manually split if you wanted a split during a boss encounter (like Fight Caves).


---

Want to use this plugin but need help? Ask me on Discord `muffyn`. Or support me at ko-fi.com/muffyn_