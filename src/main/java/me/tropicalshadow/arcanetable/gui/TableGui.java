package me.tropicalshadow.arcanetable.gui;

import me.tropicalshadow.arcanetable.ArcaneTable;
import me.tropicalshadow.arcanetable.objects.Paginator;
import me.tropicalshadow.arcanetable.utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TableGui extends BaseGui{

    public Paginator paginator;
    private Block block;

    public TableGui(){
        super("Arcane Table",6);
        this.setOnClick(this::clickInventoryEvent);
        this.setOnClose(this::closeInventoryEvent);
        this.setOnOpen(this::openInventoryEvent);
        try {
            paginator = new Paginator(Enchantment.class,20);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArcaneTable.getPlugin().reloadConfig();
    }

    public Block getBlock(){
        return this.block;
    }
    public void setEnchantmentTable(Block block){
        this.block = block;
    }
    public ItemStack getCurrentItem(){
        return getInventory().getItem((9*2)+1);
    }
    public void setEnchantingItem(Inventory inv, ItemStack item){
        inv.setItem((9*2)+1,item);
    }

    public void closeInventorySafely(Inventory inv){
        Bukkit.getScheduler().runTask(ArcaneTable.getPlugin(),()-> inv.getViewers().forEach(human->{
            giveItemFromSlot((Player)human,inv, getCurrentItem());
            human.closeInventory();
        }));

    }

    public void clickInventoryEvent(InventoryClickEvent event){
        BaseGui gui = BaseGui.getGui(event.getInventory());
        if(gui != null){
            if(!(gui instanceof TableGui) ){
                return;
            }
            if(event.getInventory()==event.getClickedInventory()){
                Player player = (Player)event.getWhoClicked();
                event.setCancelled(true);
                int slot = event.getSlot();
                ItemStack tempItem = getCurrentItem();
                ItemStack clicked = event.getCurrentItem();
                if(slot==(9*2)+1&&tempItem!=null){
                    giveItemFromSlot(player,event.getInventory(), tempItem);
                    updateInventoryWithEnchantments(event.getInventory(),null,true);
                }else if(tempItem != null){
                    if(event.getSlot() == 4 || event.getSlot()==6){
                        if(clicked!=null && clicked.getType().equals(Material.PLAYER_HEAD)){
                            if(event.getSlot()==4) {
                                ((TableGui)gui).paginator.prevPage();
                            }else{
                                ((TableGui)gui).paginator.nextPage();
                            }
                            updateInventoryWithEnchantments(event.getInventory(),getCurrentItem(),true);
                        }
                        return;
                    }
                    if(clicked == null || (!clicked.getType().equals(Material.NETHER_STAR) && !clicked.getType().equals((Material.getMaterial("KNOWLEDGE_BOOK"))!=null ? Material.getMaterial("KNOWLEDGE_BOOK") : Material.BOOK ) && !clicked.getType().equals(Material.ENCHANTED_BOOK))){
                        return;
                    }
                    if(clicked.getType().equals(Material.NETHER_STAR)){
                        ItemStack firstBook = event.getInventory().getItem(12);
                        if(firstBook!=null && firstBook.getType().equals(Material.ENCHANTED_BOOK)) {
                            updateInventoryWithEnchantments(event.getInventory(), getCurrentItem(), true);
                            return;
                        }
                        giveItemFromSlot(player,event.getInventory(), tempItem);
                        event.setCancelled(true);
                        Bukkit.getScheduler().runTask(ArcaneTable.getPlugin(),()-> event.getWhoClicked().closeInventory());

                    }else if(clicked.getType().equals((Material.getMaterial("KNOWLEDGE_BOOK"))!=null ? Material.getMaterial("KNOWLEDGE_BOOK") : Material.BOOK )){
                        updateInventoryWithEnchantments(event.getInventory(),clicked,false);
                    }else{
                        //Enchantment book
                        Enchantment ench = new ArrayList<>(clicked.getEnchantments().keySet()).get(0);//MAYBE REDO
                        int level = clicked.getEnchantments().get(ench);
                        int cost = EnchantmentUtils.getEnchantmentCost(ench,level);

                        if(EnchantmentUtils.doesItemAlreadyHasEnchant(tempItem,ench,level)){
                            //TODO - some stupid plugins re-add the enchantment if its still in the lore
                            EnchantmentUtils.purgeEnchantmentFromItem(tempItem,ench);

                            if(ArcaneTable.getPlugin().getConfig().getBoolean("ReturnXpOnDisenchant"))
                                player.setLevel(player.getLevel()+cost);
                            updateInventoryWithEnchantments(event.getInventory(),tempItem,true);
                            player.playSound(player.getLocation(),Sound.BLOCK_ANVIL_BREAK,1.0f,1.0f);
                            return;
                        }
                        if(cost>player.getExpToLevel()){
                            return;
                        }
                        new AdvancmentsUtils((Advancement)ArcaneTable.ADVANCEMENT).grant(player);
                        player.setLevel(player.getLevel()-cost);
                        ItemStack newItem = EnchantmentUtils.applyEnchantToItem(tempItem,ench,level,false,true);
                        updateInventoryWithEnchantments(event.getInventory(),newItem,true);
                        player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1.0f,1.0f);

                    }
                }else if(event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.NETHER_STAR)){
                    event.setCancelled(true);
                    Bukkit.getScheduler().runTask(ArcaneTable.getPlugin(),()-> event.getWhoClicked().closeInventory());
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
        if(tempItem==null || tempItem.getType().equals(Material.AIR))
            return;
        setEnchantingItem(inv,null);
        Map<Integer,ItemStack> leftOvers = player.getInventory().addItem(tempItem);
        leftOvers.forEach((index,item)-> player.getWorld().dropItem(player.getLocation(),item));
    }

    public void updateInventoryWithEnchantments(Inventory inv,ItemStack unique,boolean isFake){
        displayArrows(inv, false);
        if(unique==null){
            for(int x = 0; x < 5; x++ ){
                for (int y = 0; y < 4; y++){
                    inv.setItem(12+((y*9)+x),new ItemBuilder().setMaterial(Material.GLASS).setColor(7).setName(Component.space()).build());
                }
            }
            ((TableGui)BaseGui.getGui(inv)).paginator.setPage(0);
            return;
        }
        int index = 0;

        if(isFake) {
            ArrayList<Enchantment> enchs = EnchantmentUtils.getCanEnchants(unique);
            if(enchs.size()>20){
                ((TableGui)BaseGui.getGui(inv)).paginator.clear().addItems(enchs);
                enchs = new ArrayList<>((Collection<? extends Enchantment>) ((TableGui)BaseGui.getGui(inv)).paginator.getCurrentPage());
                displayArrows(inv, true);
            }
            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 5; x++) {
                    if (index >= enchs.size()) {
                        inv.setItem(12 + ((y * 9) + x), new ItemBuilder().setMaterial(Material.GLASS).setColor(7).setName(Component.space()).build());
                        continue;
                    }
                    Enchantment ench = enchs.get(index);
                    boolean isConflict = EnchantmentUtils.findConflictingEnchantments(unique,ench).size()>=1;

                    inv.setItem(12 + ((y * 9) + x), new ItemBuilder().setMaterial(Material.getMaterial("KNOWLEDGE_BOOK")!=null?Material.getMaterial("KNOWLEDGE_BOOK"):Material.BOOK)
                            .setName(EnchantmentUtils.getEnchantmentTranslateName(ench))
                            .setIgnoreLevelRestriction(true)
                            .addLore(isConflict ? ConfigurableLanguage.ENCHANTMENT_HAS_CONFLICT.getText() : null)
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
                        inv.setItem(12 + ((y * 9) + x), new ItemBuilder().setMaterial(Material.GLASS).setColor(7).setName(Component.space()).build());
                        continue;
                    }
                    int cost = EnchantmentUtils.EnchantmentCosts.getFromEnchant(ench).getCost(index+minLevel);
                    boolean alreadyHas = EnchantmentUtils.doesItemAlreadyHasEnchant(getCurrentItem(),ench,index+minLevel);
                    boolean isConflict = EnchantmentUtils.findConflictingEnchantments(getCurrentItem(),ench).size()>=1;
                    boolean hasHigher = EnchantmentUtils.itemHasHigherEnchantmentLevel(getCurrentItem(),ench,index+minLevel);
                    NamedTextColor colour = (cost>((Player)inv.getViewers().get(0)).getLevel() ? NamedTextColor.RED : NamedTextColor.AQUA);
                    inv.setItem(12 + ((y * 9) + x), new ItemBuilder()
                            .setMaterial(Material.ENCHANTED_BOOK)
                            .setName(EnchantmentUtils.getEnchantmentTranslateName(ench))
                            .setIgnoreLevelRestriction(true)
                            .addEnchantment(ench, index+minLevel)
                            .addLore(Component.translatable("container.repair.cost").args(Component.text(cost)).color(colour))
                            .addLore((alreadyHas || isConflict || hasHigher) ? " " : null)
                            .addLore(alreadyHas ? ConfigurableLanguage.ALREADY_HAS_LINE_1.getText() : null)
                            .addLore(alreadyHas ? ConfigurableLanguage.ALREADY_HAS_LINE_2.getText() : null)
                            .addLore((alreadyHas && (isConflict || hasHigher)) ? " " : null)
                            .addLore(isConflict ? ConfigurableLanguage.CONFLICT_LINE_1.getText() : null)
                            .addLore(isConflict ? ConfigurableLanguage.CONFLICT_LINE_2.getText() : null)
                            .addLore((isConflict && hasHigher) ? " " : null)
                            .addLore(hasHigher ? ConfigurableLanguage.HAS_HIGHER_LINE_1.getText() : null)
                            .addLore(hasHigher ? ConfigurableLanguage.HAS_HIGHER_LINE_2.getText() : null)
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
            inv.setItem(i,new ItemBuilder().setName(Component.space()).setMaterial(Material.GLASS).setColor(10).build());
        }
        inv.setItem((9*3)+1,new ItemBuilder().setMaterial(ArcaneTable.ETABLEMATERIAL).setName(Component.text(ConfigurableLanguage.PLACE_ITEM_ABOVE.getText()).color(NamedTextColor.GREEN)).addLore(ChatColor.WHITE+ ConfigurableLanguage.PLACE_ITEM_IN_SLOT.getText()).addLore(ChatColor.WHITE+"to view enchantments").build());
        inv.setItem((9*5)+4,new ItemBuilder().setMaterial(Material.NETHER_STAR).setName(Component.translatable("gui.back").color(NamedTextColor.GREEN)).addLore(ChatColor.WHITE+"Click to go back").build());
        displayArrows(inv, false);
        for(int x = 0; x < 5; x++ ){
            for (int y = 0; y < 4; y++){
                inv.setItem(12+((y*9)+x),new ItemBuilder().setMaterial(Material.GLASS).setColor(7).setName(Component.space()).build());
            }
        }

    }

    public void displayArrows(Inventory inv, boolean active){
        if(active){
            inv.setItem(6 , new ItemBuilder()
                    .setMaterial(SkullUtils.createSkull().getType())
                    .setName(Component.text(ConfigurableLanguage.PAGE_NEXT.getText()).color(NamedTextColor.DARK_GREEN))
                    .setPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19")
                    .build());
            TableGui gui = ((TableGui)BaseGui.getGui(inv));
            inv.setItem(5,new ItemBuilder()
                    .setName(Component.text(ConfigurableLanguage.PAGE_CURRENT.getText().formatted((gui.paginator.currentPage+1),(gui.paginator.getPageCount()+1))))
                    .setMaterial(Material.DRAGON_EGG)
                    .build()
                    );
            inv.setItem(4, new ItemBuilder()
                    .setMaterial(SkullUtils.createSkull().getType())
                    .setName(Component.text(ConfigurableLanguage.PAGE_PREV.getText()).color(NamedTextColor.DARK_GREEN))
                    .setPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0=")
                    .build());
        }else{
            inv.setItem(4,new ItemBuilder().setName(Component.space()).setMaterial(Material.GLASS).setColor(10).build());
            inv.setItem(5,new ItemBuilder().setName(Component.space()).setMaterial(Material.GLASS).setColor(10).build());
            inv.setItem(6,new ItemBuilder().setName(Component.space()).setMaterial(Material.GLASS).setColor(10).build());
        }

    }
}
