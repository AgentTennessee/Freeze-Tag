package me.dimensio.ftx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config
  
{
  private final FreezeTag plugin;
  public FileConfiguration customConfig = null;
  public File customConfigFile = null;
  public FileConfiguration customConfig2 = null;
  public File customConfigFile2 = null;
  public boolean verbose = false;
  public int item;
  public int defaultTime;
  public boolean lobby;
  public String lobby_world;
  public String lobby_area1;
  public String lobby_area2;
  public boolean arena;
  public String arena_world;
  public String arena_area1;
  public String arena_area2;
  public int numOfSpawns;
  public List<String> spawns;
  public boolean autostart;
  
  public Config(FreezeTag instance)
  {
    this.plugin = instance;
  }
  
  public void doConfig()
  {
    getCustomConfig().options().copyDefaults(true);
    saveCustomConfig();
    this.item = this.customConfig.getInt("listItem");
    this.defaultTime = this.customConfig.getInt("defaultTimeLimit");
  }
  
  public void doArena()
  {
    getArenaConfig().options().copyDefaults(true);
    saveArenaConfig();
    
    this.arena = this.customConfig2.getBoolean("arena.area.defined");
    this.lobby = this.customConfig2.getBoolean("arena.lobby.defined");
    this.numOfSpawns = this.customConfig2.getInt("arena.spawns.amount");
    if (this.arena)
    {
      this.arena_world = this.customConfig2.getString("arena.area.world");
      this.arena_area1 = this.customConfig2.getString("arena.area.p1");
      this.arena_area2 = this.customConfig2.getString("arena.area.p2");
      if ((this.arena_world == null) || (this.arena_area1 == null) || (this.arena_area2 == null)) {
        this.arena = false;
      }
    }
    if (this.lobby)
    {
      this.lobby_world = this.customConfig2.getString("arena.lobby.world");
      this.lobby_area1 = this.customConfig2.getString("arena.lobby.p1");
      this.lobby_area2 = this.customConfig2.getString("arena.lobby.p2");
      if ((this.lobby_world == null) || (this.lobby_area1 == null) || (this.lobby_area2 == null)) {
        this.lobby = false;
      }
    }
    if (this.numOfSpawns > 0)
    {
      this.spawns = this.customConfig2.getStringList("arena.spawns");
      this.spawns.remove("amount");
      this.numOfSpawns = this.spawns.size();
    }
  }
  
  public boolean saveLobby()
  {
    if ((this.lobby_area1 == null) || (this.lobby_area2 == null) || (this.lobby_world == null)) {
      return false;
    }
    this.customConfig2.set("arena.lobby.defined", Boolean.valueOf(this.lobby));
    this.customConfig2.set("arena.lobby.world", this.lobby_world);
    this.customConfig2.set("arena.lobby.p1", this.lobby_area1);
    this.customConfig2.set("arena.lobby.p2", this.lobby_area2);
    return true;
  }
  
  public boolean saveArena()
  {
    if ((this.arena_area1 == null) || (this.arena_area2 == null) || (this.arena_world == null)) {
      return false;
    }
    this.customConfig2.set("arena.area.defined", Boolean.valueOf(this.arena));
    this.customConfig2.set("arena.area.world", this.arena_world);
    this.customConfig2.set("arena.area.p1", this.arena_area1);
    this.customConfig2.set("arena.area.p2", this.arena_area2);
    return true;
  }
  
  public void clearArena()
  {
    if (new File(this.plugin.getDataFolder(), "arena.yml").exists())
    {
      this.customConfig2.set("arena.area.defined", Boolean.valueOf(false));
      this.customConfig2.set("arena.area.world", Integer.valueOf(0));
      this.customConfig2.set("arena.area.p1", Integer.valueOf(0));
      this.customConfig2.set("arena.area.p2", Integer.valueOf(0));
      this.arena = false;
    }
  }
  
  public void reloadCustomConfig()
  {
    if (this.customConfigFile == null) {
      this.customConfigFile = new File(this.plugin.getDataFolder(), "config.yml");
    }
    this.customConfig = YamlConfiguration.loadConfiguration(this.customConfigFile);
    
    InputStream defConfigStream = this.plugin.getResource("config.yml");
    if (defConfigStream != null)
    {
      YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
      this.customConfig.setDefaults(defConfig);
    }
  }
  
  public void reloadArenaConfig()
  {
    if (this.customConfigFile2 == null) {
      this.customConfigFile2 = new File(this.plugin.getDataFolder(), "arena.yml");
    }
    this.customConfig2 = YamlConfiguration.loadConfiguration(this.customConfigFile2);
    
    InputStream defConfigStream = this.plugin.getResource("arena.yml");
    if (defConfigStream != null)
    {
      YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
      this.customConfig2.setDefaults(defConfig);
    }
  }
  
  public FileConfiguration getCustomConfig()
  {
    if (this.customConfig == null) {
      reloadCustomConfig();
    }
    return this.customConfig;
  }
  
  public FileConfiguration getArenaConfig()
  {
    if (this.customConfig2 == null) {
      reloadArenaConfig();
    }
    return this.customConfig2;
  }
  
  public void saveCustomConfig()
  {
    if ((this.customConfig == null) || (this.customConfigFile == null)) {
      return;
    }
    try
    {
      getCustomConfig().save(this.customConfigFile);
    }
    catch (IOException ex)
    {
      plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.customConfigFile, ex);
    }
  }
  
  public void saveArenaConfig()
  {
    if ((this.customConfig2 == null) || (this.customConfigFile2 == null)) {
      return;
    }
    try
    {
      getArenaConfig().save(this.customConfigFile2);
    }
    catch (IOException ex)
    {
      plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.customConfigFile2, ex);
    }
  }
}









