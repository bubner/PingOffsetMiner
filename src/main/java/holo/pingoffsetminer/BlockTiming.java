package holo.pingoffsetminer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import static org.lwjgl.opengl.GL11.*;

public class BlockTiming {
    private final ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    private Block currentBlock;

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            try {
                // TODO: check if block state is updated on a server
                currentBlock = event.world.getBlockState(event.pos).getBlock();
            } catch (NullPointerException e) {
                LogManager.getLogger(PingOffsetMiner.MODID).log(Level.DEBUG, "Block is null.");
            }
            timer.reset();
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!Util.getConfig().get("settings", "active", true).getBoolean())
            return;

        MovingObjectPosition rt = Minecraft.getMinecraft().objectMouseOver;
        boolean isMouseHeld = Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown();

        if (!isMouseHeld)
            currentBlock = null;

        if (rt != null && currentBlock != null && rt.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            double time = MiningSpeedCalculator.getSecondsToBreak(
                    MiningSpeedCalculator.blockHardnesses.getOrDefault(
                            MiningSpeedCalculator.getBlockName(currentBlock, Minecraft.getMinecraft().theWorld, rt.getBlockPos()),
                            -1
                    ),
                    Util.getConfig().get("settings", "miningSpeed", -1.0).getDouble()
            );

            if (time == -1) {
                return;
            }

            double ping = Util.getConfig().get("settings", "ping", -1.0).getDouble();
            if (ping != -1) {
                // TODO: check this
                int ticks = (int) Math.ceil((ping / 1000.0) * 20);
                time -= ticks / 20.0;
            }

            glPushMatrix();
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_LINE_SMOOTH);

            glLineWidth(3);

            glDisable(GL_TEXTURE_2D);
            glEnable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);

            double renderPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
            double renderPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
            double renderPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

            glTranslated(-renderPosX, -renderPosY, -renderPosZ);
            BlockPos blockPos = rt.getBlockPos();
            glTranslated(blockPos.getX(), blockPos.getY(), blockPos.getZ());

            if (timer.seconds() >= time) {
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
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        currentBlock = null;
    }
}
