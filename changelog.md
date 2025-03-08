## Lockout v0.9.3
- Fixed spectators getting advancements causing a crash

## Lockout v0.9.2
- Added `/SetBoardSize <board size>` command. This replaces `lockoutBoardSize` gamerule.
- Added `/BoardSide <left/right>` command.
- Added a config file in `.minecraft/config/lockout.json`.
    - 'default board size' - changes the board size when the server (or singleplayer world) starts.
        - default: 5
    - 'board position' - changes the position of the board (left/right)
        - default: "right"
    - 'show NoiseRouter line' - shows the long NoiseRouter line on debug hud (F3)
        - default: false
- Biome/Structure search radius has been reduced to 750 blocks (in each direction from world spawn; 1500x1500 square).
- Board is rendered above visual effects such as vignette, nausea etc.
- Fixed 'Decorate Shield with Banner'.
- Added height map debug line to debug hud (F3) - O value is often used for knowing terrain height when underground.
- Added goals:
    - 'Obtain 64 Arrows'
- Removed goals from random goal pool:
    - 'Fill Bundle with 16 empty Bundles' - overcooked

## Lockout v0.9.1
- Fixed #22 (zombie-type mobs killing anything in the nether caused a crash)

## Lockout v0.9.0
- The mod is now on version 1.21.3.
- There is a new gamerule (`lockoutBoardSize`) that allows you to play boards of different sizes. You can play boards as small as 3x3, and as large as 7x7. This change also applies to Board Builder. (#10 by @bbb651)
- Fixed most crashes and disconnect bugs.
- Biome/Structure search radius has been reduced to 1000 blocks.
- Status effects are now shown next to the board.
- Board Builder can be opened with existing boards. (`/BoardBuilder <board name>`)
- Updated some icons to better reflect their goals.
- Added 7 goals:
    - Shoot Firework from Crossbow
    - Mine Crafter
    - Light Candle
    - Wear Full Enchanted Armor
    - Put Wolf Armor on Wolf
    - Kill Breeze using Wind Charge
    - Fill Bundle with 16 empty Bundles
- Changed some goals:
    - Obtain End Crystal -> Place End Crystal
    - 'Opponent dies 3 times' is now shared in the team
    - 'Wear a carved pumpkin for 5 minutes' - progress doesn't reset after you stop wearing a pumpkin, it's saved and shown on the board
- Removed some goals:
    - Kill Ender Dragon (there is already 'Obtain Dragon Egg' goal which is more straightforward)
- Some goals are no longer part of the random goal pool, but they can still be played on custom boards:
    - End city related goals - too much rng, cities can spawn thousands of blocks away
    - Opponent obtains seeds/obsidian - no way to force these if it's left as a tie breaker
---
## Lockout v0.8.1
- Literally nothing, I released v0.8.0 again instead of v0.8.1
    - Should have implemented a fix for #6 (rejoining the server wouldn't work)

## Lockout v0.8
- Added 3 new goals:
    - 'Drink water bottle'
    - 'Use brush on suspicious sand/gravel'
    - 'Die to Polar Bear'
- Some 1v1 team goals can appear on random boards now
- Spectator advancements (e.g. entering nether/fort/bastion) will no longer display in chat
---
## Lockout v0.7:
- Added 3 new goals:
    - 'Kill bat',
    - 'Fill chiseled bookshelf'
    - 'Opponent eats food'
- Timer is now server-sided; added pre-game countdown
- Added `/SetStartTime <seconds>` command (in seconds, between 5-300)
- Changed a few goal icons
- Fixed a bug with Piglin bartering
---
## Lockout v0.6:
- Added ability to create and play custom boards (`/BoardBuilder`, `/SetCustomBoard <board name>`)
- Added spectators (every player who isn't a part of a game is a spectator)
- Villagers now always convert to Zombie Villagers if they are killed by Zombies
---
## Lockout v0.5:
- Reverted to version 1.20.1
- Team chat now works outside the game as well.
---
## Lockout v0.4:
- Added support for single-player and LAN worlds.
- Added Blackout mode (`/blackout players <player name> <player name>...` or `/blackout team <team name>`)
---
## Lockout v0.3:
- Added `/GiveGoal <player> <goal number>` command
- Added 4 new goals
- Fixed some bugs
---
## Lockout v0.2:
- Fixed many bugs, added compasses
---
## Lockout v0.1
- Initial release