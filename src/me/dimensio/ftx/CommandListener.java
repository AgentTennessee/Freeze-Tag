package me.dimensio.ftx;

import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class CommandListener
{
  private final FreezeTag plugin;
  private final GameHandler gameHandler;
  private final Config config;
  private final Helper helper;
  public String PREFIX = ChatColor.DARK_GREEN + "[FreezeTagX] ";
  public String ERR_PREFIX = ChatColor.RED + "[FreezeTagX] ";
  String[] x1;
  String[] x2;
  
  public CommandListener(FreezeTag instance, GameHandler game, Config config, Helper helper)
  {
    this.plugin = instance;
    this.gameHandler = game;
    this.config = config;
    
    this.helper = helper;
  }
  
  public void setupCommands()
  {
    PluginCommand ftx = this.plugin.getCommand("ftx");
    CommandExecutor commandExecutor = new CommandExecutor()
    {
      public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
      {
        if (((sender instanceof Player)) && (args.length > 0)) {
          CommandListener.this.commandHandler((Player)sender, args);
        }
        return true;
      }
    };
    if (ftx != null) {
      ftx.setExecutor(commandExecutor);
    }
  }
  
  public void commandHandler(Player player, String[] args)
  {
    if (args[0].equalsIgnoreCase("reg"))
    {
      this.gameHandler.startGame(player);
    }
    else if (args[0].equalsIgnoreCase("join"))
    {
      this.gameHandler.joinGame(player);
    }
    else if (args[0].equalsIgnoreCase("unreg"))
    {
      this.gameHandler.unreg(player);
    }
    else if (args[0].equalsIgnoreCase("begin"))
    {
      if (args.length > 1) {
        this.gameHandler.beginGame(player, Integer.parseInt(args[1]));
      } else {
        this.gameHandler.beginGame(player, this.config.defaultTime);
      }
    }
    else if (args[0].equalsIgnoreCase("delete"))
    {
      for (int i = 0; i < 3; i++)
      {
        this.x1 = this.config.arena_area1.split(",");
        this.x2 = this.config.arena_area2.split(",");
      }
      Location loc1 = new Location(Bukkit.getWorld(this.config.arena_world), Integer.parseInt(this.x1[0]), Integer.parseInt(this.x1[1]), Integer.parseInt(this.x1[2]));
      Location loc2 = new Location(Bukkit.getWorld(this.config.arena_world), Integer.parseInt(this.x2[0]), Integer.parseInt(this.x2[1]), Integer.parseInt(this.x2[2]));
      this.gameHandler.deleteArena(loc1, loc2, player);
    }
    else if (args[0].equalsIgnoreCase("cancel"))
    {
      this.gameHandler.cancelCountdown(player);
    }
    else if (args[0].equalsIgnoreCase("list"))
    {
      this.gameHandler.listPlayers(player);
    }
    else if (args[0].equalsIgnoreCase("define"))
    {
      if (args.length < 1) {
        player.sendMessage(this.ERR_PREFIX + "You must specify an argument.");
      } else if (player.hasPermission("ftx.admin.define")) {
        if (args[1].equalsIgnoreCase("lobby"))
        {
          this.plugin.inAreaMode = true;
          this.plugin.mode = FreezeTag.areaMode.LOBBY_1;
          this.plugin.areaPlayer = player;
          player.sendMessage(this.PREFIX + "You're in lobby define mode! Punch the first block of your cuboid.");
        }
        else if (args[1].equalsIgnoreCase("arena"))
        {
          this.plugin.inAreaMode = true;
          this.plugin.mode = FreezeTag.areaMode.ARENA_1;
          this.plugin.areaPlayer = player;
          player.sendMessage(this.PREFIX + "You're in arena define mode! Punch the first block of your cuboid.");
        }
      }
    }
    else if (args[0].equalsIgnoreCase("generate"))
    {
      if ((!player.hasPermission("ftx.admin.generate")) || (args.length == 4))
      {
        if (this.config.arena)
        {
          for (int i = 0; i < 3; i++)
          {
            this.x1 = this.config.arena_area1.split(",");
            this.x2 = this.config.arena_area2.split(",");
          }
          Location loc1 = new Location(Bukkit.getWorld(this.config.arena_world), Integer.parseInt(this.x1[0]), Integer.parseInt(this.x1[1]), Integer.parseInt(this.x1[2]));
          Location loc2 = new Location(Bukkit.getWorld(this.config.arena_world), Integer.parseInt(this.x2[0]), Integer.parseInt(this.x2[1]), Integer.parseInt(this.x2[2]));
          this.gameHandler.deleteArena(loc1, loc2, player);
        }
        Location loc = player.getLocation();
        this.plugin.arena = Arena.build(new Location(loc.getWorld(), loc.getX(), loc.getY() - 1.0D, loc.getZ()), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), new Random());
        String[] bounds = this.plugin.arena.getBounds();
        this.config.arena = true;
        this.config.arena_area1 = bounds[0];
        this.config.arena_area2 = bounds[1];
        this.config.arena_world = player.getWorld().getName();
        this.config.saveArena();
        player.sendMessage(this.PREFIX + "Arena generated!");
      }
    }
    else if (args[0].equalsIgnoreCase("freeze"))
    {
      if (args.length == 1)
      {
        player.sendMessage(this.ERR_PREFIX + "You must define a player to freeze.");
        return;
      }
      if ((!player.hasPermission("ftx.admin.freeze")) || ((!this.plugin.inGame) && (!this.plugin.inRegistration))) {
        return;
      }
      Player p = Bukkit.getServer().getPlayer(args[1]);
      if ((!this.plugin.players.containsKey(p)) || (((String)this.plugin.players.get(p)).equalsIgnoreCase("Regular"))) {
        return;
      }
      this.plugin.players.put(p, "Regular");
      p.sendMessage(this.PREFIX + "You've been un-frozen by " + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GREEN + "!");
      player.sendMessage(this.PREFIX + "You've un-frozen " + ChatColor.YELLOW + args[1] + ChatColor.DARK_GREEN + "!");
    }
    else if (args[0].equalsIgnoreCase("forcereg"))
    {
      if (args.length == 1)
      {
        player.sendMessage(this.ERR_PREFIX + "You must define a player to force register.");
        return;
      }
      if ((!player.hasPermission("ftx.admin.forcereg")) || (!this.plugin.inRegistration)) {
        return;
      }
      Player p = Bukkit.getServer().getPlayer(args[1]);
      if (!p.isOnline()) {
        return;
      }
      if ((this.plugin.players == null) || (this.plugin.players.isEmpty())) {
        return;
      }
      if (this.plugin.players.containsKey(p))
      {
        player.sendMessage(this.ERR_PREFIX + "That player is already registered!");
        return;
      }
      this.gameHandler.joinGame(p);
      player.sendMessage(this.PREFIX + "You've successfully forced " + ChatColor.YELLOW + p.getName() + ChatColor.DARK_GREEN + " to register.");
    }
    else if (args[0].equalsIgnoreCase("endgame"))
    {
      if ((!player.hasPermission("ftx.admin.endgame")) || (this.plugin.inGame) || (this.plugin.inRegistration))
      {
        this.gameHandler.cleanUpGame();
        Bukkit.getServer().broadcastMessage(this.PREFIX + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GREEN + " has ended the current game!");
      }
    }
    else if (args[0].equalsIgnoreCase("lobby"))
    {
      if ((this.plugin.inRegistration) && (!this.plugin.players.containsKey(player))) {}
      this.gameHandler.telePlayerToLobby(player);
    }
    else if (args[0].equalsIgnoreCase("rules"))
    {
      this.helper.getRules(player);
    }
    else if (args[0].equalsIgnoreCase("permissions"))
    {
      this.helper.getPermissions(player);
    }
    else if (args[0].equalsIgnoreCase("help"))
    {
      if (args.length == 1) {
        this.helper.getHelp1(player);
      } else if ((args.length == 2) && (args[1].equals("2"))) {
        this.helper.getHelp2(player);
      }
    }
  }
}





