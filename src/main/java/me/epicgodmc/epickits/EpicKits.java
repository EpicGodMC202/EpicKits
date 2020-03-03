package me.epicgodmc.epickits;

import me.epicgodmc.epickits.commands.CmdRoot;
import me.epicgodmc.epickits.commands.subcommands.KitEditCommand;
import me.epicgodmc.epickits.objects.Editor;
import me.epicgodmc.epickits.util.FileManager;
import me.epicgodmc.epickits.util.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class EpicKits extends JavaPlugin
{

    private static EpicKits instance;
    public static EpicKits getInstance()
    {
        return instance;
    }

    public HashMap<UUID, Editor> editors = new HashMap<>();

    public MessageManager mm;
    public CmdRoot cmdRoot;
    public FileManager fileManager;


    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        registerInstances();
        registerEvents();

        cmdRoot.setup();
    }

    @Override
    public void onDisable() {
        instance = null;

    }


    public void registerInstances()
    {
        mm = new MessageManager();
        cmdRoot = new CmdRoot();
        fileManager = new FileManager(this);

    }

    public void registerEvents()
    {
        this.getServer().getPluginManager().registerEvents(new KitEditCommand(), this);

    }


}
