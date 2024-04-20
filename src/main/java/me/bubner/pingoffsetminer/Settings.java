package me.bubner.pingoffsetminer;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class Settings extends CommandBase {
    private static final String USAGE = "/pom <speed, ping, active> <mining speed, ping(ms), true|false>";

    @Override
    public String getCommandName() {
        return "pom";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return USAGE;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 2) {
            switch (args[0]) {
                case "speed":
                    double speed;
                    try {
                        speed = parseDouble(args[1]);
                    } catch (NumberFormatException e) {
                        throw new CommandException(USAGE);
                    }
                    Util.getConfig().get("settings", "miningSpeed", -1.0).set(speed);
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§4[§cPOM§4]§7 mining speed set to " + speed));
                    break;
                case "active":
                    if (!args[1].equals("true") && !args[1].equals("false")) {
                        throw new CommandException(USAGE);
                    }
                    Util.getConfig().get("settings", "active", true).set(Boolean.parseBoolean(args[1]));
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§4[§cPOM§4]§7 " + (Boolean.parseBoolean(args[1]) ? "active!" : "inactive!")));
                    break;
                case "ping":
                    double ping;
                    try {
                        ping = parseDouble(args[1]);
                    } catch (NumberFormatException e) {
                        throw new CommandException(USAGE);
                    }
                    Util.getConfig().get("settings", "ping", -1.0).set(ping);
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§4[§cPOM§4]§7 ping (ms) set to " + ping));
                    break;
            }
            Util.saveConfig();
        } else {
            double speed = Util.getConfig().get("settings", "miningSpeed", -1.0).getDouble();
            boolean active = Util.getConfig().get("settings", "active", true).getBoolean();
            double ping = Util.getConfig().get("settings", "ping", -1.0).getDouble();
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§4[§cPOM§4]§7 mining speed: " + (speed == -1 ? "NOT SET" : speed) + ", ping: " + (ping == -1 ? "NOT SET" : ping) + ", active: " + active + "\n" + USAGE));
        }
    }
}
