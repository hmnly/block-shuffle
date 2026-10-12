package com.github.hmnly.blockshuffle.game;

import net.minecraft.server.MinecraftServer;

public class GameManager {
    private Game game;

    public void tick(MinecraftServer server) {
        if (game == null) return;
        game.tick(server);
        if (!game.isActive()) {
            reset();
        }
    }

    public void start(MinecraftServer server, int roundTime) {
        game = new Game(server, roundTime);
    }

    public void reset() {
        game = null;
    }
}
