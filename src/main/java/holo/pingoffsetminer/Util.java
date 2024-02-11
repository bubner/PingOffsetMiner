package holo.pingoffsetminer;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

import static org.lwjgl.opengl.GL11.*;

public class Util {
    private static Configuration config;

    /**
     * Read the config file.
     */
    public static Configuration getConfig() {
        if (config == null) {
            File configFile = new File(Loader.instance().getConfigDir(), "POM.cfg");
            config = new Configuration(configFile);
            config.load();
        }
        return config;
    }

    public static void saveConfig() {
        if (config != null) {
            config.save();
        }
    }


    /**
     * Check if the user is in SkyBlock by analysing the scoreboard.
     */
    public static boolean isInSkyblock() {
        try {
            return Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1) != null && Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1).getDisplayName().contains("SKYBLOCK");
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static void drawBox() {
        glBegin(GL_LINES);
        glVertex3d(0, 0, 0);
        glVertex3d(1, 0, 0);

        glVertex3d(1, 0, 0);
        glVertex3d(1, 1, 0);

        glVertex3d(1, 1, 0);
        glVertex3d(0, 1, 0);

        glVertex3d(0, 1, 0);
        glVertex3d(0, 0, 0);

        glVertex3d(0, 0, 1);
        glVertex3d(1, 0, 1);

        glVertex3d(1, 0, 1);
        glVertex3d(1, 1, 1);

        glVertex3d(1, 1, 1);
        glVertex3d(0, 1, 1);

        glVertex3d(0, 1, 1);
        glVertex3d(0, 0, 1);

        glVertex3d(0, 0, 0);
        glVertex3d(0, 0, 1);

        glVertex3d(1, 0, 0);
        glVertex3d(1, 0, 1);

        glVertex3d(1, 1, 0);
        glVertex3d(1, 1, 1);

        glVertex3d(0, 1, 0);
        glVertex3d(0, 1, 1);
        glEnd();
    }
}
