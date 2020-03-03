package me.epicgodmc.epickits.util;

import me.epicgodmc.epickits.EpicKits;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class FileManager
{
    private EpicKits plugin;

    public FileManager(EpicKits plugin)
    {
        this.plugin = plugin;

        createFiles();
    }



    private File kitFile;
    private FileConfiguration kitConfig;

    private File dataFile;
    private FileConfiguration dataConfig;


    private void createFiles()
    {
        kitFile = new File(plugin.getDataFolder(), "Kits.yml");
        dataFile = new File(plugin.getDataFolder(), "PlayerData.yml");

        if (!kitFile.exists())
        {
            kitFile.getParentFile().mkdirs();
            plugin.saveResource("Kits.yml", false);
        }
        if (!dataFile.exists())
        {
            dataFile.getParentFile().mkdirs();
            plugin.saveResource("PlayerData.yml", false);
        }

        kitConfig = new YamlConfiguration();
        dataConfig = new YamlConfiguration();

        try{

            kitConfig.load(kitFile);
            dataConfig.load(dataFile);

        }catch (IOException | InvalidConfigurationException e)
        {
            Bukkit.getConsoleSender().sendMessage("[EpicKits] Failed to create files!");
            e.printStackTrace();
        }
    }

    public boolean hasEditedKit(UUID uuid, String kit)
    {
        return dataConfig.isSet(uuid.toString()+".kits."+kit);
    }

    public FileConfiguration getDataConfig()
    {
        return this.dataConfig;
    }
    public void saveDataConf()
    {
        try{
            dataConfig.save(dataFile);
        }catch (IOException e)
        {
            Bukkit.getConsoleSender().sendMessage("[EpicKits] failed to save PlayerData.yml");
            e.printStackTrace();
        }

    }

    ///////

    public FileConfiguration getKitFile()
    {
        return this.kitConfig;
    }

    public boolean kitExists(String kitName)
    {
        return getKitFile().isSet("kits."+kitName);
    }

    public void saveKitConf()
    {
        try{
            kitConfig.save(kitFile);
        }catch (IOException e)
        {
            Bukkit.getConsoleSender().sendMessage("[EpicKits] failed to save kits.yml");
            e.printStackTrace();
        }

    }

    public ItemStack getHelmet(String kit)
    {
        if (getKitFile().isSet("kits."+kit+".helmet"))
        {
            return ItemStack.deserialize(this.kitConfig.getConfigurationSection("kits."+kit+".helmet").getValues(false));

        }else return new ItemStack(Material.AIR);
    }
    public ItemStack getChest(String kit)
    {
        if (getKitFile().isSet("kits."+kit+".chest"))
        {
            return ItemStack.deserialize(this.kitConfig.getConfigurationSection("kits."+kit+".chest").getValues(false));

        }else return new ItemStack(Material.AIR);
    }
    public ItemStack getLegs(String kit)
    {
        if (getKitFile().isSet("kits."+kit+".legs"))
        {
            return ItemStack.deserialize(this.kitConfig.getConfigurationSection("kits."+kit+".legs").getValues(false));

        }else return new ItemStack(Material.AIR);
    }
    public ItemStack getBoots(String kit)
    {
        if (getKitFile().isSet("kits."+kit+".boots"))
        {
            return ItemStack.deserialize(this.kitConfig.getConfigurationSection("kits."+kit+".boots").getValues(false));

        }else return new ItemStack(Material.AIR);
    }

    public HashMap<Integer, ItemStack> getKitContents(String kitName)
    {
        HashMap<Integer, ItemStack> kitHash = new HashMap<>();

       getKitFile().getConfigurationSection("kits."+kitName+".inv").getKeys(false).forEach((e) -> {
           kitHash.put(Integer.parseInt(e), ItemStack.deserialize(getKitFile().getConfigurationSection("kits."+kitName+".inv."+e).getValues(false)));
       });

       return kitHash;
    }

}
