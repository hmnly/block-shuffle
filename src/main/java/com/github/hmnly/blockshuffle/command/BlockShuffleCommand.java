package com.github.hmnly.blockshuffle.command;

import com.github.hmnly.blockshuffle.game.Game;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class BlockShuffleCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
            dispatcher.register(Commands.literal("blockshuffle")
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    Game.INSTANCE.start(source.getServer(), 6000);
                    source.sendSuccess(() -> Component.literal(
                            "Block Shuffle started! Time limit: 5 minutes."), true);
                    return 1;
                })
                .then(Commands.argument("timeMinutes", IntegerArgumentType.integer(1))
                    .executes(context -> {
                        CommandSourceStack source = context.getSource();
                        int minutes = IntegerArgumentType.getInteger(context, "timeMinutes");
                        int ticks = minutes * 1200;

                        Game.INSTANCE.start(source.getServer(), ticks);
                        source.sendSuccess(() -> Component.literal(
                                String.format(
                                        "Block Shuffle started! Time limit: %s minutes.", minutes)), true);
                        return 1;
                    })
                )
            );
        });
    }
}