package com.github.hmnly.blockshuffle;

import com.github.hmnly.blockshuffle.command.BlockShuffleCommand;
import com.github.hmnly.blockshuffle.game.GameManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class BlockShuffle implements ModInitializer {
    @Override
    public void onInitialize() {
        GameManager manager = new GameManager();

        BlockShuffleCommand.register(manager);
        ServerTickEvents.END_SERVER_TICK.register(manager::tick);
        ServerLifecycleEvents.SERVER_STOPPED.register(_ -> manager.reset());
    }
}
