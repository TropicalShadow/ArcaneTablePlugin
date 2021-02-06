package me.tropicalshadow.arcanetable.listener;

import me.tropicalshadow.arcanetable.ArcaneTable;
import me.tropicalshadow.arcanetable.gui.TableGui;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener implements Listener {


    @EventHandler()
    public void onClickEnchantingTable(PlayerInteractEvent event){
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if(event.getClickedBlock()!=null && event.getClickedBlock().getType().equals(ArcaneTable.getEnchantingTableMaterial())){
                event.setCancelled(true);
                TableGui gui = new TableGui();
                gui.show(event.getPlayer());
            }
        }
    }
}
