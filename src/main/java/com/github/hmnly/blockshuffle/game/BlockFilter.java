package com.github.hmnly.blockshuffle.game;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

public class BlockFilter {
    public static Set<Block> getValidBlocks()
    {
        return Set.of(
                Blocks.GRASS_BLOCK,
                Blocks.DIRT,
                Blocks.STONE,
                Blocks.OAK_LOG,
                Blocks.OAK_WOOD,
                Blocks.STRIPPED_OAK_LOG,
                Blocks.STRIPPED_OAK_WOOD,
                Blocks.OAK_LEAVES,
                Blocks.OAK_PLANKS,
                Blocks.OAK_STAIRS,
                Blocks.CRAFTING_TABLE,
                Blocks.CHEST,
                Blocks.FURNACE,
                Blocks.COMPOSTER,
                Blocks.SMOKER,
                Blocks.SAND,
                Blocks.GRAVEL,
                Blocks.OBSIDIAN,
                Blocks.HAY_BLOCK,
                Blocks.COARSE_DIRT,
                Blocks.CLAY,
                Blocks.BRICKS,
                Blocks.BIRCH_LOG,
                Blocks.BIRCH_WOOD,
                Blocks.STRIPPED_BIRCH_LOG,
                Blocks.STRIPPED_BIRCH_WOOD,
                Blocks.BIRCH_PLANKS,
                Blocks.BIRCH_STAIRS
        );
    }
}
