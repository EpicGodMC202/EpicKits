package me.epicgodmc.epickits.commands.subcommands;

import me.epicgodmc.epickits.EpicKits;
import me.epicgodmc.epickits.objects.SubCommand;
import me.epicgodmc.epickits.util.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class KitGiveCommand extends SubCommand {

    private EpicKits plugin = EpicKits.getInstance();
    private MessageManager mm = plugin.mm;


    //EpicKit kit <player> <name>

    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        if (sender.hasPermission("EpicKits.admin"))
        {
            if (args.length == 2)
            {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {

                    String kitName = args[1];
                    if (plugin.fileManager.kitExists(kitName))
                    {
                        sender.sendMessage(mm.getMessage("kitGive").replace("%kit%", kitName).replace("%player%", target.getName()));
                        loadKit(target, kitName);
                    }else{
                        sender.sendMessage(mm.getMessage("kitNotFound"));
                    }
                }else{
                    sender.sendMessage(mm.getMessage("playerNotFound"));
                }
            }else{
                sender.sendMessage(mm.getMessage("invalidArgLength"));
            }
        }else{
            sender.sendMessage(mm.getMessage("noPermission"));
        }
    }

    public void loadKit(Player player, String kitName)
    {
        player.getInventory().setHelmet(plugin.fileManager.getHelmet(kitName));
        player.getInventory().setChestplate(plugin.fileManager.getChest(kitName));
        player.getInventory().setLeggings(plugin.fileManager.getLegs(kitName));
        player.getInventory().setBoots(plugin.fileManager.getBoots(kitName));

        HashMap<Integer, ItemStack> kitHash = plugin.fileManager.getKitContents(kitName);

        if (plugin.fileManager.hasEditedKit(player.getUniqueId(), kitName))
        {
            for (String origin : plugin.fileManager.getDataConfig().getConfigurationSection(player.getUniqueId().toString()+".kits."+kitName).getKeys(false))
            {
                int ori = Integer.parseInt(origin);
                int val = plugin.fileManager.getDataConfig().getInt(player.getUniqueId().toString()+".kits."+kitName+"."+origin);

                ItemStack oriStack = kitHash.get(ori);
                ItemStack valStack = kitHash.get(val);

                kitHash.put(val, oriStack);
                kitHash.put(ori, valStack);
            }
        }
        for (int slot : kitHash.keySet())
        {
            player.getInventory().setItem(slot, kitHash.get(slot));
        }
    }



    @Override
    public String name() {
        return plugin.cmdRoot.kit;
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
