 package me.dimensio.ftx;
 
 import org.bukkit.ChatColor;
 import org.bukkit.block.Block;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.EventPriority;
 import org.bukkit.event.Listener;
 import org.bukkit.event.block.BlockBreakEvent;
 import org.bukkit.event.block.BlockDamageEvent;
 import org.bukkit.event.block.BlockPlaceEvent;
 import org.bukkit.event.entity.CreatureSpawnEvent;
 
 public class BlockListener
   implements Listener
 {
   private final FreezeTag plugin;
   private final Config config;
   public String PREFIX = ChatColor.DARK_GREEN + "[FreezeTagX] ";
   public String ERR_PREFIX = ChatColor.RED + "[FreezeTagX] ";
   
   public BlockListener(FreezeTag instance, Config config)
   {
     this.plugin = instance;
     this.config = config;
   }
   
   @EventHandler
   public void onBlockDamage(BlockDamageEvent event)
   {
     if (!this.plugin.inAreaMode) {
       return;
     }
     if (this.plugin.mode == FreezeTag.areaMode.NONE) {
       return;
     }
     if (this.plugin.areaPlayer == null) {
       return;
     }
     if (this.plugin.areaPlayer != event.getPlayer()) {
       return;
     }
     Block block = event.getBlock();
     if (this.plugin.mode == FreezeTag.areaMode.LOBBY_1)
     {
       if (this.config.lobby_world == null) {
         this.config.lobby_world = block.getWorld().getName();
       }
       int x = block.getX();
       int y = block.getY();
       int z = block.getZ();
       
       this.config.lobby_area1 = (x + "," + y + "," + z);
       event.getPlayer().sendMessage(this.PREFIX + "Lobby position #1 set to " + ChatColor.YELLOW + this.config.lobby_area1 + ChatColor.DARK_GREEN + " in world " + ChatColor.YELLOW + this.config.lobby_world);
       this.plugin.mode = FreezeTag.areaMode.LOBBY_2;
     }
     else if (this.plugin.mode == FreezeTag.areaMode.LOBBY_2)
     {
       if (!this.config.lobby_world.equals(block.getWorld().getName())) {
         return;
       }
       int x = block.getX();
       int y = block.getY();
       int z = block.getZ();
       
       this.config.lobby_area2 = (x + "," + y + "," + z);
       this.config.lobby = true;
       event.getPlayer().sendMessage(this.PREFIX + "Lobby position #2 set to " + ChatColor.YELLOW + this.config.lobby_area2 + ChatColor.DARK_GREEN + " in world " + ChatColor.YELLOW + this.config.arena_world);
       if (this.config.saveLobby())
       {
         event.getPlayer().sendMessage(this.PREFIX + "Lobby region defined!");
       }
       else
       {
         event.getPlayer().sendMessage(this.ERR_PREFIX + "Error in saving lobby region definition!");
         return;
       }
       this.plugin.inAreaMode = false;
       this.plugin.areaPlayer = null;
       this.plugin.mode = FreezeTag.areaMode.NONE;
     }
     else if (this.plugin.mode == FreezeTag.areaMode.ARENA_1)
     {
       if (this.config.arena_world == null) {
         this.config.arena_world = block.getWorld().getName();
       }
       int x = block.getX();
       int y = block.getY();
       int z = block.getZ();
       
       this.config.arena_area1 = (x + "," + y + "," + z);
       event.getPlayer().sendMessage(this.PREFIX + "Arena position #1 set to " + ChatColor.YELLOW + this.config.arena_area1 + ChatColor.DARK_GREEN + " in world " + ChatColor.YELLOW + this.config.arena_world);
       this.plugin.mode = FreezeTag.areaMode.ARENA_2;
     }
     else
     {
       if (!this.config.arena_world.equals(block.getWorld().getName())) {
         return;
       }
       int x = block.getX();
       int y = block.getY();
       int z = block.getZ();
       
       this.config.arena_area2 = (x + "," + y + "," + z);
       this.config.arena = true;
       event.getPlayer().sendMessage(this.PREFIX + "Arena position #2 set to " + ChatColor.YELLOW + this.config.arena_area2 + ChatColor.DARK_GREEN + " in world " + ChatColor.YELLOW + this.config.arena_world);
       if (this.config.saveArena())
       {
         event.getPlayer().sendMessage(this.PREFIX + "Arena region defined!");
       }
       else
       {
         event.getPlayer().sendMessage(this.ERR_PREFIX + "Error in saving arena region definition!");
         return;
       }
       this.plugin.inAreaMode = false;
       this.plugin.areaPlayer = null;
       
       this.plugin.mode = FreezeTag.areaMode.NONE;
     }
   }
   
   @EventHandler(priority=EventPriority.HIGHEST)
   public void onCreatureSpawn(CreatureSpawnEvent event)
   {
     Block block = event.getLocation().getBlock();
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
       if (Arena.isWithin(arr[0], arr[1], block)) {
         event.setCancelled(true);
       }
     }
     if (this.config.lobby)
     {
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
       if (Arena.isWithin(arr[0], arr[1], block)) {
         event.setCancelled(true);
       }
     }
   }
   
   @EventHandler
   public void onBlockPlace(BlockPlaceEvent event)
   {
     if ((!this.config.arena) && (!this.config.lobby)) {
       return;
     }
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
       if (Arena.isWithin(arr[0], arr[1], event.getBlock())) {
         event.setCancelled(true);
       }
     }
     if (this.config.lobby)
     {
       int[][] arr1 = new int[2][3];
       try
       {
         String[] p1 = this.config.lobby_area1.split(",");
         String[] p2 = this.config.lobby_area2.split(",");
         arr1 = Arena.parseMinMax(p1, p2);
       }
       catch (NumberFormatException e)
       {
         e.printStackTrace();
       }
       if (Arena.isWithin(arr1[0], arr1[1], event.getBlock())) {
         event.setCancelled(true);
       }
     }
   }
   
   @EventHandler
   public void onBlockBreak(BlockBreakEvent event)
   {
     if ((!this.config.arena) && (!this.config.lobby)) {
       return;
     }
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
       if (Arena.isWithin(arr[0], arr[1], event.getBlock())) {
         event.setCancelled(true);
       }
     }
     if (this.config.lobby)
     {
       int[][] arr1 = new int[2][3];
       try
       {
         String[] p1 = this.config.lobby_area1.split(",");
         String[] p2 = this.config.lobby_area2.split(",");
         arr1 = Arena.parseMinMax(p1, p2);
       }
       catch (NumberFormatException e)
       {
         e.printStackTrace();
       }
       if (Arena.isWithin(arr1[0], arr1[1], event.getBlock())) {
         event.setCancelled(true);
       }
     }
   }
 }









