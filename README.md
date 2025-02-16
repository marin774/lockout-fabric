# Lockout
Lockout Bingo with over 200 goals, inspired by Smallant's Lockout Bingo mod.

![board_example](https://github.com/user-attachments/assets/0f16659d-9c85-46e2-8821-02e4c6f8710b)

## Client side installation
Make a 1.21.3 instance, add [Lockout](https://github.com/marin774/lockout-fabric/releases) and [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) to your mods folder.

I also recommend you install some QoL mods:
- [Sodium](https://modrinth.com/mod/sodium/versions)
- [Lithium](https://modrinth.com/mod/lithium/versions)
- [Logical Zoom](https://www.curseforge.com/minecraft/mc-mods/logical-zoom/files)
- [Simple Fog Control](https://modrinth.com/mod/simplefog/versions)
- [Gamma Utils](https://modrinth.com/mod/gamma-utils/versions)

## Server side installation
> Note: You can host the server from your own computer by opening a world to LAN and using TCP tunneling services or mods such as [e4mc](https://modrinth.com/mod/e4mc) or [Essential mod](https://modrinth.com/mod/essential).

If you decide to run Lockout on a dedicated server, make sure to install Fabric.
Add [Lockout](https://github.com/marin774/lockout-fabric/releases) and [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) to server's mods folder.
You can also install Sodium and Lithium listed above.

After world generation, Lockout will search for biomes and structures, and the server (or the singleplayer world) will not be joinable for about 20 seconds.

# Commands
Create teams using the vanilla `/team` command:
- `/team add <team name>` - Create a team
- `/team join <team name> <player name>` - Add a player to a team
- `/team modify <team name> color <color>` - Change team's color (not required)

Chat with your team:
- `/chat team`

Change the board size:
- `/gamerule lockoutBoardSize <size>` - Between 3 (3x3) and 7 (7x7), default is 5 (5x5)

Change the match start time:
- `/SetStartTime <seconds>` (between 5-300s)

Start a Lockout match:
- `/lockout teams <team name> <team name> ...` (there can be up to 16 teams)
- `/lockout players <player name> <player name> ...` - FFA, 1 player teams

Start a Blackout match:
- `/blackout team <team name>`
- `/blackout players <player name> <player name> ...`

# Board Builder

You can create and play custom boards in-game.
There's some extra goals (that aren't part of the "random goal pool") that you can find in Board Builder.
Boards are saved locally (client-side), in `.../.minecraft/lockout-boards`

Open the Board Builder:
- `/BoardBuilder`
- or press the `Open Board` hotkey (before any match starts)

Set a custom board:
- `/SetCustomBoard <custom board name>` - this board will be used for the next match, but server restarts will unset this.

Unset a custom board:
- `/RemoveCustomBoard`

![image](https://github.com/user-attachments/assets/db80832e-41a2-4ea1-a7ac-0754b3c93b5a)

# Vanilla modifications:
- Piglin barter rates are same as in version 1.16.1 (more pearls, string etc.)
- Raids replicate those on Medium difficulty (guarantees all Illagers)
- Zombies always convert Villagers into Zombie Villagers
