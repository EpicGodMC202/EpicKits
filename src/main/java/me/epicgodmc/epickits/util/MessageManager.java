package me.epicgodmc.epickits.util;

import me.epicgodmc.epickits.EpicKits;
import org.bukkit.ChatColor;

import java.util.List;

public class MessageManager
{

    private EpicKits plugin = EpicKits.getInstance();


    public String prefix = plugin.getConfig().getString("pluginPrefix");

    public String getMessage(String key)
    {
        return applyCC(prefix+plugin.getConfig().getString("messages."+key));
    }

    public List<String> getUsage()
    {
        return plugin.getConfig().getStringList("messages.usage");
    }

    public String applyCC(String input)
    {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    
}
