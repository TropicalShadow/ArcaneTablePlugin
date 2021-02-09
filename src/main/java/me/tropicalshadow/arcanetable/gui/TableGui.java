package me.tropicalshadow.arcanetable.gui;


import me.tropicalshadow.arcanetable.utils.EnchantmentUtils;
import me.tropicalshadow.arcanetable.utils.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
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

public class TableGui extends BaseGui{

    public TableGui(){
        super("Arcane Table",6);
        this.setOnClick(this::clickInventoryEvent);
        this.setOnClose(this::closeInventoryEvent);
        this.setOnOpen(this::openInventoryEvent);
    }
    
    public ItemStack getCurrentItem(){
        return getInventory().getItem((9*2)+1);
    }
    public void setEnchantingItem(Inventory inv, ItemStack item){
        inv.setItem((9*2)+1,item);
    }


    public void clickInventoryEvent(InventoryClickEvent event){
        BaseGui gui = BaseGui.getGui(event.getInventory());
        if(gui != null){
            if(event.getInventory()==event.getClickedInventory()){
                Player player = (Player)event.getWhoClicked();
                event.setCancelled(true);
                int slot = event.getSlot();
                ItemStack tempItem = getCurrentItem();
                if(slot==(9*2)+1&&tempItem!=null){
                    giveItemFromSlot(player,event.getInventory(), tempItem);
                    updateInventoryWithEnchantments(event.getInventory(),null,true);
                }else if(tempItem != null){
                    ItemStack clicked = event.getCurrentItem();
                    if(clicked == null || (!clicked.getType().equals(Material.NETHER_STAR) && !clicked.getType().equals(Material.KNOWLEDGE_BOOK) && !clicked.getType().equals(Material.ENCHANTED_BOOK))){
                        return;
                    }
                    if(clicked.getType().equals(Material.NETHER_STAR)){
                        ItemStack firstBook = event.getInventory().getItem(12);
                        if(firstBook!=null && firstBook.getType().equals(Material.ENCHANTED_BOOK)) {
                            updateInventoryWithEnchantments(event.getInventory(), getCurrentItem(), true);
                            return;
                        }
                            giveItemFromSlot(player,event.getInventory(), tempItem);
                            event.getWhoClicked().closeInventory();

                    }else if(clicked.getType().equals(Material.KNOWLEDGE_BOOK)){
                        updateInventoryWithEnchantments(event.getInventory(),clicked,false);
                    }else{
                        //Enchantment book
                        Enchantment ench = new ArrayList<>(clicked.getEnchantments().keySet()).get(0);
                        int level = clicked.getEnchantments().get(ench);
                        int cost = EnchantmentUtils.getEnchantmentCost(ench,level);
                        if(cost>player.getExpToLevel()){
                            return;
                        }
                        if(EnchantmentUtils.doesItemAlreadyHasEnchant(tempItem,ench,level)){
                            tempItem.removeEnchantment(ench);
                            updateInventoryWithEnchantments(event.getInventory(),tempItem,true);
                            return;
                        }
                        player.setLevel(player.getLevel()-cost);
                        ItemStack newItem = EnchantmentUtils.applyEnchantToItem(tempItem,ench,level,false,true);
                        updateInventoryWithEnchantments(event.getInventory(),newItem,true);
                    }
                }else if(event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.NETHER_STAR)){
                    event.getWhoClicked().closeInventory();
                }
            }else{
                event.setCancelled(true);
                ItemStack item = getCurrentItem();
                if(item==null){
                    ItemStack tempItem = event.getCurrentItem();
                    setEnchantingItem(event.getInventory(),tempItem);
                    event.setCurrentItem(null);
                    updateInventoryWithEnchantments(event.getInventory(),tempItem, true);
                }
            }
        }
    }

    private void giveItemFromSlot(Player player, Inventory inv, ItemStack tempItem) {
        setEnchantingItem(inv,null);
        Map<Integer,ItemStack> leftOvers = player.getInventory().addItem(tempItem);
        leftOvers.forEach((index,item)-> player.getWorld().dropItem(player.getLocation(),item));
    }

    public void updateInventoryWithEnchantments(Inventory inv,ItemStack unique,boolean isFake){
        if(unique==null){
            for(int x = 0; x < 5; x++ ){
                for (int y = 0; y < 4; y++){
                    inv.setItem(12+((y*9)+x),new ItemBuilder().setMaterial(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
                }
            }
            return;
        }
        int index = 0;

        if(isFake) {
            ArrayList<Enchantment> enchs = EnchantmentUtils.getCanEnchants(unique);
            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 5; x++) {
                    if (index >= enchs.size()) {
                        inv.setItem(12 + ((y * 9) + x), new ItemBuilder().setMaterial(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
                        continue;
                    }
                    Enchantment ench = enchs.get(index);
                    boolean isConflict = EnchantmentUtils.findConflictingEnchantments(unique,ench).size()>=1;
                    //Logging.info(isConflict + " : "+EnchantmentUtils.findConflictingEnchantments(unique,ench).size()+" : ");
                    inv.setItem(12 + ((y * 9) + x), new ItemBuilder().setMaterial(Material.KNOWLEDGE_BOOK)
                            .setName(EnchantmentUtils.getEnchantmentName(ench))
                            .setIgnoreLevelRestriction(true)
                            .addLore(isConflict ? "&4Enchantment conflicts with current enchants" : null)
                            .addEnchantment(ench, 1)
                            .build());
                    index++;
                }
            }
        }else{
            Enchantment ench = new ArrayList<>(unique.getEnchantments().keySet()).get(0);
            int maxLevel = ench.getMaxLevel();
            int minLevel = ench.getStartLevel();
            //GET COST OF ENCHANT & ITS MULTIPLIER
            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 5; x++) {
                    if (index >= maxLevel) {
                        inv.setItem(12 + ((y * 9) + x), new ItemBuilder().setMaterial(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
                        continue;
                    }
                    int cost = EnchantmentUtils.EnchantmentCosts.getFromEnchant(ench).getCost(index+minLevel);
                    boolean alreadyHas = EnchantmentUtils.doesItemAlreadyHasEnchant(getCurrentItem(),ench,index+minLevel);
                    boolean isConflict = EnchantmentUtils.findConflictingEnchantments(getCurrentItem(),ench).size()>=1;
                    boolean hasHigher = EnchantmentUtils.itemHasHigherEnchantmentLevel(getCurrentItem(),ench,index+minLevel);
                    inv.setItem(12 + ((y * 9) + x), new ItemBuilder()
                            .setMaterial(Material.ENCHANTED_BOOK)
                            .setName(EnchantmentUtils.getEnchantmentName(ench))
                            .setIgnoreLevelRestriction(true)
                            .addEnchantment(ench, index+minLevel)
                            .addLore((cost>((Player)inv.getViewers().get(0)).getLevel() ? "&4" : "&b") + ("Cost: "+ cost))
                            .addLore((alreadyHas || isConflict || hasHigher) ? " " : null)
                            .addLore(alreadyHas ? "&4Item already has this enchantment" : null)
                            .addLore(alreadyHas ? "&4Clicking will remove this enchant" : null)
                            .addLore((alreadyHas && (isConflict || hasHigher)) ? " " : null)
                            .addLore(isConflict ? "&4Enchantment conflicts with current enchants" : null)
                            .addLore(isConflict ? "&4Clicking will remove conflicting enchants" : null)
                            .addLore((isConflict && hasHigher) ? " " : null)
                            .addLore(hasHigher ? "&4Item already has higher level" : null)
                            .addLore(hasHigher ? "&4of this enchantment" : null)
                            .build());
                    index ++;
                }
            }
        }
    }

    public void closeInventoryEvent(InventoryCloseEvent event){
        ItemStack item = event.getInventory().getItem((9*2)+1);
        if(item==null)return;
        Map<Integer,ItemStack> leftover = event.getPlayer().getInventory().addItem(item);
        Location loc = event.getPlayer().getLocation();
        leftover.forEach((count,i)-> Objects.requireNonNull(loc.getWorld()).dropItem(loc,i));
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
        inv.setItem((9*3)+1,new ItemBuilder().setMaterial(Material.ENCHANTING_TABLE).setName("&aPlace item above").addLore(ChatColor.WHITE+"Place item in slot above").addLore(ChatColor.WHITE+"to view enchantments").build());
        inv.setItem((9*5)+4,new ItemBuilder().setMaterial(Material.NETHER_STAR).setName("&aBack").addLore(ChatColor.WHITE+"Click to go back").build());
        inv.setItem((4 * 9)-1, new ItemBuilder()
                .setMaterial(Material.PLAYER_HEAD)
                .setName("&2Next Page")
                .setPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM1YThhYThhNGMwMzYwMGEyYjVhNGViNmJlYjUxZDU5MDI2MGIwOTVlZTFjZGFhOTc2YjA5YmRmZTU2NjFjNiJ9fX0==")
                .build());
        inv.setItem((5 * 9)-1, new ItemBuilder()
                .setMaterial(Material.PLAYER_HEAD)
                .setName("&2Prev Page")
                .setPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM1YThhYThhNGMwMzYwMGEyYjVhNGViNmJlYjUxZDU5MDI2MGIwOTVlZTFjZGFhOTc2YjA5YmRmZTU2NjFjNiJ9fX0==")
                .build());
        for(int x = 0; x < 5; x++ ){
            for (int y = 0; y < 4; y++){
                inv.setItem(12+((y*9)+x),new ItemBuilder().setMaterial(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
            }
        }

    }
}
