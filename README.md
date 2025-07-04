# Generic Autosplitter

Be sure to **have a splits file open**, or when the program tries to split it will stop the timer.

Using this plugin requires the LiveSplit program.

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
Make sure you have `Compare Against > Game Time` selected on LiveSplit, or the timer will be slightly out of sync.

Download the Watchdog plugin from the plugin hub to modify what events the plugin will split on.

### Format:
In Watchdog, create a Notification Event with the body `autosplitter:split`. When that event fires, LiveSplit will split.

---

Want to use this plugin but need help? Ask me on Discord `Muffyn`. Or support me at ko-fi.com/muffyn_