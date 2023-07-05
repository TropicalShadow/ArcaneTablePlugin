package me.tropicalshadow.arcanetable.listener;

import com.google.common.base.Preconditions;
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

import static org.bukkit.inventory.InventoryView.OUTSIDE;

public class GuiHook implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        BaseGui gui = BaseGui.getGui(event.getInventory());

        if (gui == null) {
            return;
        }

        if (!gui.isClickable) {
            event.setCancelled(true);
            return;
        }

        InventoryView view = event.getView();
        Inventory inventory = getClickedInventory(view, event.getRawSlot());
        if (inventory == null) {
            return;
        }

        gui.handleClick(event);
    }

    private Inventory getClickedInventory(InventoryView view, int rawSlot) {
        // Slot may be -1 if not properly detected due to client bug
        // e.g., dropping an item into part of the enchantment list section of an enchanting table
        if (rawSlot == OUTSIDE || rawSlot == -1) {
            return null;
        }

        Preconditions.checkArgument(rawSlot >= 0, "Negative, non outside slot: " + rawSlot);
        Preconditions.checkArgument(rawSlot < view.countSlots(), "Slot %s greater than inventory slot count: " + rawSlot);

        return rawSlot < view.getTopInventory().getSize() ? view.getTopInventory() : view.getBottomInventory();
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        BaseGui gui = BaseGui.getGui(event.getInventory());
        if (gui == null) {
            return;
        }

        if (gui.isUpdating()) {
            gui.handleClose(event);
        } else {
            Bukkit.getScheduler().runTask(ArcaneTable.getPlugin(), () -> {
                HumanEntity humanEntity = event.getPlayer();
                humanEntity.closeInventory();
            });
        }

        gui.handleClose(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        BaseGui gui = BaseGui.getGui(event.getInventory());
        if (gui == null) {
            return;
        }

        gui.handleOpen(event);
    }
}
