package me.epicgodmc.epickits.commands.subcommands;

import me.epicgodmc.epickits.EpicKits;
import me.epicgodmc.epickits.objects.SubCommand;
import me.epicgodmc.epickits.util.MessageManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;

public class KitCreateCommand extends SubCommand
{
    private EpicKits plugin = EpicKits.getInstance();
    private MessageManager mm = plugin.mm;


    // /epickits create <kitname>

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        //this is the code that runs when a player types /epickits kitcreate

        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            if (player.hasPermission("EpicKits.admin"))
            {
                if (args.length == 1)
                {
                    if (!plugin.fileManager.kitExists(args[0]))
                    {
                        player.sendMessage(mm.getMessage("serializing"));

                        PlayerInventory inv = player.getInventory();
                        try {
                            saveKit(args[0], inv.getHelmet(), inv.getChestplate(), inv.getLeggings(), inv.getBoots(), getKitInv(inv));
                        }catch (Exception e)
                        {
                            player.sendMessage(mm.getMessage("kitCreateFailed"));
                            e.printStackTrace();
                            return;
                        }
                        player.sendMessage(mm.getMessage("kitCreateSuccess"));
                    }else{
                        player.sendMessage(mm.getMessage("kitAlreadyExists"));
                    }
                }else{
                    player.sendMessage(mm.getMessage("invalidArgLength"));
                }
            }else {
                player.sendMessage(mm.getMessage("noPermission"));
            }
        }else{
            sender.sendMessage(mm.getMessage("onlyPlayers"));
        }

    }


    private HashMap<Integer, ItemStack> getKitInv(PlayerInventory inv)
    {
        HashMap<Integer, ItemStack> kitHash = new HashMap<>();

        for (int i = 0; i < inv.getContents().length; i++ )
        {
            ItemStack current = inv.getItem(i);
            if (current == null || current.getType().equals(Material.AIR)) continue;
            kitHash.put(i, current);
        }

        return kitHash;
    }
    private void saveKit(String kitName, ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack boots, HashMap<Integer, ItemStack> inv)
    {
        FileConfiguration kitConf = plugin.fileManager.getKitFile();

        if (helmet != null) {
            kitConf.set("kits." + kitName + ".helmet", helmet.serialize());
        }

        if (chest != null) {
            kitConf.set("kits." + kitName + ".chest", chest.serialize());
        }

        if (legs != null) {
            kitConf.set("kits." + kitName + ".legs", legs.serialize());
        }

        if (boots != null) {
            kitConf.set("kits." + kitName + ".boots", boots.serialize());
        }


        for (Integer slot : inv.keySet())
        {
            kitConf.set("kits."+kitName+".inv."+slot, inv.get(slot).serialize());
        }

        plugin.fileManager.saveKitConf();

    }




    @Override
    public String name() {
        return plugin.cmdRoot.kitCreate;
    }

    @Override
    public String info() {
        return "";
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }
}
