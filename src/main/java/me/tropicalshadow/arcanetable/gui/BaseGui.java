package me.tropicalshadow.arcanetable.gui;

import me.tropicalshadow.arcanetable.ArcaneTable;
import me.tropicalshadow.arcanetable.listener.GuiHook;
import me.tropicalshadow.arcanetable.utils.Logging;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class BaseGui implements InventoryHolder {

    public static final Map<Inventory,BaseGui> GUI_INVETORIES = new WeakHashMap<>();

    private Consumer<InventoryClickEvent> clickEvent;
    private Consumer<InventoryCloseEvent> closeEvent;
    private Consumer<InventoryOpenEvent> openEvent;
    private static boolean hasRegisteredListeners = false;
    public boolean isClickable = true;
    private Inventory inv = null;
    private boolean updating;
    private final String name;
    private final int rows;

    public BaseGui(){
        this("BaseGui");
    }
    public BaseGui(String name){
        this(name,1);
    }
    public BaseGui(String name,int row){
        this.name = name;
        this.rows = row;
        if(!hasRegisteredListeners){
            Bukkit.getPluginManager().registerEvents(new GuiHook(),
                    ArcaneTable.getPlugin());
            hasRegisteredListeners = true;
        }
    }

    public void addToInventory(Inventory inv){}

    public Inventory createInventory(){
        Inventory inv =  Bukkit.createInventory(this,this.rows*9,this.name);
        addToInventory(inv);
        this.addInvetory(inv,this);
        return inv;
    }

    public void setOnClick(Consumer<InventoryClickEvent> onClick){
        this.clickEvent = onClick;
    }
    public void handleClick(InventoryClickEvent event){
        callCallback(clickEvent,event,"onClick");
    }
    public void setOnClose(Consumer<InventoryCloseEvent> onClose){
        this.closeEvent = onClose;
    }
    public void handleClose(InventoryCloseEvent event){
        callCallback(closeEvent,event,"onClose");
    }
    public void setOnOpen(Consumer<InventoryOpenEvent> onClose){
        this.openEvent = onClose;
    }
    public void handleOpen(InventoryOpenEvent event){callCallback(openEvent,event,"onOpen");}
    private <T extends InventoryEvent> void callCallback(Consumer<T> callback, T event, String callbackName){
        if(callback == null)return;
        try{
            callback.accept(event);
        }catch(Throwable throwable){
            String message = "Exception while handling "+ callbackName;
            if(event instanceof InventoryCloseEvent){
                message += throwable.getMessage();
            }
            if(event instanceof InventoryClickEvent clickEvent){
                message += ", slot=" + clickEvent.getSlot();
                throwable.printStackTrace();
            }
            Logging.danger(message);
        }
    }
    protected void addInvetory(Inventory inventory,BaseGui gui){
        GUI_INVETORIES.put(inventory,gui);
    }
    public static BaseGui getGui(Inventory inventory){
        return GUI_INVETORIES.get(inventory);
    }
    public int getViewerCount(){
        return getInventory().getViewers().size();
    }
    public List<HumanEntity> getViewers(){
        return new ArrayList<>(getInventory().getViewers());
    }
    public void update(){
        updating = true;
        getViewers().forEach(this::show);
        if(!updating)
            throw new AssertionError("Gui#isUpdating became false before Gui#update finished");
        updating = false;

    }
    public boolean isUpdating(){
        return this.updating;
    }
    public void show(HumanEntity humanEntity){
        humanEntity.openInventory(getInventory());
    }
    @Override
    public @NotNull Inventory getInventory() {
        if(inv == null){
            inv = createInventory();
        }
        return inv;
    }

    public int getRows() {
        return rows;
    }
}
