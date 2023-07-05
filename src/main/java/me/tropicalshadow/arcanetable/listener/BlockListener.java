package me.tropicalshadow.arcanetable.listener;

import me.tropicalshadow.arcanetable.ArcaneTable;
import me.tropicalshadow.arcanetable.gui.TableGui;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener implements Listener {


    @EventHandler()
    public void onClickEnchantingTable(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null && clickedBlock.getType() == ArcaneTable.getEnchantingTableMaterial()) {
                event.setCancelled(true);
                TableGui gui = new TableGui();
                gui.setEnchantmentTable(clickedBlock);
                gui.show(event.getPlayer());
            }
        }
    }

    @EventHandler()
    public void onBreakingOfEnchantmentTable(BlockBreakEvent event){
        Block block = event.getBlock();
        if (block.getType() != ArcaneTable.getEnchantingTableMaterial()) {
            return;
        }

        Bukkit.getScheduler().runTask(ArcaneTable.getPlugin(),()-> TableGui.GUI_INVETORIES.forEach((inv, gui)->{
            if(gui instanceof TableGui tableGui){
                if(tableGui.getBlock() != null && tableGui.getBlock().equals(block)){
                    tableGui.closeInventorySafely(inv);
                }
            }
        }));
    }
}
