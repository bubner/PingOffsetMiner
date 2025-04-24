package me.bubner.pingoffsetminer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static org.lwjgl.opengl.GL11.*;

public class BlockTiming {
    private BlockPos currentBlock;
    private double ticksNeeded;
    private boolean timeoutExceeded;
    private int startServerTick;

    @SubscribeEvent
    public void renderBlockOverlay(DrawBlockHighlightEvent e) {
        if (!Util.getConfig().get("settings", "active", true).getBoolean() || !Util.isInSkyblock()) {
            ticksNeeded = -1;
            return;
        }
        BlockPos blockPos = e.target.getBlockPos();
        Minecraft mc = Minecraft.getMinecraft();
        if (blockPos == null)
            return;
        if (!blockPos.equals(currentBlock) || !mc.gameSettings.keyBindAttack.isKeyDown()) {
            timeoutExceeded = false;
            currentBlock = blockPos;
            startServerTick = mc.thePlayer.ticksExisted;
        }
        if (currentBlock == null)
            return;
        String blockName = MiningSpeedCalculator.getBlockName(mc.theWorld.getBlockState(currentBlock).getBlock(), currentBlock);
        double ticks = MiningSpeedCalculator.getTicksToBreak(
                MiningSpeedCalculator.blockHardnesses.getOrDefault(blockName, -1),
                Util.getConfig().get("settings", "miningSpeed", -1.0).getDouble()
        );
        if (ticks == -1)
            return;
        ticksNeeded = ticks;
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(4);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        RenderManager rm = mc.getRenderManager();
        glTranslated(-rm.viewerPosX, -rm.viewerPosY, -rm.viewerPosZ);
        glTranslated(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        glColor4f(timeoutExceeded ? 0f : 1f, timeoutExceeded ? 1f : 0f, 0f, 1f);
        Util.drawBox();
        glColor4f(1f, 1f, 1f, 1f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        int ticksElapsed = Minecraft.getMinecraft().thePlayer.ticksExisted - startServerTick;
        // TODO: Dynamic ping calculation?
        double pingSec = Util.getConfig().get("settings", "ping", -1.0).getDouble() / 1000.0;
        double pingOffset = pingSec > 0 && ticksNeeded > 0
                ? ticksNeeded - pingSec * 20.0
                : ticksNeeded;
        timeoutExceeded = ticksNeeded > 0 && ticksElapsed >= pingOffset;
    }
}
