package me.bubner.pingoffsetminer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import java.util.HashMap;

public class MiningSpeedCalculator {
    public final static HashMap<String, Integer> blockHardnesses = new HashMap<String, Integer>() {{
        put("skyblock:gray_mithril", 500);
        put("skyblock:green_mithril", 800);
        put("skyblock:blue_mithril", 1500);
        put("skyblock:titanium", 2000);
    }};

    public static String getBlockName(Block block, BlockPos eventPos) {
        try {
            int meta = block.getMetaFromState(Minecraft.getMinecraft().theWorld.getBlockState(eventPos));
            if (block == Blocks.wool && meta == 7 || (block == Blocks.stained_hardened_clay && meta == 9)) {
                return "skyblock:gray_mithril";
            }
            if (block == Blocks.prismarine && (meta == 0 || meta == 1 || meta == 2)) {
                return "skyblock:green_mithril";
            }
            if (block == Blocks.wool && meta == 3) {
                return "skyblock:blue_mithril";
            }
            if (block == Blocks.stone && meta == 4) {
                return "skyblock:titanium";
            }
        } catch (IllegalArgumentException e) {
            return block.getRegistryName();
        }
        return block.getRegistryName();
    }

    public static double getSecondsToBreak(int blockHardness, double miningSpeed) {
        if (blockHardness == -1 || miningSpeed == -1) {
            return -1;
        }
        return Math.round(blockHardness * 30 / miningSpeed) / 20.0;
    }
}
