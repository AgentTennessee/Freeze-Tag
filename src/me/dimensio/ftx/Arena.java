 package me.dimensio.ftx;
 
 import java.util.Random;
 import org.bukkit.DyeColor;
 import org.bukkit.Location;
 import org.bukkit.Material;
 import org.bukkit.World;
 import org.bukkit.block.Block;
 import org.bukkit.block.BlockState;
 import org.bukkit.material.Wool;
 
 public abstract class Arena
 {
   public static Arena build(Location center, int xSize, int zSize, int height, Random random)
   {
     return new Internal(center, xSize, zSize, height, random);
   }
   
   public static boolean isWithin(int[] min, int[] max, Block block)
   {
     return isWithin(min, max, new int[] { block.getX(), block.getY(), block.getZ() });
   }
   
   public static boolean isWithin(int[] min, int[] max, int[] point)
   {
     for (int i = 0; i < 3; i++)
     {
       if (min[i] > point[i]) {
         return false;
       }
       if (max[i] < point[i]) {
         return false;
       }
     }
     return true;
   }
   
   public static int[][] parseMinMax(String[] point1, String[] point2)
     throws NumberFormatException
   {
     int[][] minmax = new int[2][3];
     for (int i = 0; i < 3; i++)
     {
       int coord1 = Integer.parseInt(point1[i]);
       int coord2 = Integer.parseInt(point2[i]);
       minmax[0][i] = Math.min(coord1, coord2);
       minmax[1][i] = Math.max(coord1, coord2);
     }
     return minmax;
   }
   
   public abstract boolean isWithin(Block paramBlock);
   
   public abstract boolean isWithin(int[] paramArrayOfInt);
   
   public abstract String[] getBounds();
   
   private static class Internal
     extends Arena
   {
     private final BlockState[] blocks;
     private final int[] min;
     private final int[] max;
     private final World world;
     
     Internal(Location center, int xSize, int zSize, int height, Random random)
     {
       this.blocks = new BlockState[(xSize + 1) * (zSize + 1) * 4 * (height + 1)];
       this.world = center.getWorld();
       this.min = new int[] { center.getBlockX() - xSize, center.getBlockY(), center.getBlockZ() - zSize };
       
 
 
 
       this.max = new int[] { center.getBlockX() + xSize, center.getBlockY() + height, center.getBlockZ() + zSize };
       
 
 
 
       generate(this.world, random, this.min[0], this.max[0], this.min[1], this.max[1], this.min[2], this.max[2]);
     }
     
     private void generate(World world, Random random, int startX, int endX, int startY, int endY, int startZ, int endZ)
     {
       int zs = endY - startY + 1;
       int xs = (endZ - startZ + 1) * zs;
       for (int x = startX; x <= endX; x++) {
         for (int y = startY; y <= endY; y++) {
           for (int z = startZ; z <= endZ; z++) {
             this.blocks[((x - startX) * xs + (z - startZ) * zs + (y - startY))] = world.getBlockAt(x, y, z).getState();
           }
         }
       }
       for (int x = startX; x <= endX; x++) {
         for (int y = startY; y <= endY; y++) {
           for (int z = startZ; z <= endZ; z++) {
             world.getBlockAt(x, y, z).setType(Material.AIR);
           }
         }
       }
       for (int y = startY; y <= endY; y++)
       {
         for (int x = startX; x <= endX; x++)
         {
           world.getBlockAt(x, y, startZ).setType(Material.WOOL);
           BlockState state = world.getBlockAt(x, y, startZ).getState();
           if ((state.getData() instanceof Wool))
           {
             ((Wool)state.getData()).setColor(DyeColor.LIME);
             state.update();
           }
           world.getBlockAt(x, y, endZ).setType(Material.WOOL);
           state = world.getBlockAt(x, y, endZ).getState();
           if ((state.getData() instanceof Wool))
           {
             ((Wool)state.getData()).setColor(DyeColor.LIME);
             state.update();
           }
         }
         for (int z = startZ; z <= endZ; z++)
         {
           world.getBlockAt(startX, y, z).setType(Material.WOOL);
           BlockState state = world.getBlockAt(startX, y, z).getState();
           if ((state.getData() instanceof Wool))
           {
             ((Wool)state.getData()).setColor(DyeColor.LIME);
             state.update();
           }
           world.getBlockAt(endX, y, z).setType(Material.WOOL);
           state = world.getBlockAt(endX, y, z).getState();
           if ((state.getData() instanceof Wool))
           {
             ((Wool)state.getData()).setColor(DyeColor.LIME);
             state.update();
           }
         }
       }
       for (int x = startX; x <= endX; x++) {
         for (int z = startZ; z <= endZ; z++) {
           world.getBlockAt(x, endY, z).setType(Material.GLASS);
         }
       }
       for (int x = startX; x <= endX; x++) {
         for (int z = startZ; z <= endZ; z++)
         {
           world.getBlockAt(x, startY, z).setType(Material.WOOL);
           BlockState state = world.getBlockAt(x, startY, z).getState();
           if ((state.getData() instanceof Wool))
           {
             ((Wool)state.getData()).setColor(DyeColor.PINK);
             state.update();
           }
         }
       }
       buildObstacles(world, random, startX + 1, endX - 1, startY + 1, endY - 1, startZ + 1, endZ - 1);
     }
     
     private void buildObstacles(World world, Random random, int startX, int endX, int startY, int endY, int startZ, int endZ)
     {
       int area = (endX - startX) * (endZ - startZ);
       for (int i = 0; i < area / 30; i++)
       {
         int x1 = random.nextInt(endX - startX + 1) + startX;
         int x2 = x1;
         int y1 = startY;
         int y2 = random.nextInt(4) + startY;
         int z1 = random.nextInt(endZ - startZ + 1) + startZ;
         int z2 = z1;
         if (random.nextBoolean())
         {
           x2 += random.nextInt(5) - random.nextInt(5);
           if (x2 < startX) {
             x2 = startX;
           }
           if (x2 > endX) {
             x2 = endX;
           }
         }
         else
         {
           z2 += random.nextInt(5) - random.nextInt(5);
           if (z2 < startZ) {
             z2 = startZ;
           }
           if (z2 > endZ) {
             z2 = endZ;
           }
         }
         for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
           for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
             for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
               if ((y != startY + 1) || (random.nextInt(8) != 0))
               {
                 Block b = world.getBlockAt(x, y, z);
                 b.setType(Material.WOOL);
                 BlockState state = b.getState();
                 if ((state.getData() instanceof Wool))
                 {
                   ((Wool)state.getData()).setColor(DyeColor.BLUE);
                   state.update();
                 }
               }
             }
           }
         }
       }
     }
     
     public boolean isWithin(Block block)
     {
       return (block.getWorld() == this.world) && (isWithin(this.min, this.max, block));
     }
     
     public boolean isWithin(int[] point)
     {
       return isWithin(this.min, this.max, point);
     }
     
     public String[] getBounds()
     {
       return new String[] { Integer.toString(this.min[0]) + ',' + Integer.toString(this.min[1]) + ',' + Integer.toString(this.min[2]), Integer.toString(this.max[0]) + ',' + Integer.toString(this.max[1]) + ',' + Integer.toString(this.max[2]) };
     }
   }
 }


