# Lockout
Lockout Bingo with over 200 goals, inspired by Smallant's Lockout Bingo mod.

![image](https://github.com/marin774/lockout-fabric/assets/87690741/fd1a3617-f6a0-499b-90c3-247dc4b4282e)

## Client side installation
Make a  1.20.1 instance, add [Lockout](https://github.com/marin774/lockout-fabric/releases) and [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) to your mods folder.

I also recommend you install QoL mods:
- [Sodium](https://modrinth.com/mod/sodium/versions)
- [Lithium](https://modrinth.com/mod/lithium/versions)
- [Logical Zoom](https://www.curseforge.com/minecraft/mc-mods/logical-zoom/files)
- [NoFog](https://www.curseforge.com/minecraft/mc-mods/nofog/files) - download the Fabric version
- [Boosted Brightness](https://modrinth.com/mod/boosted-brightness/versions) - has clashes with the default `Open Board` hotkey (you can change this in your options)

## Server side installation
> Note: You can host the server from your own computer using NGROK, you don't need to buy an overpriced server. Create a new singeplayer world, open to LAN, and share the NGROK IP to your friends.

If you do decide to run this on a dedicated server, make sure to install Fabric on it.
Add [Lockout](https://github.com/marin774/lockout-fabric/releases) and [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) to server's mods folder.
You can also install Sodium and Lithium listed above.

After world generation, Lockout will search for biomes and structures, and the server (or the singleplayer world) will not be joinable for about 20 seconds.

# Commands
Create your teams using the vanilla `/team` command (`/team add <team name>`, `/team join <team name> <player name>`, `/team modify <team name> color <color>`).

To start a lockout match, run:
`/lockout teams <team name> <team name> ...` (there can be up to 16 teams)

Or if you want to do FFA:
`/lockout players <player name> <player name> ...` (there can be up to 16 players)

If you want to chat with your team, run `/chat team`.

# Board Builder

You can build and play custom boards in-game.
There's some extra goals that aren't part of "random goal pool" which you can use in Board Builder.
Boards are saved locally (client-side), in `.minecraft\lockout-boards`

To open the Board Builder, run:
`/BoardBuilder`

To set a custom board (which will be applied when you start a /lockout or /blackout game):
`/SetCustomBoard <custom board name>`

> Custom Boards are set on servers and are kept temporarily, so you will need to use this command on the server you will play on.

To unset/remove a custom board from the server, run:
`/RemoveCustomBoard`

# Vanilla modifications:
- Piglin barter rates are same as in version 1.16.1 (more pearls, string etc.)
- Raids replicate those on Medium difficulty (guarantees all Illagers)
- Zombies always convert Villagers into Zombie Villagers