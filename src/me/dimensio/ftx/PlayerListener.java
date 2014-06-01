package me.dimensio.ftx;

import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener
  implements Listener
{
  private final FreezeTag plugin;
  private final GameHandler gameHandler;
  private final Config config;
  public String PREFIX = ChatColor.DARK_GREEN + "[FreezeTagX] ";
  public String ERR_PREFIX = ChatColor.RED + "[FreezeTagX] ";
  
  public PlayerListener(FreezeTag instance, GameHandler game, Config config)
  {
    this.plugin = instance;
    this.gameHandler = game;
    
    this.config = config;
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onPlayerInteract(PlayerInteractEvent event)
  {
    if (!this.plugin.inGame) {
      return;
    }
    ItemStack item = event.getPlayer().getItemInHand();
    if (!item.equals(new ItemStack(this.config.item, 1))) {
      return;
    }
    if ((event.getAction() == Action.LEFT_CLICK_AIR) || (event.getAction() == Action.LEFT_CLICK_BLOCK)) {
      return;
    }
    this.gameHandler.listPlayers(event.getPlayer());
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    if (this.config.arena)
    {
      int[][] arr = new int[2][3];
      try
      {
        String[] p1 = this.config.arena_area1.split(",");
        String[] p2 = this.config.arena_area2.split(",");
        arr = Arena.parseMinMax(p1, p2);
      }
      catch (NumberFormatException e)
      {
        e.printStackTrace();
      }
      if (Arena.isWithin(arr[0], arr[1], event.getPlayer().getLocation().getBlock())) {
        this.gameHandler.telePlayerToLobby(event.getPlayer());
      }
    }
    if (!this.plugin.inRegistration) {
      return;
    }
    if (this.config.verbose) {
      System.out.println(this.PREFIX + "Informing " + event.getPlayer().getName() + " of current game.");
    }
    event.getPlayer().sendMessage(this.PREFIX + "There's a game of Stuck In The Mud in the registration stage! Type /mud join to join in the fun!");
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onPlayerMove(PlayerMoveEvent event)
  {
    if ((!this.plugin.inGame) && (!this.plugin.inCountdown)) {
      return;
    }
    if ((this.plugin.inCountdown) && (this.plugin.players.containsKey(event.getPlayer())))
    {
      Location locFrom = event.getFrom();
      Location locTo = event.getTo();
      if ((locFrom.getX() != locTo.getX()) || (locFrom.getZ() != locTo.getZ())) {
        event.setTo(new Location(event.getPlayer().getWorld(), locFrom.getX(), locTo.getY(), locFrom.getZ()));
      }
      return;
    }
    if ((this.plugin.inGame) && (this.plugin.players.containsKey(event.getPlayer())) && (((String)this.plugin.players.get(event.getPlayer())).equalsIgnoreCase("FROZEN")))
    {
      Location locFrom = event.getFrom();
      Location locTo = event.getTo();
      if ((locFrom.getX() != locTo.getX()) || (locFrom.getZ() != locTo.getZ())) {
        event.setTo(new Location(event.getPlayer().getWorld(), locFrom.getX(), locTo.getY(), locFrom.getZ()));
      }
    }
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    Player player = event.getPlayer();
    if (this.plugin.players.containsKey(player)) {
      if (this.plugin.inRegistration)
      {
        this.gameHandler.restoreInventory(player);
        this.plugin.players.remove(player);
        Bukkit.getServer().broadcastMessage(this.ERR_PREFIX + ChatColor.YELLOW + player.getName() + ChatColor.RED + " has left the server! They've been un-registered from the current game.");
        if (this.plugin.numOfPlayers == 1)
        {
          Bukkit.getServer().broadcastMessage(this.ERR_PREFIX + "There are no more players left in the game! The game has ended.");
          this.plugin.inRegistration = false;
        }
        this.plugin.numOfPlayers -= 1;
      }
      else if ((this.plugin.inGame) || (this.plugin.inCountdown))
      {
        this.gameHandler.restoreInventory(player);
        this.plugin.players.remove(player);
        Bukkit.getServer().broadcastMessage(this.ERR_PREFIX + ChatColor.YELLOW + player.getName() + ChatColor.RED + " has left the server! They've been removed from the current game.");
        if (this.plugin.numOfPlayers == 2)
        {
          Bukkit.getServer().broadcastMessage(this.ERR_PREFIX + "There are not enough players left to continue. The game has ended.");
          this.gameHandler.cleanUpGame();
        }
        else
        {
          this.plugin.numOfPlayers -= 1;
          if (this.gameHandler.checkVictory()) {
            this.gameHandler.victory();
          }
        }
      }
    }
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onPlayerKick(PlayerKickEvent event)
  {
    Player player = event.getPlayer();
    if (this.plugin.players.containsKey(player)) {
      if (this.plugin.inRegistration)
      {
        this.gameHandler.restoreInventory(player);
        this.plugin.players.remove(player);
        Bukkit.getServer().broadcastMessage(this.ERR_PREFIX + ChatColor.YELLOW + player.getName() + ChatColor.RED + " has been kicked from the server! They've been un-registered from the current game.");
        if (this.plugin.numOfPlayers == 1)
        {
          Bukkit.getServer().broadcastMessage(this.ERR_PREFIX + "There are no more players left in the game! The game has ended.");
          this.plugin.inRegistration = false;
        }
        this.plugin.numOfPlayers -= 1;
      }
      else if (this.plugin.inGame)
      {
        this.gameHandler.restoreInventory(player);
        this.plugin.players.remove(player);
        Bukkit.getServer().broadcastMessage(this.ERR_PREFIX + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GREEN + " has been kicked from the server! They've been removed from the current game.");
        if (this.plugin.numOfPlayers == 2)
        {
          Bukkit.getServer().broadcastMessage(this.ERR_PREFIX + "There are not enough players left to continue. The game has ended.");
          this.gameHandler.cleanUpGame();
        }
        else
        {
          this.plugin.numOfPlayers -= 1;
          if (this.gameHandler.checkVictory()) {
            this.gameHandler.victory();
          }
        }
      }
    }
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onPlayerRespawn(PlayerRespawnEvent event)
  {
    if ((!this.plugin.inGame) && (!this.plugin.inRegistration)) {
      return;
    }
    if (!this.plugin.players.containsKey(event.getPlayer())) {
      return;
    }
    if (this.plugin.inRegistration)
    {
      if (!this.config.lobby) {
        return;
      }
      int[][] arr = new int[2][3];
      try
      {
        String[] p1 = this.config.lobby_area1.split(",");
        String[] p2 = this.config.lobby_area2.split(",");
        arr = Arena.parseMinMax(p1, p2);
      }
      catch (NumberFormatException e)
      {
        e.printStackTrace();
      }
      Random random = new Random();
      int xGap = arr[1][0] - arr[0][0] - 2;
      int toX = arr[0][0] + 1 + random.nextInt(xGap);
      int zGap = arr[1][2] - arr[0][2] - 2;
      int toZ = arr[0][2] + 1 + random.nextInt(zGap);
      int toY = arr[0][1] + 1;
      World w = Bukkit.getServer().getWorld(this.config.lobby_world);
      while (w.getBlockAt(toX, toY, toZ).getType() == Material.AIR) {
        toY--;
      }
      while ((w.getBlockAt(toX, toY - 1, toZ).getType() == Material.WATER) || (w.getBlockAt(toX, toY - 1, toZ).getType() == Material.STATIONARY_WATER) || (w.getBlockAt(toX, toY - 1, toZ).getType() == Material.STATIONARY_LAVA) || (w.getBlockAt(toX, toY - 1, toZ).getType() == Material.LAVA))
      {
        toX = arr[0][0] + random.nextInt(arr[1][0] - arr[0][0]);
        toZ = arr[0][2] + random.nextInt(arr[1][2] - arr[0][2]);
      }
      while (w.getBlockAt(toX, toY, toZ).getType() != Material.AIR) {
        toY++;
      }
      event.setRespawnLocation(new Location(w, toX, toY, toZ));
    }
    else if (this.plugin.inGame)
    {
      if (!this.config.arena) {
        return;
      }
      int[][] arr = new int[2][3];
      try
      {
        String[] p1 = this.config.arena_area1.split(",");
        String[] p2 = this.config.arena_area2.split(",");
        arr = Arena.parseMinMax(p1, p2);
      }
      catch (NumberFormatException e)
      {
        e.printStackTrace();
      }
      Random random = new Random();
      int xGap = arr[1][0] - arr[0][0] - 2;
      int toX = arr[0][0] + 1 + random.nextInt(xGap);
      int zGap = arr[1][2] - arr[0][2] - 2;
      int toZ = arr[0][2] + 1 + random.nextInt(zGap);
      int toY = arr[0][1] + 1;
      World w = Bukkit.getServer().getWorld(this.config.arena_world);
      while ((w.getBlockAt(toX, toY - 1, toZ).getType() == Material.WATER) || (w.getBlockAt(toX, toY - 1, toZ).getType() == Material.STATIONARY_WATER) || (w.getBlockAt(toX, toY - 1, toZ).getType() == Material.STATIONARY_LAVA) || (w.getBlockAt(toX, toY - 1, toZ).getType() == Material.LAVA))
      {
        toX = arr[0][0] + random.nextInt(arr[1][0] - arr[0][0]);
        toZ = arr[0][2] + random.nextInt(arr[1][2] - arr[0][2]);
      }
      while (w.getBlockAt(toX, toY, toZ).getType() != Material.AIR) {
        toY++;
      }
      event.setRespawnLocation(new Location(w, toX, toY, toZ));
      event.getPlayer().getInventory().addItem(new ItemStack[] { new ItemStack(this.config.item, 1) });
    }
  }
}









