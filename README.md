![mod icon](/src/main/resources/assets/blockshuffle/icon.png)

# Block Shuffle

Block Shuffle is a Fabric mod, inspired by an old(-ish) [Dream](https://www.youtube.com/@dream) video called [Minecraft Block Shuffle....](https://www.youtube.com/watch?v=p34C7fNFgTA). The original was implemented as a Tuinity/Paper plugin for Minecraft 1.15.2

This reimplementation is designed to run on the latest Minecraft versions using Fabric

Core features remain unchanged, although this mod is designed to handle more than 2 players

## Known issues

- ~~Match data persists across singleplayer worlds~~
  - ~~You can start a game in one world, exit the world, then create another world, and the game will keep going from where you left off~~
- Match data is lost between server restarts
- New round announcement is broken
  - The new round is announced after players are assigned their blocks
  - The first round is announced as "Everyone found their block"

## Roadmap

### Currently working on

- Expanding the pool of usable blocks
- Implementing simple UI using standard Minecraft features
- Adding localization

### Planned

- Adding support for custom game modes
- Adding official game modes
- Community translation project

### Considering

- Reworking the usable block pool to generate dynamically
  - Generating the block pool out of blocks that appear less than 1000 blocks away from spawn
    > This heavily reduces the possibility of losing only because of bad luck (e.g., you shouldn't get a Jungle Log if the nearest Jungle is 5000 blocks from spawn)
  - Introducing Nether- and End-exclusive blocks later in the game
- Introducing per-player block re-rolls
  - Intended as a fallback when the dynamic pool still assigns an unreachable block
  - The game is paused and a vote is started
  - All players, except the re-roll target, vote on whether the target's block should be re-rolled
- Creating an API to allow users to create their own game modes
- Uploading to Modrinth/CurseForge

## Installation

Requirements:
- Minecraft 26.3
  - Other version support is untested
- Fabric Loader 0.19.5 or higher
- Fabric API 0.162.0 or higher

Grab the [latest release](https://github.com/hmnly/block-shuffle/releases/latest), put it into the `mods` folder of your Minecraft instance and run the game

## Usage

Run `/blockshuffle [timeMinutes]`.

Optional parameters:
- `[timeMinutes]` - defines custom round time limit in minutes. Default: 5 minutes

Does not require Operator permissions (this may change)

## Game modes

### Normal

Standard Block Shuffle mode. Each player is assigned a random block that they have to find and stand on top of within the round's time limit. Failure to find their block results in elimination from the game. A normal game can end in three ways:

- Win
  - Only one player is left standing after a round. Includes solo games
- Draw
  - No one could find their block
- Tie
  - There are not enough blocks in the pool to start the next round

## Building from source

Block Shuffle uses the Gradle build tool and can be built with the `gradle build` command. The build artifacts (production binaries and their source bundles) can be found in the `build/libs` directory

The Gradle wrapper is provided for ease of use and will automatically download and install the appropriate version of Gradle for the project build. To use the Gradle wrapper, substitute `gradle` in build commands with `gradlew.bat` (Windows) or `./gradlew` (macOS and Linux)

### Build Requirements

- OpenJDK 25
  - Eclipse Temurin distribution is recommended, as it is known to be of high quality and was the one used during development
- Gradle 9.8.0
  - Typically, newer versions of Gradle will work without issues, but the build script is only tested against the version used by the [wrapper script](/gradle/wrapper/gradle-wrapper.properties)

## License

Currently, this project is licensed under The MIT License. Read [LICENSE](LICENSE.txt) for further details.

In the future, license is to be changed.