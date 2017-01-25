package net.minikloon;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class ChangedBlock {
    public final Location loc;
    public final MaterialData mat;

    public ChangedBlock(Location loc, MaterialData mat) {
        this.loc = loc;
        this.mat = mat;
    }
    
    public void revert() {
        loc.getBlock().setTypeIdAndData(mat.getItemTypeId(), mat.getData(), false);
    }
    
    public static List<ChangedBlock> snapshot(List<Block> blocks) {
        return new ArrayList<>(Lists.transform(blocks, 
                b -> new ChangedBlock(b.getLocation(), new MaterialData(b.getType(), b.getData()))
        )); // wrap in arraylist because Lists.transform is lazy -- see its doc
    }
}