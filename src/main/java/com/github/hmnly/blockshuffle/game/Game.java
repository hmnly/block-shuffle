package com.github.hmnly.blockshuffle.game;

import com.github.hmnly.blockshuffle.round.PlayerSession;
import com.github.hmnly.blockshuffle.round.Round;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Game {
    public static final Game INSTANCE = new Game();

    private Set<Block> availableBlocks;
    private Set<PlayerSession> sessions;
    private boolean isActive;
    private Round currentRound;
    private ServerPlayer gameWinner;
    private int roundTime;

    public Game() {
        isActive = false;
    }

    public void start(MinecraftServer server, int roundTime) {
        availableBlocks = BlockFilter.getValidBlocks();
        sessions = new HashSet<>();
        isActive = true;
        currentRound = null;
        gameWinner = null;
        this.roundTime = roundTime;

        Set<UUID> onlinePlayers = server.getPlayerList().getPlayersByUUID().keySet();
        for (UUID uuid : onlinePlayers) {
            sessions.add(new PlayerSession(uuid));
        }
    }

    public void end(MinecraftServer server) {
        Component message;
        // draw
        if (gameWinner == null) {
            message = Component.literal("No one could find their block. It is a draw.");
        }
        // win
        else {
            Component styledName = gameWinner.getName().copy().withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            message = Component.empty()
                    .append(styledName)
                    .append(Component.literal(" wins!"));
        }
        server.getPlayerList().broadcastSystemMessage(message, false);
    }

    public void tick(MinecraftServer server) {
        if (!isActive) return;
        if (currentRound == null || !currentRound.isOngoing()) {
            currentRound = new Round(availableBlocks, sessions, roundTime, server);
        }

        currentRound.tick(server);

        if (!currentRound.isOngoing()) {
            Set<PlayerSession> roundLosers = currentRound.getRoundLosers();
            sessions.removeAll(roundLosers);

            switch (sessions.size()) {
                case 1:
                    UUID winnerUUID = sessions.iterator().next().uuid;
                    gameWinner = server.getPlayerList().getPlayer(winnerUUID);
                    isActive = false;
                    break;
                case 0:
                    isActive = false;
                    break;
            }
        }

        if (!isActive) end(server);
    }
}
