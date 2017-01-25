package net.minikloon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Consumer;

public class PortalViewPlugin extends JavaPlugin {
    private Portal portal;
    private Collection<ChangedBlock> flash;

    @Override
    public void onDisable() {
        if(portal != null)
            portal.despawn();
        if(flash != null)
            flash.forEach(ChangedBlock::revert);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(! (sender instanceof Player)) {
            sender.sendMessage("§cYou have to be a player to use this command!");
            return true;
        }
        Player player = (Player) sender;
        
        if(command.getName().equals("setportal")) {
            onSetPortal(player);
        } else if(command.getName().equals("flashportal")) {
            onFlashPortal(player);
        }
        
        return true;
    }
    
    private void onSetPortal(Player player) {
        if(portal != null)
            portal.despawn();
        portal = Portal.spawn(player.getLocation().subtract(0, 1, 0));
        player.sendMessage("§aPoof! A portal apears");
    }
    
    private void onFlashPortal(Player player) {
        if(portal == null) {
            player.sendMessage("§cFirst spawn a portal using /setportal");
            return;
        }
        
        if(flash != null) {
            flash.forEach(ChangedBlock::revert);
        }
        
        Set<Block> viewable = getViewableTopDown(player.getLocation(), portal.getPillar1(), portal.getPillar2(), 40.0);
        flash = ChangedBlock.snapshot(new ArrayList<>(viewable));
        
        viewable.forEach(b -> b.setType(Material.GOLD_BLOCK));
    }
    
    private Set<Block> getViewableTopDown(Location fromPov, Location limit1, Location limit2, double renderDistanceBeyondLimits) {
        Vector dir1 = limit1.clone().subtract(fromPov).toVector().normalize();
        Vector dir2 = limit2.clone().subtract(fromPov).toVector().normalize();
        
        Set<Block> blocks = new HashSet<>();
        
        for(double i = 1.0; i <= renderDistanceBeyondLimits; ++i) {
            Vector side1 = limit1.toVector().add(dir1.clone().multiply(i));
            Vector side2 = limit2.toVector().add(dir2.clone().multiply(i));
            forEachBetweenPoints(side1, side2, 0.5, point -> {
                Block block = fromPov.getWorld().getHighestBlockAt(point.getBlockX(), point.getBlockZ());
                blocks.add(block);
            });
        }
        return blocks;
    }
    
    private void forEachBetweenPoints(Vector a, Vector b, double stepLength, Consumer<Vector> consumer) {
        double distanceFromBSquared = a.distanceSquared(b);
        Vector step = b.clone().subtract(a).normalize().multiply(stepLength);
        Vector point = a.clone();
        while(true) {
            consumer.accept(point);
            point.add(step);
            
            double newDistance = point.distanceSquared(b);
            if(newDistance > distanceFromBSquared)
                break;
            distanceFromBSquared = newDistance;
        }
    }
}
