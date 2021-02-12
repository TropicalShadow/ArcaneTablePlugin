package me.tropicalshadow.arcanetable.listener;

import me.tropicalshadow.arcanetable.ArcaneTable;
import me.tropicalshadow.arcanetable.gui.TableGui;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener implements Listener {


    @EventHandler()
    public void onClickEnchantingTable(PlayerInteractEvent event){
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if(event.getClickedBlock()!=null && event.getClickedBlock().getType().equals(ArcaneTable.getEnchantingTableMaterial())){
                event.setCancelled(true);
                TableGui gui = new TableGui();
                gui.setEnchantmentTable(event.getClickedBlock());
                gui.show(event.getPlayer());
            }
        }
    }
    //DONE - on block break close all inventories that are assosiated with it
    //TODO - TEST IF THIS WORKS!!
    @EventHandler()
    public void onBreakingOfEnchantmentTable(BlockBreakEvent event){
        if(!event.getBlock().getType().equals(ArcaneTable.ETABLEMATERIAL))return;
        Block eTable = event.getBlock();
        TableGui.GUI_INVETORIES.forEach((inv,gui)->{
            if(gui instanceof TableGui){
                TableGui tGui = ((TableGui) gui);
                if(tGui.getBlock()!=null){
                    if(tGui.getBlock().equals(eTable)){
                        ((TableGui) gui).closeInventorySafely(inv);
                    }
                }
            }
        });
    }
}
