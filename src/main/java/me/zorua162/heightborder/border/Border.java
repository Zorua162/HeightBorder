package me.zorua162.heightborder.border;


import jdk.internal.icu.text.UnicodeSet;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Logger;

import static org.bukkit.util.NumberConversions.round;

@SerializableAs("Border")
public class Border implements ConfigurationSerializable {
    // Manager for getting constants such as the colorMap
    BorderManager borderManager;
    // Things that need to be stored
    double currentHeight;
    // Height at which border stops moving
    double endHeight;

    // top or bottom, stored as "up" or "bottom" in direction
    String direction;
    // velocity
    double velocity = 0;
    // front left and back right position: flpos brpos
    Location flpos;
    Location brpos;
    // Particle colour (red default, but this will be changed if it isn't moving)
    Color particleColour = Color.fromRGB(255, 0, 0);
    // Particle colours change from one to the other, gives a more varied colour to the world border
    String type;
    Logger log;
    Map<String, Color> colorMap = new HashMap<>();
    // Current damage tick, when this is equal to damage pause any player outside border is damaged
    int damageTick = 0;
    int damagePause = 20;

    int previousBreakLayer = (int) currentHeight;

    public Border(double currentHeight, double endHeight, String direction, double velocity, Location flpos, Location brpos, String type) {
        this.currentHeight = currentHeight;
        this.endHeight = endHeight;
        this.direction = direction;
        this.velocity = velocity;
        this.flpos = flpos;
        this.brpos = brpos;
        this.type = type;
        this.log = Bukkit.getLogger();
        putMapColours();
    }

    public void putMapColours() {
        colorMap.put("moving", Color.fromRGB(255, 0, 0));
        // colorMap.put("green", Color.fromRGB(0, 255, 0));
        colorMap.put("stopped", Color.fromRGB(0, 0, 255));
    }

    public Map<String, Object> serialize() {
        LinkedHashMap result = new LinkedHashMap();
        result.put("currentheight", Double.valueOf(this.getCurrentHeight()));
        result.put("endheight", Double.valueOf(this.getEndHeight()));
        result.put("direction", String.valueOf(this.getDirection()));
        result.put("velocity", Double.valueOf(this.getVelocity()));
        result.put("flpos", this.getFLPos());
        result.put("brpos", this.getBRPos());
        return result;
    }

    private double getEndHeight() {
        return endHeight;
    }

    private double getCurrentHeight() {
        return currentHeight;
    }

    private Location getBRPos() {
        return this.brpos;
    }

    private Location getFLPos() {
        return this.flpos;
    }

    private String getDirection() {
        return this.direction;
    }

    private Double getVelocity() {
        return this.velocity;
    }

    public static Border deserialize(Map<String, Object> args) {
        // Some default deseralize values so that errors aren't thrown, could cause issues in future.
        double currentHeight = 256;
        double endHeight = 128;
        String direction = "down";
        double velocity = 0;
        Location flpos = null;
        Location brpos = null;
        String type = "damage";

        if (args.containsKey("currentheight")) {
            currentHeight = ((Double) args.get("currentheight")).doubleValue();
        }

        if (args.containsKey("endheight")) {
            endHeight = ((Double) args.get("endheight")).doubleValue();
        }

        if(args.containsKey("direction")) {
            direction = ((String)args.get("direction")).toString();
        }

        if(args.containsKey("velocity")) {
            velocity = ((Double)args.get("velocity")).doubleValue();
        }

        if(args.containsKey("flpos")) {
            flpos = ((Location)args.get("flpos"));
        }

        if(args.containsKey("brpos")) {
            brpos = ((Location)args.get("brpos"));
        }

        if(args.containsKey("type")) {
            type = ((String)args.get("type"));
        }
        return new Border(currentHeight, endHeight, direction, velocity, flpos, brpos, type);
    }

    public String getListInfo() {
        String outData = "y = " + currentHeight + "\nend height = " + endHeight + "\ndirection = " + direction ;
        outData = outData + "\nvelocity = " + velocity + "\nflpos = " + flpos.toString() + "\nbrpos = " + brpos.toString();
        outData = outData + "\nparticleColour = " + particleColour.toString() + "\ntype = " + type;
        return outData;
    }

    private int getMax(int n1, int n2) {
        if (n1 > n2) {
            return n1;
        } else {
            return n2;
        }
    }

    private int getMin(int n1, int n2) {
        if (n1 < n2) {
            return n1;
        } else {
            return n2;
        }
    }

    private List<Integer> getBorders(){
        List<Integer> outList;
        // Get positions that make up corner of the border to be shown
        int x1 = flpos.getBlockX();
        int x2 = brpos.getBlockX();
        int z1 = flpos.getBlockZ();
        int z2 = brpos.getBlockZ();
        // set up for for loop
        int startx = getMin(x1, x2);
        int endx = getMax(x1, x2);
        int startz = getMin(z1, z2);
        int endz = getMax(z1, z2);
        outList = Arrays.asList(startx, endx, startz, endz);
        return outList;
    }

    public void displayBorder() {

        // get start and end position for the for nested 2d for loops that loop over the border
        List<Integer> borders = getBorders();
        int startx = borders.get(0);
        int endx = borders.get(1);
        int startz = borders.get(2);
        int endz = borders.get(3);

        // do not display if lower then y = -200, as this would be unecessary
        if (currentHeight < -200) {
            return;
        }

        World world = flpos.getWorld();
        // Only this set number of particles is created to reduce client lag
        int numberOfParticles = 100;
        // Scale number of particles to the required size
        int stepx = (endx-startx)/round(Math.sqrt(numberOfParticles));
        int stepz = (endz-startz)/round(Math.sqrt(numberOfParticles));
        //
        for (int x=startx; x < endx + stepx; x = x + stepx) {
           for (int z=startz; z < endz + stepz; z = z + stepz) {
               Location currentLoc = new Location(world, x, currentHeight, z);
               // Only the same colour is used, because in testing this significantly reduced client side lag
               // Particle.DustTransition dustOptions = new Particle.DustTransition(Color.fromRGB(255, 0, 0), Color.fromRGB(255, 0, 0), 10.0F);
               // Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 10.0F);
               Particle.DustOptions dustOptions = new Particle.DustOptions(particleColour, 10.0F);
               world.spawnParticle(Particle.REDSTONE , currentLoc, 1, 0, 0, 0,
                       1, dustOptions, true);

           }
        }
    }

    public void moveBorder() {
        // Velocity is given in blocks/minute
        // this function is called every tick
        // conversion of block/min to amount/tick
        // ticks in a second = 20
        // so it should move the number of blocks/s/20 every tick or blocks/min/20/60

        if (direction.equals("down")){
            // Check if reached final height
            if (currentHeight <= endHeight) {
                // final height reached
                currentHeight = endHeight;
                particleColour = colorMap.get("stopped");
                return;
            }
            particleColour = colorMap.get("moving");
            currentHeight = currentHeight - velocity/20/60;
        } else {
            if (currentHeight >= endHeight) {
                // final height reached
                currentHeight = endHeight;
                particleColour = colorMap.get("stopped");
                return;
            }
            currentHeight = currentHeight + velocity/20/60;
        }

        // Set particle colour to blue if the border isn't moving
        if (velocity == 0) {
            particleColour = colorMap.get("stopped");
        } else {
            particleColour = colorMap.get("moving");
        }
    }

    public void doDamage() {
        // check if border should do damage
        if (!type.equals("damage")){
            return;
        }
        // get players in border's world and damage if outside of it
        List<Player> players = flpos.getWorld().getPlayers();
        damageTick = damageTick + 1;
        if (damageTick == damagePause){
            damageTick = 0;
            for (Player player: players) {
                if (direction.equals("down")) {
                    if (player.getLocation().getY() + 1 > currentHeight) {
                        player.damage(0.5);
                    }
                } else {
                    if (player.getLocation().getY() - 1 < currentHeight) {
                        player.damage(0.5);
                    }
                }
            }
        }
    }

    public void breakBlocks() {
        if (!type.equals("break")){
            return;
        }
        World world = flpos.getWorld();
        List<Integer> borders = getBorders();
        int startx = borders.get(0);
        int endx = borders.get(1);
        int startz = borders.get(2);
        int endz = borders.get(3);
        for (int x=startx-1; x<=endx; x++){
            for (int z=startz-1; z<=endz; z++) {
                Block block = world.getBlockAt(x, (int) currentHeight, z);
                block.setType(Material.AIR);
            }
        }

        // irrespective of direction, all the blocks at the borders current y level should be broken by the time
        // time it has passed through them
        // Calculate time to pass through a block
        // velocity is in blocks per minute
        // velocity/60 = bps
        if ((int) currentHeight != previousBreakLayer) {
            log.info("start breaking current = " + (int) currentHeight + "previous = " +  previousBreakLayer);
            previousBreakLayer = (int) currentHeight;
        }
    }
}