package me.tropicalshadow.arcanetable.gui;

import me.tropicalshadow.arcanetable.ArcaneTable;
import me.tropicalshadow.arcanetable.objects.Paginator;
import me.tropicalshadow.arcanetable.utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.Map;

public class TableGui extends BaseGui{

    private final static int ACTIVE_ITEM_SLOT = (9*2)+1;


    public Paginator<Enchantment> paginator;
    private Block block;

    public TableGui(){
        super("Arcane Table",6);
        this.setOnClick(this::clickInventoryEvent);
        this.setOnClose(this::closeInventoryEvent);
        this.setOnOpen(this::openInventoryEvent);
        try {
            paginator = new Paginator<>(20);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArcaneTable.getPlugin().reloadConfig();
    }

    @UnknownNullability
    public Block getBlock(){
        return this.block;
    }
    public void setEnchantmentTable(Block block){
        this.block = block;
    }

    @Nullable
    public ItemStack getActiveItem(){
        return getInventory().getItem(ACTIVE_ITEM_SLOT);
    }

    public void setActiveItem(Inventory inv, ItemStack item){
        inv.setItem(ACTIVE_ITEM_SLOT, item);
    }

    public void closeInventorySafely(Inventory inventory) {
        Bukkit.getScheduler().runTask(ArcaneTable.getPlugin(), () -> {
            for (HumanEntity viewer : inventory.getViewers()) {
                if (viewer instanceof Player player) {
                    giveItemFromSlot(player, inventory, getActiveItem());
                    player.closeInventory();
                }
            }
        });
    }

    public void clickInventoryEvent(InventoryClickEvent event) {
        BaseGui gui = BaseGui.getGui(event.getInventory());
        if (!(gui instanceof TableGui)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();

        Inventory clickedInventory = event.getClickedInventory();
        if (event.getInventory() != clickedInventory) {
            event.setCancelled(true);
            ItemStack currentItem = getActiveItem();
            if (currentItem == null) {
                ItemStack tempItem = event.getCurrentItem();
                setActiveItem(event.getInventory(), tempItem);
                event.setCurrentItem(null);
                updateInventoryWithEnchantments(event.getInventory(), tempItem, true);
                player.playSound(player, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            }
            return;
        }

        event.setCancelled(true);
        int slot = event.getSlot();
        ItemStack tempItem = getActiveItem();
        ItemStack clicked = event.getCurrentItem();

        if (slot == ACTIVE_ITEM_SLOT && tempItem != null) {
            giveItemFromSlot(player, event.getInventory(), tempItem);
            updateInventoryWithEnchantments(event.getInventory(), null, true);
            player.playSound(player, Sound.UI_LOOM_SELECT_PATTERN, 1.0f, 1.0f);
        } else if (tempItem != null) {

            if (slot == 4 || slot == 6) {
                if (clicked != null && clicked.getType().equals(Material.PLAYER_HEAD)) {
                    TableGui tableGui = (TableGui) gui;
                    if (slot == 4) {
                        tableGui.paginator.prevPage();
                    } else {
                        tableGui.paginator.nextPage();
                    }
                    updateInventoryWithEnchantments(event.getInventory(), getActiveItem(), true);
                    player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                }
                return;
            }

            if (clicked == null ||
                    !clicked.getType().equals(Material.NETHER_STAR) &&
                    !clicked.getType().equals(getKnowledgeBookMaterial()) &&
                    !clicked.getType().equals(Material.ENCHANTED_BOOK)) {
                return;
            }

            if (clicked.getType().equals(Material.NETHER_STAR)) {
                ItemStack firstBook = event.getInventory().getItem(12);
                if (firstBook != null && firstBook.getType().equals(Material.ENCHANTED_BOOK)) {
                    updateInventoryWithEnchantments(event.getInventory(), getActiveItem(), true);
                    player.playSound(player, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    return;
                }
                giveItemFromSlot(player, event.getInventory(), tempItem);
                event.setCancelled(true);
                Bukkit.getScheduler().runTask(ArcaneTable.getPlugin(), () -> event.getWhoClicked().closeInventory());
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            } else if (clicked.getType().equals(getKnowledgeBookMaterial())) {
                updateInventoryWithEnchantments(event.getInventory(), clicked, false);
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            } else {
                // Enchantment book
                Enchantment enchantment = new ArrayList<>(clicked.getEnchantments().keySet()).get(0);
                int level = clicked.getEnchantments().get(enchantment);
                int cost = EnchantmentUtils.getEnchantmentCost(enchantment, level);

                if (EnchantmentUtils.doesItemAlreadyHasEnchant(tempItem, enchantment, level)) {
                    if(!ArcaneTable.getPlugin().getConfig().getBoolean("disenchant.enabled")){
                        player.playSound(player, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 1.0f, 1.0f);
                        return;
                    }
                    EnchantmentUtils.purgeEnchantmentFromItem(tempItem, enchantment);

                    double returnPercentage = (float) ArcaneTable.getPlugin().getConfig().getDouble("disenchant.xp-percentage");

                    if (returnPercentage > 0) {
                        cost = (int) (returnPercentage / 100 * cost);
                        player.setLevel(player.getLevel() + cost);
                    }

                    updateInventoryWithEnchantments(event.getInventory(), tempItem, true);
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
                    return;
                }

                if (cost > player.getLevel()) {
                    return;
                }

                new AdvancmentsUtils((Advancement) ArcaneTable.ADVANCEMENT).grant(player);
                player.setLevel(player.getLevel() - cost);
                ItemStack newItem = EnchantmentUtils.applyEnchantToItem(tempItem, enchantment, level, false, true);
                updateInventoryWithEnchantments(event.getInventory(), newItem, true);
                player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
            }
        } else if (event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.NETHER_STAR)) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(ArcaneTable.getPlugin(), () -> event.getWhoClicked().closeInventory());
        }
    }

    private Material getKnowledgeBookMaterial() {
        return Material.getMaterial("KNOWLEDGE_BOOK") != null ? Material.getMaterial("KNOWLEDGE_BOOK") : Material.BOOK;
    }

    private void giveItemFromSlot(Player player, Inventory inventory, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        setActiveItem(inventory, null);
        dropLeftoverItems(player.getWorld(), player.getLocation(), player.getInventory().addItem(item));
    }

    public void updateInventoryWithEnchantments(Inventory inv, @Nullable ItemStack unique, boolean isFake) {
        displayArrows(inv, false);

        ItemStack activeItem = getActiveItem();
        if (unique == null || activeItem == null) {
            clearInventory(inv);
            ((TableGui) BaseGui.getGui(inv)).paginator.clear().setPage(0);
            return;
        }

        int index = 0;

        if (isFake) {
            ArrayList<Enchantment> enchs = EnchantmentUtils.getCanEnchants(activeItem);

            if (enchs.size() > 20) {
                ((TableGui) BaseGui.getGui(inv)).paginator.clear().addItems(enchs);
                enchs = new ArrayList<>(((TableGui) BaseGui.getGui(inv)).paginator.getCurrentPageContent());
                displayArrows(inv, true);
            }
            if(enchs.size() == 0) {
                clearInventory(inv);
                return;
            }

            fillFakeEnchantmentItems(inv, activeItem, enchs, index);
        } else {
            Enchantment ench = new ArrayList<>(unique.getEnchantments().keySet()).get(0);
            int maxLevel = ench.getMaxLevel();
            int minLevel = ench.getStartLevel();

            fillFakeEnchantmentItems(inv, activeItem, ench, maxLevel, minLevel, index);
        }
    }


    private void clearInventory(Inventory inv) {
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 4; y++) {
                inv.setItem(12 + ((y * 9) + x), createGlassItem(7));
            }
        }
    }

    private void fillFakeEnchantmentItems(Inventory inv, ItemStack activeItem, ArrayList<Enchantment> enchs, int index) {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 5; x++) {
                if (index >= enchs.size()) {
                    inv.setItem(12 + ((y * 9) + x), createGlassItem(7));
                    continue;
                }

                Enchantment ench = enchs.get(index);
                boolean isConflict = EnchantmentUtils.findConflictingEnchantments(activeItem, ench).size() >= 1;

                inv.setItem(12 + ((y * 9) + x), createEnchantmentItem(ench, 1, -1, false, isConflict, false, NamedTextColor.WHITE, true));
                index++;
            }
        }
    }

    private void fillFakeEnchantmentItems(Inventory inv, ItemStack activeItem, Enchantment ench, int maxLevel, int minLevel, int index) {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 5; x++) {
                if (index >= maxLevel) {
                    inv.setItem(12 + ((y * 9) + x), createGlassItem(7));
                    continue;
                }

                int cost = EnchantmentUtils.EnchantmentCosts.getFromEnchant(ench).getCost(index + minLevel);
                boolean alreadyHas = EnchantmentUtils.doesItemAlreadyHasEnchant(activeItem, ench, index + minLevel);
                boolean isConflict = EnchantmentUtils.findConflictingEnchantments(activeItem, ench).size() >= 1;
                boolean hasHigher = EnchantmentUtils.itemHasHigherEnchantmentLevel(activeItem, ench, index + minLevel);

                NamedTextColor colour = (cost > ((Player) inv.getViewers().get(0)).getLevel() && !alreadyHas ? NamedTextColor.RED : NamedTextColor.AQUA);

                inv.setItem(12 + ((y * 9) + x), createEnchantmentItem(ench, index + minLevel, cost, alreadyHas, isConflict, hasHigher, colour, false));

                index++;
            }
        }
    }

    private ItemStack createEnchantmentItem(Enchantment ench, int level, int cost, boolean alreadyHas, boolean isConflict, boolean hasHigher, NamedTextColor colour, boolean isFake) {
        final Material material = isFake ? getKnowledgeBookMaterial() : Material.ENCHANTED_BOOK;
        ItemBuilder itemBuilder = new ItemBuilder()
                .setMaterial(material)
                .setName(EnchantmentUtils.getEnchantmentTranslateName(ench))
                .setIgnoreLevelRestriction(true)
                .addEnchantment(ench, level);

        if(!isFake)
            itemBuilder.addLore(Component.translatable("container.repair.cost").args(Component.text(cost)).color(colour));

        if (alreadyHas) {
            itemBuilder.addLore(" ");
            itemBuilder.addLore(ConfigurableLanguage.ALREADY_HAS_LINE_1.getText());
            itemBuilder.addLore(ConfigurableLanguage.ALREADY_HAS_LINE_2.getText());
        }

        if (isConflict) {
            if(alreadyHas){
                itemBuilder.addLore(" ");
            }
            itemBuilder.addLore(ConfigurableLanguage.CONFLICT_LINE_1.getText());
            itemBuilder.addLore(ConfigurableLanguage.CONFLICT_LINE_2.getText());
        }

        if (hasHigher) {
            if(alreadyHas || isConflict){
                itemBuilder.addLore(" ");
            }
            itemBuilder.addLore(ConfigurableLanguage.HAS_HIGHER_LINE_1.getText());
            itemBuilder.addLore(ConfigurableLanguage.HAS_HIGHER_LINE_2.getText());
        }

        return itemBuilder.build();
    }

    public void closeInventoryEvent(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        ItemStack item = inventory.getItem(ACTIVE_ITEM_SLOT);

        if (item == null) {
            return;
        }

        Player player = (Player) event.getPlayer();
        dropLeftoverItems(player.getWorld(), player.getLocation(), player.getInventory().addItem(item));
        player.playSound(player, Sound.UI_STONECUTTER_TAKE_RESULT, 1.0f, 1.0f);
    }

    public void openInventoryEvent(InventoryOpenEvent event){
        clearInventory(event.getInventory());
        Player player = (Player) event.getPlayer();
        player.playSound(player, Sound.UI_LOOM_TAKE_RESULT, 1.0f, 1.0f);
    }

    @Override
    public void addToInventory(Inventory inventory){

        int numRows = this.getRows();
        int numRowsTimes9 = numRows * 9;


        for (int i = 0; i < numRowsTimes9; i++) {
            if (i == ACTIVE_ITEM_SLOT) {
                continue;
            }
            inventory.setItem(i, createGlassItem(10));
        }


        inventory.setItem((9 * 3) + 1, createArcaneTableItem());
        inventory.setItem((9 * 5) + 4, createBackItem());
        displayArrows(inventory, false);

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 4; y++) {
                int slotIndex = 12 + (y * 9) + x;
                inventory.setItem(slotIndex, createGlassItem(10));
            }
        }

    }

    private void dropLeftoverItems(World world, Location location, Map<Integer, ItemStack> leftovers) {
        leftovers.values().forEach(item -> world.dropItem(location, item));
    }

    private ItemStack createArcaneTableItem() {
        return new ItemBuilder()
                .setMaterial(ArcaneTable.ETABLEMATERIAL)
                .setName(LegacyComponentSerializer.legacyAmpersand().deserialize(ConfigurableLanguage.PLACE_ITEM_NAME.getText()).colorIfAbsent(NamedTextColor.GREEN))
                .addLore(ConfigurableLanguage.PLACE_ITEM_LORE.getTextList().toArray(new String[0]))
                .build();
    }

    private ItemStack createBackItem() {
        return new ItemBuilder()
                .setMaterial(Material.NETHER_STAR)
                .setName(LegacyComponentSerializer.legacyAmpersand().deserialize(ConfigurableLanguage.GO_BACK_NAME.getText()).colorIfAbsent(NamedTextColor.GREEN))
                .addLore(ConfigurableLanguage.GO_BACK_LORE.getTextList().toArray(new String[0]))
                .build();
    }

    private ItemStack createGlassItem(int colour) {
        return new ItemBuilder()
                .setName(Component.space())
                .setMaterial(Material.GLASS)
                .setColor(colour)
                .build();
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
                    .setName(Component.text(ConfigurableLanguage.PAGE_CURRENT.getText().formatted((gui.paginator.getCurrentPage()+1),(gui.paginator.getPageCount()+1))))
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
