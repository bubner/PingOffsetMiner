package me.bubner.pingoffsetminer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static org.lwjgl.opengl.GL11.*;

public class BlockTiming {
    private final ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    private BlockPos blockPos;
    private boolean timeoutExceeded;

    // TODO: Might be able to draw a box around a nearby target to mine instead?
    @SubscribeEvent
    public void renderBlockOverlay(DrawBlockHighlightEvent e) {
        if (blockPos == null)
            return;

        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);

        glLineWidth(4);

        glDisable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        RenderManager mc = Minecraft.getMinecraft().getRenderManager();
        glTranslated(-mc.viewerPosX, -mc.viewerPosY, -mc.viewerPosZ);
        glTranslated(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        if (timeoutExceeded) {
            glColor4f(0, 255, 0, 1);
        } else {
            glColor4f(255, 0, 0, 1);
        }

        Util.drawBox();
        glColor4f(1, 1, 1, 1);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);

        glPopMatrix();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!Util.getConfig().get("settings", "active", true).getBoolean())
            return;

        Minecraft mc = Minecraft.getMinecraft();
        MovingObjectPosition rt = mc.objectMouseOver;
        if (rt == null || rt.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            blockPos = null;
            return;
        }

        boolean isMouseHeld = mc.gameSettings.keyBindAttack.isKeyDown();

        if (!isMouseHeld || (blockPos != null && !rt.getBlockPos().equals(blockPos))) {
            timer.reset();
        }

        WorldClient world = mc.theWorld;
        if (world == null) return;
        Block currentBlock = world.getBlockState(rt.getBlockPos()).getBlock();
        double time = MiningSpeedCalculator.getSecondsToBreak(
                MiningSpeedCalculator.blockHardnesses.getOrDefault(
                        MiningSpeedCalculator.getBlockName(currentBlock, rt.getBlockPos()),
                        -1
                ),
                Util.getConfig().get("settings", "miningSpeed", -1.0).getDouble()
        );

        // TODO: Dynamic ping calculation?
        double ping = Util.getConfig().get("settings", "ping", -1.0).getDouble();
        double pingOffsetTime = time;
        if (ping != -1 && time != -1) {
            pingOffsetTime -= ping / 1000.0;
        }
        pingOffsetTime = Math.max(pingOffsetTime, ping / 1000.0);
        blockPos = time != -1 ? rt.getBlockPos() : null;
        timeoutExceeded = time != -1 && timer.seconds() >= pingOffsetTime;
    }
}
