package com.github.hmnly.blockshuffle.round;

import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerSession {
    public final UUID uuid;
    private Block targetBlock;
    private final Set<Block> blockHistory;
    private boolean hasFoundBlock = false;

    public PlayerSession(UUID uuid) {
        this.uuid = uuid;
        blockHistory = new HashSet<>();
    }

    public void setTargetBlock(Block block) {
        targetBlock = block;
        blockHistory.add(block);
    }

    public Block getTargetBlock() {
        return targetBlock;
    }

    public boolean hasSeenBlock(Block block) {
        return blockHistory.contains(block);
    }

    public boolean hasFoundBlock() {
        return this.hasFoundBlock;
    }

    public void foundBlock() {
        this.hasFoundBlock = true;
    }

    public void resetHasFoundBlock() {
        this.hasFoundBlock = false;
    }
}
