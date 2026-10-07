package com.github.hmnly.blockshuffle;

import com.github.hmnly.blockshuffle.command.BlockShuffleCommand;
import com.github.hmnly.blockshuffle.game.Game;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class BlockShuffle implements ModInitializer {

    @Override
    public void onInitialize() {
        BlockShuffleCommand.register();
        ServerTickEvents.END_SERVER_TICK.register(Game.INSTANCE::tick);
    }
}
