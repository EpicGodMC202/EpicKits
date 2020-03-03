package me.epicgodmc.epickits.objects;

import org.bukkit.inventory.Inventory;

import java.util.HashMap;


public class Editor
{

    private String kit;
    private Inventory inv;
    private int selected = -1;
    private HashMap<Integer, Integer> switchedSlots = new HashMap<>();

    public Editor(Inventory inv, String kit) {
        this.inv = inv;
        this.kit = kit;
    }



    public void setSwitchedSlot(int slot, int slot2)
    {
        switchedSlots.put(slot, slot2);
    }

    public Inventory getInv() {
        return inv;
    }

    public void setInv(Inventory inv) {
        this.inv = inv;
    }

    public boolean hasSelected()
    {
        return selected != -1;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public String getKit() {
        return kit;
    }

    public void setKit(String kit) {
        this.kit = kit;
    }

    public HashMap<Integer, Integer> getSwitches()
    {
        return switchedSlots;
    }
}
