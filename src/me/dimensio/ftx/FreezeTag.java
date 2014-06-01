 package me.dimensio.ftx;
 
 import java.util.HashMap;
 import java.util.logging.Logger;
 import org.bukkit.Bukkit;
 import org.bukkit.Location;
 import org.bukkit.entity.Player;
 import org.bukkit.plugin.PluginManager;
 import org.bukkit.plugin.java.JavaPlugin;
 
 public class FreezeTag
   extends JavaPlugin
 {
   private Config config;
   private  Helper helper;
   public GameHandler gameHandler;
   private EntityListener el;
   private PlayerListener pl;
   private BlockListener bl;
   private CommandListener cmdHandler;
   public String logPrefix;
   public static final Logger log = Logger.getLogger("Minecraft");
   public boolean inGame;
   public boolean inCountdown;
   public boolean inRegistration;
   public boolean inAreaMode;
   public Player areaPlayer;
   public Arena arena;
   public areaMode mode;
   public int numOfPlayers;
   public int numOfFrozen;
   public int numOfChasers;
   public HashMap<Player, Location> oldLocations;
   public HashMap<Player, String> players;
   
   public void onEnable()
   {
        config = new Config(this);
     helper = new Helper(this);
     gameHandler = new GameHandler(this, this.config);
     
     el = new EntityListener(this, this.gameHandler);
     pl = new PlayerListener(this, this.gameHandler, this.config);
     bl = new BlockListener(this, this.config);
     
     cmdHandler = new CommandListener(this, this.gameHandler, this.config, this.helper);
     
     logPrefix = "[FreezeTagX] ";
     
 
     inGame = false;
     inCountdown = false;
     inRegistration = false;
     inAreaMode = false;
     
 
     mode = areaMode.NONE;
     numOfPlayers = 0;
     numOfFrozen = 0;
     numOfChasers = 0;
    oldLocations = new HashMap();
     
     players = new HashMap();
     PluginManager pm = getServer().getPluginManager();
     pm.registerEvents(this.bl, this);
     pm.registerEvents(this.el, this);
     pm.registerEvents(this.pl, this);
     this.cmdHandler.setupCommands();
     this.config.doConfig();
     this.config.doArena();
     
   }
   
   public void onDisable()
   {
     Bukkit.getServer().getScheduler().cancelTasks(this);
     this.gameHandler.cleanUpGame();
     this.config.saveCustomConfig();
     this.config.saveArenaConfig();
     log.info(this.logPrefix + "disabled.");
   }
   
 
   public static enum areaMode
   {
     LOBBY_1,  LOBBY_2,  ARENA_1,  ARENA_2,  NONE;
     
     private areaMode() {}
   }
 }








