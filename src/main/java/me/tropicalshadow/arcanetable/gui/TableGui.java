package me.tropicalshadow.arcanetable.gui;


import me.tropicalshadow.arcanetable.utils.EnchantmentUtils;
import me.tropicalshadow.arcanetable.utils.ItemBuilder;
import me.tropicalshadow.arcanetable.utils.Logging;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class TableGui extends BaseGui{

    public TableGui(){
        super("Arcane Table",6);
        this.setOnClick(this::clickInventoryEvent);
        this.setOnClose(this::closeInventoryEvent);
        this.setOnOpen(this::openInventoryEvent);
    }
    
    public ItemStack getCurrentItem(Inventory inv){
        return inv.getItem((9*2)+1);
    }
    public void setEnchantingItem(Inventory inv, ItemStack item){
        inv.setItem((9*2)+1,item);
    }


    public void clickInventoryEvent(InventoryClickEvent event){
        BaseGui gui = BaseGui.getGui(event.getInventory());
        if(gui != null){
            if(event.getInventory()==event.getClickedInventory()){
                event.setCancelled(true);
                int slot = event.getSlot();
                ItemStack tempItem = getCurrentItem(event.getInventory());
                if(slot==(9*2)+1&&tempItem!=null){
                    Player player = (Player)event.getWhoClicked();
                    setEnchantingItem(event.getInventory(),null);
                    Map<Integer,ItemStack> leftOvers = player.getInventory().addItem(tempItem);
                    leftOvers.forEach((index,item)->{
                        player.getWorld().dropItem(player.getLocation(),item);
                    });
                    updateInventoryWithEnchantments(event.getInventory(),null);
                }
            }else{
                event.setCancelled(true);
                ItemStack item = getCurrentItem(event.getInventory());
                if(item==null){
                    ItemStack tempItem = event.getCurrentItem();
                    setEnchantingItem(event.getInventory(),tempItem);
                    event.setCurrentItem(null);
                    updateInventoryWithEnchantments(event.getInventory(),tempItem);
                }
            }
        }
    }

    public void updateInventoryWithEnchantments(Inventory inv,ItemStack unique){
        if(unique==null){
            for(int x = 0; x < 5; x++ ){
                for (int y = 0; y < 4; y++){
                    inv.setItem(12+((y*9)+x),new ItemBuilder().setMaterial(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
                }
            }
            return;
        }
        int index = 0;

        ArrayList<Enchantment> enchs = EnchantmentUtils.getCanEnchants(unique);
        for (int y = 0; y < 4; y++){
            for(int x = 0; x < 5; x++ ){
                if(index>=enchs.size()){
                    inv.setItem(12+((y*9)+x),new ItemBuilder().setMaterial(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
                    return;
                }
                Enchantment ench = enchs.get(index);
                inv.setItem(12+((y*9)+x),new ItemBuilder().setMaterial(Material.KNOWLEDGE_BOOK).setName(ench.getKey().getKey().toLowerCase(Locale.ROOT).replace("_"," ")).setIgnoreLevelRestriction(true).addEnchantment(ench,1).build());
                index++;
            }
        }
    }

    public void closeInventoryEvent(InventoryCloseEvent event){
        ItemStack item = event.getInventory().getItem((9*2)+1);
        if(item==null)return;
        Map<Integer,ItemStack> leftover = event.getPlayer().getInventory().addItem(item);
        Location loc = event.getPlayer().getLocation();
        leftover.forEach((count,i)->{
            Objects.requireNonNull(loc.getWorld()).dropItem(loc,i);
        });
    }
    public void openInventoryEvent(InventoryOpenEvent event){

    }

    @Override
    public void addToInventory(Inventory inv){
        for (int i = 0; i < this.getRows()*9; i++) {
            if(((9*2)+1)==i){
                continue;
            }
            inv.setItem(i,new ItemBuilder().setName(" ").setMaterial(Material.PURPLE_STAINED_GLASS_PANE).build());
        }
        inv.setItem((9*3)+1,new ItemBuilder().setMaterial(Material.ENCHANTING_TABLE).setName("&aPlace item above").addLore("&8Place item in slot above").addLore("&8to view enchantments").build());

        for(int x = 0; x < 5; x++ ){
            for (int y = 0; y < 4; y++){
                inv.setItem(12+((y*9)+x),new ItemBuilder().setMaterial(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
            }
        }
    }
}
