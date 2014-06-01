 package me.dimensio.ftx;
 
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Random;
 import org.bukkit.Bukkit;
 import org.bukkit.ChatColor;
 import org.bukkit.GameMode;
 import org.bukkit.Location;
 import org.bukkit.Material;
 import org.bukkit.World;
 import org.bukkit.block.Block;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 import org.bukkit.potion.PotionEffect;
 import org.bukkit.potion.PotionEffectType;
 
 public class GameHandler
 {
   private final FreezeTag plugin;
   private final Config config;
   public String PREFIX = ChatColor.DARK_GREEN + "[FreezeTagX] ";
   public String ERR_PREFIX = ChatColor.RED + "[FreezeTagX] ";
   int time = 5;
   public HashMap<Player, ItemStack[]> inventories = new HashMap();
   public HashMap<Player, GameMode> gamemode = new HashMap();
   
   public GameHandler(FreezeTag instance, Config config)
   {
     this.plugin = instance;
     
     this.config = config;
   }
   
   public void startGame(Player player)
   {
     int minimum = this.config.getCustomConfig().getInt("MinimumPlayer");
     if ((!this.plugin.inRegistration) && (!this.plugin.inGame))
     {
       if (player.hasPermission("ftx.admin.reg"))
       {
         this.plugin.inRegistration = true;
         Bukkit.getServer().broadcastMessage(this.PREFIX + "A new game of Freeze Tag has begun! To join, type /ftx join");
         this.plugin.players.put(player, "Regular");
         this.plugin.numOfPlayers += 1;
         if (this.config.lobby)
         {
           this.plugin.oldLocations.put(player, player.getLocation());
           telePlayerToLobby(player);
         }
         player.sendMessage(ChatColor.RED + "[FreezeTagX] You've started a new game, and successfully registered to play. To start the game, type /ftx begin with at least " + minimum + " players registered.");
       }
       else
       {
         player.sendMessage(this.ERR_PREFIX + "You do not have permission to do that.");
       }
     }
     else if (this.plugin.inRegistration) {
       player.sendMessage(this.ERR_PREFIX + "There is already a game in the registration stage! To join, type /ftx join");
     } else {
       player.sendMessage(this.ERR_PREFIX + "There is already a game in progress! Wait for this game to finish before starting a new one.");
     }
   }
   
   public void listPlayers(Player player)
   {
     if ((!this.plugin.inGame) && (!this.plugin.inRegistration)) {
       return;
     }
     Iterator i = this.plugin.players.keySet().iterator();
     player.sendMessage(" ");
     player.sendMessage(this.PREFIX + "Current player list:");
     while (i.hasNext())
     {
       Player current = (Player)i.next();
       String status;
     
       if (((String)this.plugin.players.get(current)).equalsIgnoreCase("FROZEN"))
       {
         status = ChatColor.BLUE + "FROZEN";
       }
       else
       {
      
         if (((String)this.plugin.players.get(current)).equalsIgnoreCase("Chaser")) {
           status = ChatColor.RED + "Chaser";
         } else {
           status = ChatColor.GREEN + "Un-frozen";
         }
       }
       player.sendMessage(this.PREFIX + ChatColor.YELLOW + current.getName() + ChatColor.DARK_GREEN + " (Status: " + status + ChatColor.DARK_GREEN + ")");
     }
   }
   
   public void joinGame(Player player)
   {
     if ((this.plugin.inRegistration) && (!this.plugin.inGame))
     {
       if (player.hasPermission("ftx.users.join"))
       {
         if (!this.plugin.players.containsKey(player))
         {
           this.plugin.players.put(player, "Regular");
           if (this.config.lobby) {
             telePlayerToLobby(player);
           } else {
             this.plugin.oldLocations.put(player, player.getLocation());
           }
           this.plugin.numOfPlayers += 1;
           Bukkit.getServer().broadcastMessage(this.PREFIX + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GREEN + " has registered! There are currently " + ChatColor.YELLOW + this.plugin.players.size() + ChatColor.DARK_GREEN + " players registered.");
           player.sendMessage(this.ERR_PREFIX + "You've successfully registered to play. There are currently " + ChatColor.YELLOW + this.plugin.players.size() + ChatColor.RED + " players registered.");
           int minimum = this.config.getCustomConfig().getInt("MinimumPlayer");
           int halfmin = Math.round(minimum / 2);
           if (this.plugin.numOfPlayers > minimum + halfmin) {
             autoStart();
           }
         }
         else
         {
           player.sendMessage(this.ERR_PREFIX + "You're already registered! To un-register, type /ftx unreg");
         }
       }
       else {
         player.sendMessage(this.ERR_PREFIX + "You do not have permission to do that.");
       }
     }
     else if ((!this.plugin.inRegistration) && (!this.plugin.inGame)) {
       player.sendMessage(this.ERR_PREFIX + "There isn't a game registration in progress. Get an admin to start the game for you.");
     } else if ((!this.plugin.inRegistration) && (this.plugin.inGame)) {
       player.sendMessage(this.ERR_PREFIX + "There's already a game in progress. Wait until the next game starts.");
     }
   }
   
   public void cleanUpGame()
   {
     unTelePlayers(true);
     Bukkit.getServer().getScheduler().getPendingTasks().clear();
     Bukkit.getServer().getScheduler().cancelTasks(this.plugin);
     restoreInventories();
     this.plugin.numOfPlayers = 0;
     this.plugin.numOfChasers = 0;
     this.plugin.numOfFrozen = 0;
     this.plugin.inGame = false;
     this.plugin.inRegistration = false;
     this.plugin.inCountdown = false;
     this.plugin.players.clear();
   }
   
   public void unreg(Player player)
   {
     if (!this.plugin.inRegistration) {
       return;
     }
     if ((this.plugin.inRegistration) && (!this.plugin.inCountdown))
     {
       this.plugin.players.remove(player);
       Bukkit.getServer().broadcastMessage(this.ERR_PREFIX + ChatColor.YELLOW + player.getName() + ChatColor.RED + " has un-registered from the current game!");
       if (this.plugin.numOfPlayers == 1)
       {
         Bukkit.getServer().broadcastMessage(this.ERR_PREFIX + "There are no more players left in the game! The game has ended.");
         this.plugin.inRegistration = false;
       }
       unTelePlayer(player, true);
       this.plugin.numOfPlayers -= 1;
     }
     else
     {
       player.sendMessage(this.ERR_PREFIX + "You can't leave a game in the countdown.");
     }
   }
   
   public void beginGame(Player player, int timeLimit)
   {
     int minimum = this.config.getCustomConfig().getInt("MinimumPlayer");
     if ((!this.plugin.inRegistration) || (this.plugin.inGame)) {
       return;
     }
     if (player.hasPermission("ftx.admin.begin"))
     {
       if (this.plugin.numOfPlayers < minimum)
       {
         player.sendMessage(this.ERR_PREFIX + "There are not enough players to begin the game. You need at least " + ChatColor.YELLOW + minimum + ChatColor.RED + " players.");
       }
       else
       {
         this.plugin.inRegistration = false;
         this.plugin.inCountdown = true;
         
         this.plugin.numOfChasers = ((int)Math.floor(this.plugin.numOfPlayers / minimum));
         Object[] key = this.plugin.players.keySet().toArray();
         
         HashSet chasers = new HashSet();
         for (int x = 1; x <= this.plugin.numOfChasers; x++)
         {
           Random random = new Random();
           int chaser = random.nextInt(this.plugin.numOfPlayers);
           while (((String)this.plugin.players.get((Player)key[chaser])).equalsIgnoreCase("Chaser")) {
             chaser = random.nextInt(this.plugin.numOfPlayers);
           }
           Player chaserP = (Player)key[chaser];
           
           this.plugin.players.put(chaserP, "Chaser");
           chasers.add(chaserP);
           chaserP.sendMessage(this.PREFIX + "You are a " + ChatColor.YELLOW + "CHASER" + ChatColor.DARK_GREEN + "! Freeze other players by punching them!");
           chaserP.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1, 120));
           chaserP.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1, 120));
         }
         String message = this.PREFIX + "The chasers are: ";
         Object[] arr = chasers.toArray();
         for (int i = 0; i < chasers.size(); i++)
         {
           Player p = (Player)arr[i];
           message = message + ChatColor.YELLOW + p.getName();
           if (i == chasers.size() - 1) {
             message = message + ChatColor.DARK_GREEN + ". ";
           } else {
             message = message + ChatColor.DARK_GREEN + ", ";
           }
         }
         for (int i = 0; i < this.plugin.players.keySet().toArray().length; i++)
         {
           Player player2 = (Player)this.plugin.players.keySet().toArray()[i];
           player2.sendMessage(message);
         }
         if (!this.config.lobby) {
           this.plugin.oldLocations.put(player, player.getLocation());
         }
         telePlayersToArena();
         
         storeInventories();
         
 
         startCountdown(timeLimit);
       }
     }
     else {
       player.sendMessage(this.ERR_PREFIX + "You do not have permission to do that.");
     }
   }
   
   public void autoStart()
   {
     if ((!this.plugin.inRegistration) || (this.plugin.inGame)) {
       return;
     }
     this.plugin.inRegistration = false;
     this.plugin.inCountdown = true;
     int minimum = this.config.getCustomConfig().getInt("MinimumPlayer");
     this.plugin.numOfChasers = ((int)Math.floor(this.plugin.numOfPlayers / minimum));
     Object[] key = this.plugin.players.keySet().toArray();
     
     HashSet chasers = new HashSet();
     for (int x = 1; x <= this.plugin.numOfChasers; x++)
     {
       Random random = new Random();
       int chaser = random.nextInt(this.plugin.numOfPlayers);
       while (((String)this.plugin.players.get((Player)key[chaser])).equalsIgnoreCase("Chaser")) {
         chaser = random.nextInt(this.plugin.numOfPlayers);
       }
       Player chaserP = (Player)key[chaser];
       
       this.plugin.players.put(chaserP, "Chaser");
       chasers.add(chaserP);
       chaserP.sendMessage(this.PREFIX + "You are a " + ChatColor.YELLOW + "CHASER" + ChatColor.DARK_GREEN + "! Freeze other players by punching them!");
       chaserP.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1, 120));
       chaserP.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1, 120));
     }
     String message = this.PREFIX + "The chasers are: ";
     Object[] arr = chasers.toArray();
     for (int i = 0; i < chasers.size(); i++)
     {
       Player p = (Player)arr[i];
       message = message + ChatColor.YELLOW + p.getName();
       if (i == chasers.size() - 1) {
         message = message + ChatColor.DARK_GREEN + ". ";
       } else {
         message = message + ChatColor.DARK_GREEN + ", ";
       }
     }
     for (int i = 0; i < this.plugin.players.keySet().toArray().length; i++)
     {
       Player player = (Player)this.plugin.players.keySet().toArray()[i];
       player.sendMessage(message);
     }
     telePlayersToArena();
     
     storeInventories();
     
 
     startCountdown(this.config.defaultTime);
   }
   
   public void startCountdown(int timeLimit)
   {
     this.time = timeLimit;
     if (this.plugin.inCountdown)
     {
       Bukkit.getServer().broadcastMessage(this.PREFIX + "The game is about to begin!");
       Bukkit.getServer().broadcastMessage(this.PREFIX + "3..");
       Bukkit.getServer().getScheduler().runTaskLater(this.plugin, new Runnable()
       {
         public void run()
         {
           Bukkit.getServer().broadcastMessage(GameHandler.this.PREFIX + "2..");
         }
       }, 30L);
       
 
       Bukkit.getServer().getScheduler().runTaskLater(this.plugin, new Runnable()
       {
         public void run()
         {
           Bukkit.getServer().broadcastMessage(GameHandler.this.PREFIX + "1..");
         }
       }, 60L);
       
 
       Bukkit.getServer().getScheduler().runTaskLater(this.plugin, new Runnable()
       {
         public void run()
         {
           GameHandler.this.plugin.inGame = true;
           Bukkit.getServer().broadcastMessage(GameHandler.this.PREFIX + "GO!");
           GameHandler.this.plugin.inCountdown = false;
           Bukkit.getServer().getScheduler().runTaskLater(GameHandler.this.plugin, new Runnable()
           {
             public void run()
             {
               Bukkit.getServer().broadcastMessage(GameHandler.this.PREFIX + "Hurry, chasers! You only have " + ChatColor.RED + "ONE MINUTE" + ChatColor.DARK_GREEN + " left!");
             }
           }, (GameHandler.this.time - 1) * 60 * 20);
           
 
           Bukkit.getServer().getScheduler().runTaskLater(GameHandler.this.plugin, new Runnable()
           {
             public void run()
             {
               GameHandler.this.plugin.gameHandler.regularVictory();
             }
           }, GameHandler.this.time * 60 * 20);
         }
       }, 90L);
     }
   }
   
   public void cancelCountdown(Player player)
   {
     if ((player.hasPermission("ftx.admin.cancel")) && (this.plugin.inCountdown))
     {
       Bukkit.getServer().getScheduler().cancelTasks(this.plugin);
       this.plugin.inGame = false;
       this.plugin.inCountdown = false;
       this.plugin.inRegistration = true;
       this.plugin.numOfChasers = 0;
       this.plugin.numOfFrozen = 0;
       if (this.config.lobby) {
         telePlayersToLobby();
       } else {
         unTelePlayers(false);
       }
       Bukkit.getServer().broadcastMessage(this.PREFIX + ChatColor.YELLOW + player.getName() + " cancelled the countdown!");
     }
   }
   
   public boolean checkVictory()
   {
     if (!this.plugin.inGame) {
       return false;
     }
     if (this.plugin.numOfFrozen == this.plugin.numOfPlayers - this.plugin.numOfChasers) {
       return true;
     }
     return false;
   }
   
   public void victory()
   {
     if (this.config.verbose) {
       System.out.println(this.PREFIX + "The game is over! The chasers have won!");
     }
     if (checkVictory())
     {
       Bukkit.getServer().broadcastMessage(this.PREFIX + "The game is over! The chasers have won!");
       Bukkit.getServer().getScheduler().cancelTasks(this.plugin);
       Bukkit.getServer().getScheduler().getPendingTasks().clear();
     }
     cleanUpGame();
   }
   
   public void regularVictory()
   {
     if (this.config.verbose) {
       System.out.println(this.PREFIX + "The game is over! The regulars have won!");
     }
     if (!checkVictory())
     {
       Bukkit.getServer().broadcastMessage(this.PREFIX + "The game is over! The regulars have won!");
       
       cleanUpGame();
     }
   }
   
   public boolean storeInventories()
   {
     if ((this.plugin.players == null) || (this.plugin.players.isEmpty())) {
       return false;
     }
     Iterator i = this.plugin.players.keySet().iterator();
     while (i.hasNext())
     {
       Player p = (Player)i.next();
       GameMode playermode = p.getGameMode();
       ItemStack[] pI = p.getInventory().getContents();
       
       this.inventories.put(p, pI);
       this.gamemode.put(p, playermode);
       p.setGameMode(GameMode.SURVIVAL);
       p.getInventory().clear();
       p.getInventory().addItem(new ItemStack[] { new ItemStack(this.config.item, 1) });
     }
     return true;
   }
   
   public boolean restoreInventories()
   {
     if ((this.inventories == null) || (this.inventories.isEmpty())) {
       return false;
     }
     Iterator i = this.inventories.keySet().iterator();
     while (i.hasNext())
     {
       Player p = (Player)i.next();
       p.getInventory().setContents((ItemStack[])this.inventories.get(p));
       p.setGameMode((GameMode)this.gamemode.get(p));
     }
     this.inventories.clear();
     return true;
   }
   
   public boolean restoreInventory(Player player)
   {
     if ((this.inventories == null) || (this.inventories.isEmpty()) || (!this.inventories.containsKey(player))) {
       return false;
     }
     player.getInventory().setContents((ItemStack[])this.inventories.get(player));
     this.inventories.remove(player);
     return true;
   }
   
   public void telePlayersToArena()
   {
     if (!this.config.arena) {
       return;
     }
     Iterator i = this.plugin.players.keySet().iterator();
     while (i.hasNext())
     {
       Player p = (Player)i.next();
       
       int[][] arr = new int[2][3];
       try
       {
         String[] p1 = this.config.arena_area1.split(",");
         String[] p2 = this.config.arena_area2.split(",");
         arr = Arena.parseMinMax(p1, p2);
       }
       catch (NumberFormatException e) {}
       Random random = new Random();
       int xGap = arr[1][0] - arr[0][0] - 2;
       int toX = arr[0][0] + 1 + random.nextInt(xGap);
       int zGap = arr[1][2] - arr[0][2] - 2;
       int toZ = arr[0][2] + 1 + random.nextInt(zGap);
       int toY = arr[0][1] + 1;
       World w = Bukkit.getServer().getWorld(this.config.arena_world);
       while ((w.getBlockAt(toX, toY - 1, toZ).isLiquid()) || (w.getBlockAt(toX, toY, toZ).isLiquid()))
       {
         toX = arr[0][0] + random.nextInt(arr[1][0] - arr[0][0]);
         toZ = arr[0][2] + random.nextInt(arr[1][2] - arr[0][2]);
       }
       while (w.getBlockAt(toX, toY, toZ).getType() != Material.AIR) {
         toY++;
       }
       p.teleport(new Location(w, toX, toY, toZ));
       p.setFlying(false);
     }
   }
   
   public void telePlayersToLobby()
   {
     if (!this.config.lobby) {
       return;
     }
     Iterator i = this.plugin.players.keySet().iterator();
     while (i.hasNext())
     {
       Player p = (Player)i.next();
       
       int[][] arr = new int[2][3];
       try
       {
         String[] p1 = this.config.lobby_area1.split(",");
         String[] p2 = this.config.lobby_area2.split(",");
         arr = Arena.parseMinMax(p1, p2);
       }
       catch (NumberFormatException e) {}
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
       p.teleport(new Location(w, toX, toY, toZ));
     }
   }
   
   public void telePlayerToLobby(Player p)
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
     catch (NumberFormatException e) {}
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
     while (w.getBlockAt(toX, toY, toZ).getType() != Material.AIR)
     {
       while (w.getBlockAt(toX, toY, toZ).getType() != Material.AIR) {
         toY++;
       }
       p.teleport(new Location(w, toX, toY, toZ));
     }
   }
   
   public void unTelePlayers(boolean clear)
   {
     if (this.plugin.oldLocations == null) {
       return;
     }
     Iterator i = this.plugin.oldLocations.keySet().iterator();
     while (i.hasNext())
     {
       Player player = (Player)i.next();
       player.teleport((Location)this.plugin.oldLocations.get(player));
     }
     this.plugin.oldLocations.clear();
   }
   
   public void unTelePlayer(Player p, boolean clear)
   {
     if ((this.plugin.oldLocations == null) || (!this.plugin.oldLocations.containsKey(p))) {
       return;
     }
     p.teleport((Location)this.plugin.oldLocations.get(p));
     if (clear) {
       this.plugin.oldLocations.remove(p);
     }
   }
   
   public void deleteArena(Location loc, Location loc2, Player player)
   {
     if (player.hasPermission("ftx.admin.delete"))
     {
       World world = player.getWorld();
       int startx = loc.getBlockX();
       int starty = loc.getBlockY();
       int startz = loc.getBlockZ();
       int endx = loc2.getBlockX();
       int endy = loc2.getBlockY();
       int endz = loc2.getBlockZ();
       for (int x = startx; x <= endx; x++) {
         for (int y = starty; y <= endy; y++) {
           for (int z = startz; z <= endz; z++)
           {
             Block block = world.getBlockAt(x, y, z);
             block.setType(Material.AIR);
           }
         }
       }
       this.config.arena = false;
       this.config.arena_area1 = null;
       this.config.arena_area2 = null;
       this.config.saveArena();
     }
     else
     {
       player.sendMessage(this.ERR_PREFIX + "You do not have permission to do that.");
     }
   }
 }









