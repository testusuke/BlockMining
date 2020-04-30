package net.testusuke.blockmining.Data;

import net.testusuke.blockmining.BlockMining;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class MiningData {
    private Player player;
    private int score;
    private int money;
    private String fileName;
    private File file;
    private YamlConfiguration config;

    public MiningData(Player player){
        this.player = player;
        try {
            this.fileName = player.getUniqueId().toString() + ".yml";
            //  Config
            File directory = BlockMining.getPlugin().getDataFolder();
            if (!directory.exists()) directory.mkdir();
            file = new File(directory, fileName);
            if (!file.exists()) {
                file.createNewFile();
                config = YamlConfiguration.loadConfiguration(file);
                config.set("name", player.getName());
                config.set("score", 0);
                config.set("money", 0);
                BlockMining.getPlugin().getLogger().info("create new file. player: " + player.getName());
            } else {
                config = YamlConfiguration.loadConfiguration(file);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        loadData();
    }

    public void loadData(){
        score = config.getInt("score");
        money = config.getInt("money");
    }

    public void saveData(){
        config.set("score", score);
        config.set("money", money);
        try{
            config.save(file);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void addMoney(int vault){
        money += vault;
    }

    public int getScore(){
        return score;
    }

    public int getMoney(){
        return money;
    }
}
