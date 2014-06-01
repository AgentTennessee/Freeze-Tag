package me.dimensio.ftx;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class Helper
{
  private final FreezeTag plugin;
  public ChatColor GREEN = ChatColor.DARK_GREEN;
  public String PREFIX = ChatColor.DARK_GREEN + "[FreezeTagX] ";
  public Logger log = Logger.getLogger("Minecraft");
  
  public Helper(FreezeTag instance)
  {
    this.plugin = instance;
  }
  
  public void getRules(Player player)
  {
    PluginDescriptionFile pdfFile = this.plugin.getDescription();
    player.sendMessage(" ");
    player.sendMessage(this.GREEN + "FreezeTagX v" + ChatColor.WHITE + pdfFile.getVersion() + this.GREEN + " rules:");
    player.sendMessage(" ");
    player.sendMessage(this.GREEN + "Remember playing Freeze Tag as a kid? This is the same, but in Minecraft!");
    player.sendMessage(this.GREEN + "If there is a game in progress, type '/ftx join' (without quotes) to register for the game.");
    player.sendMessage(" ");
    player.sendMessage(this.GREEN + "When the game begins, you will be told who the " + ChatColor.WHITE + "CHASERS" + this.GREEN + " are.");
    player.sendMessage(" ");
    player.sendMessage(this.GREEN + "If you're a chaser, find un-frozen players and tag them to freeze them. You win by freezing every player.");
    player.sendMessage(" ");
    player.sendMessage(this.GREEN + "If you're not a chaser, find your frozen brothers and tag them to unfreeze them! You win by surviving until the game ends.");
    player.sendMessage(" ");
  }
  
  public void getPermissions(Player player)
  {
    PluginDescriptionFile pdfFile = this.plugin.getDescription();
    player.sendMessage("1:");
    player.sendMessage(this.GREEN + "FreezeTagX v" + ChatColor.WHITE + pdfFile.getVersion() + this.GREEN + " Permissions:");
    player.sendMessage("2:");
    player.sendMessage(this.GREEN + "ftx.admin.reg - this permission allows the user/group to start a new game registration.");
    player.sendMessage("3:");
    player.sendMessage(this.GREEN + "ftx.admin.begin - this permission allows the user/group to begin the game.");
    player.sendMessage("4:");
    player.sendMessage(this.GREEN + "ftx.admin.cancel - this permission allows the user/group to cancel the countdown.");
    player.sendMessage("5:");
    player.sendMessage(this.GREEN + "ftx.admin.endgame - this permission allows the user/group to end the game in progress.");
    player.sendMessage("6:");
    player.sendMessage(this.GREEN + "ftx.admin.define - this permission allows the user/group to define the arena and lobby areas.");
    player.sendMessage("7:");
    player.sendMessage(this.GREEN + "ftx.admin.generate - this permission allows the user/group to generate a temporary arena.");
    player.sendMessage("8:");
    player.sendMessage(this.GREEN + "ftx.admin.freeze - this permission allows the user/group to freeze or unfreeze players with commands.");
    player.sendMessage("9:");
    player.sendMessage(this.GREEN + "ftx.admin.forcereg - this permission allows the user/group to force other users to register in the game.");
    player.sendMessage("10:");
    player.sendMessage(this.GREEN + "ftx.users.join - this permission allows the user/group to join a game. It is given to the user by default");
  }
  
  public void getHelp1(Player player)
  {
    PluginDescriptionFile pdfFile = this.plugin.getDescription();
    player.sendMessage(" ");
    player.sendMessage(this.GREEN + "FreezeTagX v" + ChatColor.WHITE + pdfFile.getVersion() + this.GREEN + " Commands:");
    player.sendMessage("1:");
    player.sendMessage(this.GREEN + "/ftx reg - this command allows the user to start a new game registration.");
    player.sendMessage("2:");
    player.sendMessage(this.GREEN + "/ftx begin <Optional time> - this command allows the user to begin the game.");
    player.sendMessage("3:");
    player.sendMessage(this.GREEN + "/ftx cancel - this command allows the user to cancel the countdown.");
    player.sendMessage("4:");
    player.sendMessage(this.GREEN + "/ftx endgame - this command allows the user to end the game in progress.");
    player.sendMessage("5:");
    player.sendMessage(this.GREEN + "/ftx define arena or lobby - this command allows the user to define the arena and lobby areas.");
    player.sendMessage("6:");
    player.sendMessage(this.GREEN + "/ftx generate length width height - this command allows the user to generate a temporary arena with them as the center.");
    player.sendMessage(" ");
    player.sendMessage(this.GREEN + "For more commands do /ftx help 2");
  }
  
  public void getHelp2(Player player)
  {
    PluginDescriptionFile pdfFile = this.plugin.getDescription();
    player.sendMessage(" ");
    player.sendMessage(this.GREEN + "FreezeTagX v" + ChatColor.WHITE + pdfFile.getVersion() + this.GREEN + " Commands 2:");
    player.sendMessage("7:");
    player.sendMessage(this.GREEN + "/ftx freeze <Player> - this command allows the user to freeze or unfreeze players with commands.");
    player.sendMessage("8:");
    player.sendMessage(this.GREEN + "/ftx forcereg <Player> - this command allows the user to force other users to register in the game.");
    player.sendMessage("9:");
    player.sendMessage(this.GREEN + "/ftx join - this command allows the user to join a game in registration. It is given to the user by default");
    player.sendMessage("10:");
    player.sendMessage(this.GREEN + "/ftx permissions - this command lists all the required permissions for freezetag commands.");
    player.sendMessage("11:");
    player.sendMessage(this.GREEN + "/ftx help - returns you to page one");
  }
}









