 package me.dimensio.ftx;
 
 import org.bukkit.ChatColor;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.EventPriority;
 import org.bukkit.event.Listener;
 import org.bukkit.event.entity.EntityDamageByEntityEvent;
 import org.bukkit.event.entity.EntityDamageEvent;
 
 public class EntityListener
   implements Listener
 {
   private final FreezeTag plugin;
   private final GameHandler gameHandler;
   public String PREFIX = ChatColor.DARK_GREEN + "[FreezeTagX] ";
   public String ERR_PREFIX = ChatColor.RED + "[FreezeTagX] ";
   
   public EntityListener(FreezeTag instance, GameHandler game)
   {
     this.plugin = instance;
     this.gameHandler = game;
   }
   
   @EventHandler(priority=EventPriority.HIGHEST)
   public void onEntityDamage(EntityDamageEvent event)
   {
     if (!this.plugin.inGame) {
       return;
     }
     if ((event instanceof EntityDamageByEntityEvent))
     {
       EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)event;
       if ((!(edbee.getDamager() instanceof Player)) || (!(edbee.getEntity() instanceof Player))) {
         return;
       }
       Player damager = (Player)edbee.getDamager();
       Player damagee = (Player)edbee.getEntity();
       if ((!this.plugin.players.containsKey(damager)) || (!this.plugin.players.containsKey(damagee))) {
         return;
       }
       if ((((String)this.plugin.players.get(damager)).equalsIgnoreCase("Chaser")) && (((String)this.plugin.players.get(damagee)).equalsIgnoreCase("Regular")))
       {
         edbee.setCancelled(true);
         this.plugin.players.put(damagee, "FROZEN");
         damagee.sendMessage(this.PREFIX + "You've been frozen by " + ChatColor.YELLOW + damager.getName() + ChatColor.DARK_GREEN + "! Wait for somebody else to tag you, to be unfrozen.");
         damager.sendMessage(this.PREFIX + "You've frozen " + ChatColor.YELLOW + damagee.getName() + ChatColor.DARK_GREEN + "!");
         this.plugin.numOfFrozen += 1;
         if (this.gameHandler.checkVictory()) {
           this.gameHandler.victory();
         }
         return;
       }
       if (((String)this.plugin.players.get(damagee)).equalsIgnoreCase("Chaser")) {
         edbee.setCancelled(true);
       }
       if ((((String)this.plugin.players.get(damager)).equalsIgnoreCase("Regular")) && (((String)this.plugin.players.get(damagee)).equalsIgnoreCase("FROZEN")))
       {
         edbee.setCancelled(true);
         this.plugin.players.put(damagee, "Regular");
         damagee.sendMessage(this.PREFIX + "You've been un-frozen by " + ChatColor.YELLOW + damager.getName() + ChatColor.DARK_GREEN + "!");
         damager.sendMessage(this.PREFIX + "You've un-frozen " + ChatColor.YELLOW + damagee.getName() + ChatColor.DARK_GREEN + "!");
         damagee.setHealth(20D);
         damagee.setFoodLevel(20);
         this.plugin.numOfFrozen -= 1;
       }
     }
   }
 }









