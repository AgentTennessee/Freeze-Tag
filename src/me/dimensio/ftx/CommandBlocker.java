package me.dimensio.ftx;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandBlocker
  implements Listener
{
  public static Config config;
  public static FreezeTag plugin;
  
  public CommandBlocker(FreezeTag instance)
  {
    plugin = instance;
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if (event.getPlayer().isOp()) {
      return;
    }
    List whitelist = config.customConfig.getStringList("CommandWhitelist");
    whitelist.add("ftx");
    
    String[] cmdArg = event.getMessage().split(" ");
    String cmdString = cmdArg[0].trim().substring(1).toLowerCase();
    try
    {
      Command command = plugin.getServer().getPluginCommand(cmdString);
      if (whitelist.contains(command.getLabel().toLowerCase())) {
        return;
      }
      if (!command.getAliases().isEmpty()) {
        for (String alias : command.getAliases()) {
          if (whitelist.contains(alias.toLowerCase())) {
            return;
          }
        }
      }
    }
    catch (NullPointerException e)
    {
      if (whitelist.contains(cmdString)) {
        return;
      }
    }
    event.setCancelled(true);
    Player receiver = event.getPlayer();
    receiver.sendMessage(ChatColor.RED + "You are not permitted to perform this command while in a FreezeTag.");
  }
}









