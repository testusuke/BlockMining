package net.testusuke.blockmining;

import net.testusuke.blockmining.Data.MiningData;
import net.testusuke.blockmining.Data.MiningRanking;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public final class BlockMining extends JavaPlugin {

    private static BlockMining plugin;
    //  Prefix
    public String prefix;
    public boolean mode;
    public String author = "testusuke";
    public String version = "1.0";
    public String pluginName = "BlockMining";
    //  Class
    public BlockVault blockVault = null;
    public BlockWorld blockWorld = null;
    public MiningMoneyTask moneyTask = null;
    public MiningRanking miningRanking = null;
    //  PlayerData
    public HashMap<Player, MiningData> miningDataMap = new HashMap<>();
    public HashMap<Player, MoneyScoreBoard> scoreBoardMap = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        //  Logger
        getLogger().info("==============================");
        getLogger().info("Plugin: " + pluginName);
        getLogger().info("Ver: " + version + " Author: " + author);
        getLogger().info("==============================");
        //  Event
        getServer().getPluginManager().registerEvents(new BlockMiningEvent(this),this);
        //  Command
        getCommand("bm").setExecutor(new MiningCommand(this));
        getCommand("mining").setExecutor(new MiningCommand(this));
        //  Config
        this.saveDefaultConfig();
        //  Class
        plugin = this;
        blockVault = new BlockVault(this);
        blockWorld = new BlockWorld(this);
        moneyTask = new MiningMoneyTask(this);
        miningRanking = new MiningRanking(this);
        new VaultManager(this);
        //  Runnable
        int moneyTicks = 20 * 10;
        moneyTask.runTaskTimer(this,moneyTicks,moneyTicks);
        //  MiningRanking Runnable
        int rankingTicks = 20 * 60 * 5; //  ５分
        new BukkitRunnable(){
            @Override
            public void run(){
                //  Save
                for(MiningData md : miningDataMap.values()){
                    md.saveData();
                }
                //  load ranking
                miningRanking.loadConfig();
            }
        }.runTaskTimer(this,rankingTicks,rankingTicks);
        //  LoadConfig
        loadConfig();
        //  LoadData
        loadMiningData();
        loadScoreBoard();
        //  Prefix
        blockVault.loadPrefix();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        //  SaveConfig
        blockVault.saveVaultMap();
        blockWorld.saveWorldList();
        saveMiningData();
        saveConfig();
    }

    public void loadConfig(){
        mode = getConfig().getBoolean("mode");
        try{
            prefix = getConfig().getString("prefix").replace("&","§");
        }catch (NullPointerException e){
            e.printStackTrace();
            prefix = "§e[§6Block§aMining§e]§f";
        }
    }

    public void saveConfig(){
        this.getConfig().set("mode", mode);
        this.saveConfig();
    }

    private void loadMiningData(){
        miningDataMap.clear();
        for(Player player : Bukkit.getOnlinePlayers()){
            MiningData data = new MiningData(player);
            miningDataMap.put(player,data);
        }
    }

    private void saveMiningData(){
        for(MiningData data : miningDataMap.values()){
            data.saveData();
        }
    }

    private void loadScoreBoard(){
        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            //  ScoreBoard
            MoneyScoreBoard moneyScoreBoard = new MoneyScoreBoard(player);
            plugin.scoreBoardMap.put(player,moneyScoreBoard);
        }
    }

    public static BlockMining getPlugin(){
        return plugin;
    }
}
