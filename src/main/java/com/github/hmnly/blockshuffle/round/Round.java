package com.github.hmnly.blockshuffle.round;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Round {
    private final Set<Block> availableBlocks;
    private final Set<Block> usedBlocks;
    private final Set<PlayerSession> initialSessions;
    private final Set<PlayerSession> roundLosers;
    private boolean isFinished;
    private int timeLeftTicks;

    public Round(Set<Block> availableBlocks, Set<PlayerSession> sessions, int roundTime, MinecraftServer server) {
        this.availableBlocks = availableBlocks;
        this.usedBlocks = new HashSet<>();
        this.initialSessions = sessions;
        this.roundLosers = new HashSet<>(sessions);
        this.isFinished = false;
        this.timeLeftTicks = roundTime;

        assignBlocks(server);
    }

    private void assignBlocks(MinecraftServer server) {
        for (PlayerSession session : initialSessions) {
            session.resetHasFoundBlock();

            List<Block> blockCandidates = new ArrayList<>(availableBlocks);

            blockCandidates.removeIf(session::hasSeenBlock);
            blockCandidates.removeIf(usedBlocks::contains);

            // canStartRound ensures it's not empty but Just In Case™
            if (blockCandidates.isEmpty()) {
                throw new IllegalStateException();
            }

            int randomIndex = ThreadLocalRandom.current().nextInt(blockCandidates.size());
            Block block = blockCandidates.get(randomIndex);

            session.setTargetBlock(block);
            usedBlocks.add(block);

            notifyPlayer(session, block, server);
        }
    }

    private void notifyPlayer(PlayerSession session, Block block, MinecraftServer server) {
        ServerPlayer player = server.getPlayerList().getPlayer(session.uuid);
        if (player != null) {
            Component blockName = block.getName().copy().withStyle(ChatFormatting.DARK_GREEN);
            Component message = Component.literal("Your target block is: ").append(blockName);
            player.sendSystemMessage(message);
        }
    }

    public void tick(MinecraftServer server) {
        if (isFinished) return;
        if (timeLeftTicks % 10 == 0) {
            for (PlayerSession session : initialSessions) {
                if (session.hasFoundBlock()) continue;

                ServerPlayer player = server.getPlayerList().getPlayer(session.uuid);
                if (player == null) {
                    continue;
                }

                if (!player.onGround()) {
                    continue;
                }

                Vec3 pos = player.position();
                BlockPos blockPos = new BlockPos((int) Math.floor(pos.x), (int) Math.ceil(pos.y), (int) Math.floor(pos.z)).below();
                BlockState state = player.level().getBlockState(blockPos);

                if (state.is(session.getTargetBlock())) {
                    session.foundBlock();
                    Component message = Component.empty()
                            .append(player.getName())
                            .append(" has found their block!");
                    server.getPlayerList().broadcastSystemMessage(message, false);
                    roundLosers.remove(session);
                }

            }
        }

        if (roundLosers.isEmpty()) {
            endRound();
            return;
        }

        timeLeftTicks--;
        announceTime(timeLeftTicks, server);

        if (timeLeftTicks == 0) {
            endRound();
        }
    }

    private void endRound() {
        isFinished = true;
    }

    private static void announceTime(int ticks, MinecraftServer server) {
        Component message;
        if (ticks > 1200 && ticks % 1200 == 0) {
            message = Component.literal(String.format("%d minutes left!", ticks / 1200));
        } else if (ticks == 1200) {
            message = Component.literal("1 minute remaining.");
        } else if (ticks == 600) {
            message = Component.literal("30 seconds remaining.");
        } else if (ticks == 200) {
            message = Component.literal("10 seconds remaining.");
        } else if (ticks <= 100 && ticks > 0 && ticks % 20 == 0) {
            message = Component.literal(String.format("%d seconds remaining.", ticks / 20));
        } else return;
        server.getPlayerList().broadcastSystemMessage(message, true);
    }

    public boolean isFinished() {
        return isFinished;
    }

    public Set<PlayerSession> getRoundLosers() {
        return roundLosers;
    }
}
