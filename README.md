# Generic Autosplitter

Be sure to **have a splits file open**, or when the program tries to split it will stop the timer.

Using this plugin requires the LiveSplit program with the LiveSplit server component.

Installation and setup guide can be found here:

[LiveSplit](https://livesplit.org/downloads/)

[LiveSplit Server](https://github.com/LiveSplit/LiveSplit.Server)

## How to use
Download LiveSplit and the LiveSplit server component.

Turn the plugin on and make sure the port in the plugin settings match your LiveSplit server port.

Start LiveSplit and start the LiveSplit server (right click LS -> control -> start server).
Make sure to add the LS server to your layout, otherwise you won't see "start server" under control.

Open the sidebar and click "Connect".
If the status turns green it means you have a connection to your LiveSplit server.
If it stays red something went wrong, most likely you did not start the LiveSplit server
or you have mismatching ports in the plugin settings and the LiveSplit server settings.

If your status is green you are good to go.

## Splits
Make sure you have `Compare Against > Game Time` selected on LiveSplit, or the timer will be slightly out of sync.

Open the plugin config to modify what events the plugin will split on.
The first element is just for naming, and is completely ignored.

### Format:

`<name>,0,<itemID>(,<quantity>)`

Splits when an item matching the ID enters your inventory for the first time.
If a quantity is given, it will split when you first have that many of the item in your inventory.
You can find item IDs at https://www.osrsbox.com/tools/item-search/

`<name>,1,<varb>,<value>`

Splits when the varbit changes to that value for the first time.

`<name>,2,<varp>,<value>`

Splits when the varplayer changes to that value for the first time.

You can find varbs and varps using RuneLite's developer mode's Var Inspector https://github.com/runelite/runelite/wiki/Using-the-client-developer-tools

---

Want to use this plugin but need help? Ask me on Discord `Muffyn`. Or support me at ko-fi.com/muffyn_