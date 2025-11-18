package me.bubner.pingoffsetminer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import java.util.HashMap;

public class MiningSpeedCalculator {
    public final static HashMap<String, Integer> blockHardnesses = new HashMap<String, Integer>() {{
        // https://hypixel-skyblock.fandom.com/wiki/Block_Strength (2025-04-24)
        // For all intents and purposes, we only care about the blocks most commonly mined and have relevant strength
        
        // Dwarven ores, we just use the vanilla mapping since they are fundamentally the same
        String[] ores = {
                "minecraft:coal_block",
                "minecraft:iron_block",
                "minecraft:gold_block",
                "minecraft:lapis_block",
                "minecraft:redstone_block",
                "minecraft:emerald_block",
                "minecraft:diamond_block",
                "minecraft:quartz_block",
        };
        for (String ore : ores)
            put(ore, 600);
        // Dwarven metals
        put("skyblock:gray_mithril", 500);
        put("skyblock:green_mithril", 800);
        put("skyblock:blue_mithril", 1500);
        put("skyblock:titanium", 2000);
        // Gemstones
        put("skyblock:ruby_gemstone", 2300);
        put("skyblock:amber_gemstone", 3000);
        put("skyblock:sapphire_gemstone", 3000);
        put("skyblock:jade_gemstone", 3000);
        put("skyblock:amethyst_gemstone", 3000);
        put("skyblock:opal_gemstone", 3000);
        put("skyblock:topaz_gemstone", 3800);
        put("skyblock:jasper_gemstone", 4800);
        put("skyblock:onyx_gemstone", 5200);
        put("skyblock:aquamarine_gemstone", 5200);
        put("skyblock:citrine_gemstone", 5200);
        put("skyblock:peridot_gemstone", 5200);
        put("skyblock:tungsten", 5600);
        put("skyblock:umber", 5600);
        put("skyblock:glacite", 6000);
    }};

    public static String getBlockName(Block block, BlockPos eventPos) {
        try {
            int meta = block.getMetaFromState(Minecraft.getMinecraft().theWorld.getBlockState(eventPos));
            if (block == Blocks.wool && meta == 7 || (block == Blocks.stained_hardened_clay && meta == 9))
                return "skyblock:gray_mithril";
            if (block == Blocks.prismarine && (meta == 0 || meta == 1 || meta == 2))
                return "skyblock:green_mithril";
            if (block == Blocks.wool && meta == 3)
                return "skyblock:blue_mithril";
            if (block == Blocks.stone && meta == 4)
                return "skyblock:titanium";
            if (block == Blocks.stained_glass || block == Blocks.stained_glass_pane)
                switch (meta) {
                    case 14:
                        return "skyblock:ruby_gemstone";
                    case 1:
                        return "skyblock:amber_gemstone";
                    case 3:
                        return "skyblock:sapphire_gemstone";
                    case 5:
                        return "skyblock:jade_gemstone";
                    case 10:
                        return "skyblock:amethyst_gemstone";
                    case 0:
                        return "skyblock:opal_gemstone";
                    case 4:
                        return "skyblock:topaz_gemstone";
                    case 2:
                        return "skyblock:jasper_gemstone";
                    case 15:
                        return "skyblock:onyx_gemstone";
                    case 11:
                        return "skyblock:aquamarine_gemstone";
                    case 12:
                        return "skyblock:citrine_gemstone";
                    case 13:
                        return "skyblock:peridot_gemstone";
                }
            if (block == Blocks.clay || (block == Blocks.monster_egg && meta == 1))
                return "skyblock:tungsten";
            if ((block == Blocks.stained_hardened_clay && meta == 12) || block == Blocks.hardened_clay || block == Blocks.double_stone_slab2 || block == Blocks.red_sandstone)
                return "skyblock:umber";
            if (block == Blocks.packed_ice)
                return "skyblock:glacite";
        } catch (IllegalArgumentException e) {
            return block.getRegistryName();
        }
        return block.getRegistryName();
    }

    public static double getTicksToBreak(int blockHardness, double miningSpeed) {
        if (blockHardness == -1 || miningSpeed == -1)
            return -1;
        return Math.round(blockHardness * 30 / miningSpeed);
    }
}
