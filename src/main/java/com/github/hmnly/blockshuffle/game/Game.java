package com.github.hmnly.blockshuffle.game;

import com.github.hmnly.blockshuffle.round.PlayerSession;
import com.github.hmnly.blockshuffle.round.Round;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class Game {
    public static final Game INSTANCE = new Game();

    private Set<Block> availableBlocks;
    private Set<PlayerSession> sessions;
    private boolean isActive;
    private Round currentRound;
    private int roundTime;

    public Game() {
        isActive = false;
    }

    public void start(MinecraftServer server, int roundTime) {
        availableBlocks = BlockFilter.getValidBlocks();
        sessions = new HashSet<>();
        isActive = true;
        currentRound = null;
        this.roundTime = roundTime;

        Set<UUID> onlinePlayers = server.getPlayerList().getPlayersByUUID().keySet();
        for (UUID uuid : onlinePlayers) {
            sessions.add(new PlayerSession(uuid));
        }
    }

    private Component buildTieMessage(MinecraftServer server, Set<PlayerSession> survivors) {
        MutableComponent message = Component.literal("There are no blocks left to find! Game is a tie between ");
        List<Component> names = new ArrayList<>();
        for (PlayerSession s : survivors) {
            ServerPlayer player = server.getPlayerList().getPlayer(s.uuid);
            names.add((player == null ?
                    Component.literal("[Disconnected]") :
                    player.getName().copy()).withStyle(ChatFormatting.BOLD));
        }

        for (int i = 0; i < names.size(); i++) {
            if (i > 0) message.append(i == names.size() - 1 ? " and " : ", ");
            message.append(names.get(i));
        }

        return message.append(".");
    }

    private void end(MinecraftServer server, Set<PlayerSession> survivors) {
        Component message = switch (survivors.size()) {
            case 0 -> Component.literal("No one could find their block! Game is a draw.");
            case 1 -> {
                ServerPlayer player = server.getPlayerList().getPlayer(survivors.iterator().next().uuid);
                yield Component.empty()
                        .append((player == null ? Component.literal("[Disconnected]") : player.getName().copy())
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
                        .append(" wins!");
            }
            default -> buildTieMessage(server, survivors);
        };

        server.getPlayerList().broadcastSystemMessage(message, false);
        isActive = false;
    }

    private boolean canStartRound() {
        for (PlayerSession s : sessions) {
            int unseenBlockCount = availableBlocks.size() - s.getBlockHistorySize();
            if (unseenBlockCount < sessions.size()) return false;
        }
        return true;
    }

    public void tick(MinecraftServer server) {
        if (!isActive) return;
        if (currentRound == null || currentRound.isFinished()) {
            if (!canStartRound()) {
                end(server, sessions);
                return;
            }
            currentRound = new Round(availableBlocks, sessions, roundTime, server);
            Component message = Component.literal("Everyone found their block! Starting next round...");
            server.getPlayerList().broadcastSystemMessage(message, false);
        }

        currentRound.tick(server);

        if (currentRound.isFinished()) {
            Set<PlayerSession> roundLosers = currentRound.getRoundLosers();
            sessions.removeAll(roundLosers);

            if (sessions.size() <= 1) {
                end(server, sessions);
            }
        }
    }
}
