package me.bubner.pingoffsetminer;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * PingOffsetMiner
 *
 * @author Lucas Bubner, 2024
 */
@Mod(modid = PingOffsetMiner.MODID, version = PingOffsetMiner.VERSION)
public class PingOffsetMiner {
    public static final String MODID = "PingOffsetMiner";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new Settings());
        MinecraftForge.EVENT_BUS.register(new BlockTiming());
    }
}
