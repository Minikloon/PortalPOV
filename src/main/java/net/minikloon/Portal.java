package net.minikloon;

import com.google.common.base.Suppliers;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;
import java.util.function.Supplier;

public class Portal {
    private final Collection<ChangedBlock> changed;
    private final Location pillar1;
    private final Location pillar2;

    private Portal(Collection<ChangedBlock> changed, Location pillar1, Location pillar2) {
        this.changed = changed;
        this.pillar1 = pillar1;
        this.pillar2 = pillar2;
    }

    public static Portal spawn(Location center) {
        int sideSize = 5;
        
        Location pillar1 = center.clone().subtract(2, 0, 0);
        Location pillar2 = pillar1.clone().add(sideSize, 0, 0);
        
        Block bottomLeft = center.clone().subtract(2, 0, 0).getBlock();
        List<Block> blocks = new ArrayList<>(sideSize * 3);
        
        for (int i = 0; i < sideSize; ++i) {
            blocks.add(bottomLeft.getRelative(i, 0, 0));
            blocks.add(bottomLeft.getRelative(0, i, 0));
            blocks.add(bottomLeft.getRelative(sideSize, i, 0));
        }
        
        Collection<ChangedBlock> changed = ChangedBlock.snapshot(blocks);
        
        blocks.forEach(b -> {
            b.setType(Material.OBSIDIAN);
        });
        
        
        
        return new Portal(changed, pillar1, pillar2);
    }
    
    public void despawn() {
        changed.forEach(ChangedBlock::revert);
    }
    
    public Location getPillar1() {
        Location diff = pillar2.clone().subtract(pillar1);
        return pillar1.clone().add(diff.toVector().normalize().multiply(0.5));
    }

    public Location getPillar2() {
        Location diff = pillar2.clone().subtract(pillar1);
        return pillar2.clone().subtract(diff.toVector().normalize().multiply(0.5));
    }
}
