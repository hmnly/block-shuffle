package com.github.hmnly.blockshuffle.round;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Round {
    private final List<Block> availableBlocks;
    private final Set<Block> usedBlocks;
    private final Set<PlayerSession> initialSessions;
    private final Set<PlayerSession> roundLosers;
    private boolean isOngoing;
    private int timeLeftTicks;

    public Round(Set<Block> availableBlocks, Set<PlayerSession> sessions, int roundTime, MinecraftServer server) {
        this.availableBlocks = new ArrayList<>(availableBlocks);
        this.usedBlocks = new HashSet<>();
        this.initialSessions = sessions;
        this.roundLosers = new HashSet<>(sessions);
        this.isOngoing = true;
        this.timeLeftTicks = roundTime;

        assignBlocks(server);
    }

    private void assignBlocks(MinecraftServer server) {
        for (PlayerSession session : initialSessions) {
            session.resetHasFoundBlock();

            // TODO: declare draw when block pool is exhausted
            Block block;
            do {
                int randomIndex = ThreadLocalRandom.current().nextInt(availableBlocks.size());
                block = this.availableBlocks.get(randomIndex);
            } while (session.hasSeenBlock(block) || usedBlocks.contains(block));

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
        if (!isOngoing) return;
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

                // TODO: fix short block detection
                BlockPos pos = player.blockPosition().below();
                BlockState state = player.level().getBlockState(pos);

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
            if (initialSessions.size() > 1) {
                Component message = Component.literal("Everyone found their block! Starting next round...");
                server.getPlayerList().broadcastSystemMessage(message, false);
            }
            endRound();
            return;
        }

        timeLeftTicks--;
        announceTime(timeLeftTicks, server);

        if (timeLeftTicks == 0) {
    //            for (PlayerSession loser : roundLosers) {
    //                ServerPlayer player = server.getPlayerList().getPlayer(loser.uuid);
    //                if (player != null) player.setGameMode(GameType.SPECTATOR);
    //            }
            endRound();
        }
    }

    private void endRound() {
        isOngoing = false;
    }

    private static void announceTime(int ticks, MinecraftServer server) {
        Component message;
        if (ticks > 1200 && ticks % 1200 == 0) {
            message = Component.literal(String.format("%d minutes left!", ticks / 1200));
        }
        else if (ticks == 1200) {
            message = Component.literal("1 minute left!");
        }
        else if (ticks == 600) {
            message = Component.literal("30 seconds left!");
        }
        else if (ticks == 200) {
            message = Component.literal("10 seconds left!");
        }
        else if (ticks <= 100 && ticks > 0 && ticks % 20 == 0) {
            message = Component.literal(String.format("%d", ticks / 20));
        }
        else return;
        server.getPlayerList().broadcastSystemMessage(message, false);
    }

    public boolean isOngoing() {
        return isOngoing;
    }

    public Set<PlayerSession> getRoundLosers() {
        return roundLosers;
    }
}
