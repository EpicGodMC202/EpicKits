package me.epicgodmc.epickits.commands.subcommands;

import de.tr7zw.nbtapi.NBTItem;
import me.epicgodmc.epickits.EpicKits;
import me.epicgodmc.epickits.objects.Editor;
import me.epicgodmc.epickits.objects.SubCommand;
import me.epicgodmc.epickits.util.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class KitEditCommand extends SubCommand implements Listener {
    private EpicKits plugin = EpicKits.getInstance();
    private MessageManager mm = plugin.mm;


    // /EpicKits edit <kit>

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                String kit = args[0];
                if (plugin.fileManager.kitExists(kit)) {
                    if (args.length == 1) {
                        if (player.hasPermission("EpicKits.edit." + kit) || player.hasPermission("EpicKits.edit.*")) {
                            sender.sendMessage(mm.getMessage("editorOpen"));
                            openEditor(player, kit);
                        } else {
                            player.sendMessage(mm.getMessage("noPermissionToEdit").replace("%kit%", kit));
                        }
                    } else sender.sendMessage(mm.getMessage("invalidArgLength"));
                } else sender.sendMessage(mm.getMessage("kitNotFound"));
            } else sender.sendMessage(mm.getMessage("notEnoughArgs"));
        } else sender.sendMessage(mm.getMessage("onlyPlayers"));
    }

    private void openEditor(Player player, String kitName) {
        String invName = plugin.getConfig().getString("editor.name");
        Inventory editor = Bukkit.createInventory(null, 36, mm.applyCC(invName));

        HashMap<Integer, ItemStack> kitHash = plugin.fileManager.getKitContents(kitName);


        for (int i : kitHash.keySet()) {
            editor.setItem(getActualSlot(i), addIdentifier(kitHash.get(i), i));

        }

        plugin.editors.put(player.getUniqueId(), new Editor(editor, kitName));
        player.openInventory(editor);

    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        UUID uuid = e.getWhoClicked().getUniqueId();
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if (plugin.editors.containsKey(uuid)) {
            e.setCancelled(true);
            Editor editer = plugin.editors.get(uuid);
            int clickedSlot = e.getSlot();
            if (e.getRawSlot() == e.getSlot()) {
                if (!isNull(inv.getItem(clickedSlot))) {
                    if (editer.hasSelected()) {
                        switchItems(editer, inv, editer.getSelected(), clickedSlot);
                    } else {
                        editer.setSelected(clickedSlot);
                        e.getInventory().setItem(clickedSlot, getSelected(e.getCurrentItem()));
                    }
                } else {
                    if (editer.hasSelected()) {
                        switchItems(editer, inv, editer.getSelected(), clickedSlot);
                    }
                }
                player.updateInventory();
            }
        }
    }

    private void switchItems(Editor editor, Inventory inv, int selectedSlot, int slot2) {

        ItemStack selected = inv.getItem(selectedSlot);
        ItemStack to;

        if (isNull(inv.getItem(slot2))) {
            to = new ItemStack(Material.AIR);
        } else to = inv.getItem(slot2);

        inv.setItem(slot2, unSelect(selected));
        inv.setItem(selectedSlot, to);

        editor.setSwitchedSlot(getOrigin(selected), getActualSwitchSlot(slot2));
        editor.setSelected(-1);
    }

    @EventHandler
    public void closeInv(InventoryCloseEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (plugin.editors.containsKey(uuid)) {
            saveSlots(uuid, plugin.editors.get(uuid));
            plugin.editors.remove(uuid);
        }

    }

    private void saveSlots(UUID uuid, Editor editor) {
        FileConfiguration config = plugin.fileManager.getDataConfig();
        String kit = editor.getKit();
        HashMap<Integer, Integer> switches = editor.getSwitches();

        config.set(uuid.toString() + ".kits." + kit, null);

        for (int i : switches.keySet()) {
            int f = switches.get(i);
            config.set(uuid.toString() + ".kits." + kit + "." + i, f);
        }
        plugin.fileManager.saveDataConf();

        Bukkit.getPlayer(uuid).sendMessage(mm.getMessage("savingLayout").replace("%kit%", kit));
    }

    private ItemStack unSelect(ItemStack i) {
        ItemStack output = i;
        ItemMeta meta = output.getItemMeta();

        List<String> lore = meta.getLore();
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);

        meta.setLore(lore);
        output.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(output);
        nbtItem.setBoolean("SELECTED", false);

        return nbtItem.getItem();
    }

    private ItemStack getSelected(ItemStack clicked) {
        ItemStack output = clicked;
        ItemMeta meta = output.getItemMeta();

        List<String> lore;
        if (output.hasItemMeta() && output.getItemMeta().hasLore()) {
            lore = output.getItemMeta().getLore();
        } else {
            lore = new ArrayList<>();
        }
        lore.add("");
        lore.add(mm.applyCC("&5&l[&6&k::&5&l] &f&lSELECTED &5&l[&6&k::&5&l]"));
        meta.setLore(lore);
        output.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(output);
        nbtItem.setBoolean("SELECTED", true);
        output = nbtItem.getItem();

        return output;
    }

    private ItemStack addIdentifier(ItemStack i, int slot) {
        NBTItem nbtItem = new NBTItem(i);
        nbtItem.setBoolean("EditorItem", true);
        nbtItem.setInteger("origin", slot);
        return nbtItem.getItem();
    }

    private boolean isNull(ItemStack i) {
        return i == null || i.getType().equals(Material.AIR);
    }

    private int getOrigin(ItemStack i) {
        NBTItem nbtItem = new NBTItem(i);
        return nbtItem.getInteger("origin");

    }

    private int getActualSwitchSlot(int slot) {
        switch (slot) {
            case 0:
                return 9;
            case 1:
                return 10;
            case 2:
                return 11;
            case 3:
                return 12;
            case 4:
                return 13;
            case 5:
                return 14;
            case 6:
                return 15;
            case 7:
                return 16;
            case 8:
                return 17;
            case 9:
                return 18;
            case 10:
                return 19;
            case 11:
                return 20;
            case 12:
                return 21;
            case 13:
                return 22;
            case 14:
                return 23;
            case 15:
                return 24;
            case 16:
                return 25;
            case 17:
                return 26;
            case 18:
                return 27;
            case 19:
                return 28;
            case 20:
                return 29;
            case 21:
                return 30;
            case 22:
                return 31;
            case 23:
                return 32;
            case 24:
                return 33;
            case 25:
                return 34;
            case 26:
                return 35;
            case 27:
                return 0;
            case 28:
                return 1;
            case 29:
                return 2;
            case 30:
                return 3;
            case 31:
                return 4;
            case 32:
                return 5;
            case 33:
                return 6;
            case 34:
                return 7;
            case 35:
                return 8;
            default:
                return -1;
        }

    }

    private int getActualSlot(int slot) {
        switch (slot) {
            case 0:
                return 27;
            case 1:
                return 28;
            case 2:
                return 29;
            case 3:
                return 30;
            case 4:
                return 31;
            case 5:
                return 32;
            case 6:
                return 33;
            case 7:
                return 34;
            case 8:
                return 35;
            case 9:
                return 0;
            case 10:
                return 1;
            case 11:
                return 2;
            case 12:
                return 3;
            case 13:
                return 4;
            case 14:
                return 5;
            case 15:
                return 6;
            case 16:
                return 7;
            case 17:
                return 8;
            case 18:
                return 9;
            case 19:
                return 10;
            case 20:
                return 11;
            case 21:
                return 12;
            case 22:
                return 13;
            case 23:
                return 14;
            case 24:
                return 15;
            case 25:
                return 16;
            case 26:
                return 17;
            case 27:
                return 18;
            case 28:
                return 19;
            case 29:
                return 20;
            case 30:
                return 21;
            case 31:
                return 22;
            case 32:
                return 23;
            case 33:
                return 24;
            case 34:
                return 25;
            case 35:
                return 26;
            default:
                return -1;
        }
    }


    @Override
    public String name() {
        return plugin.cmdRoot.edit;
    }

    @Override
    public String info() {
        return null;
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }
}
