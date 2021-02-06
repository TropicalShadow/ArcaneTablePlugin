package me.tropicalshadow.arcanetable.listener;

import me.tropicalshadow.arcanetable.ArcaneTable;
import me.tropicalshadow.arcanetable.gui.BaseGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.HashSet;
import java.util.Set;

public class GuiHook implements Listener {

    private final Set<BaseGui> activateGuiInstances = new HashSet<>();

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event){
        BaseGui gui = BaseGui.getGui(event.getInventory());

        if(gui == null){
            return;
        }
        //Logging.info(event.getWhoClicked().getName() +": clicked : "+gui.canClick);
        if(!gui.canClick){
            event.setCancelled(true);
            return;
        }
        InventoryView view = event.getView();
        Inventory inventory = view.getInventory(event.getRawSlot());
        if(inventory == null){
            //Clicked outside both inventories
            return;
        }
        gui.callOnClick(event);
        //detect bottom or top inv
    }
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event){
        BaseGui gui = BaseGui.getGui(event.getInventory());
        if(gui == null){
            return;
        }
        if(gui.isUpdating()){
            gui.callOnClose(event);
            if(gui.getViewerCount() == 1){
                activateGuiInstances.remove(gui);
            }
            return;
        }else{
            //Logging.info("Force Closing of GUI");
            Bukkit.getScheduler().runTask(ArcaneTable.getPlugin(),()->{
                HumanEntity humanEntity = event.getPlayer();
                humanEntity.closeInventory();
            });
        }
        gui.callOnClose(event);
    }
    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event){
        BaseGui gui = BaseGui.getGui(event.getInventory());
        if(gui == null){
            return;
        }
        gui.callOnOpen(event);
    }

}
