package net.testusuke.blockmining.Data;

import net.testusuke.blockmining.BlockMining;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class MiningRanking {
    private final BlockMining plugin;
    private String dir = "plugins/BlockMining/data";
    private File dirFile;
    private HashMap<String,Integer> scoreMap = new HashMap<>();
    private HashMap<String,Integer> playerRankingMap = new HashMap<>();
    public MiningRanking(BlockMining plugin){
        this.plugin = plugin;
        dirFile = new File(dir);
        loadConfig();
    }

    public void loadConfig(){
        try {
            clearScoreMap();
            //  List
            String[] lists = dirFile.list();
            //  load file
            for(String fileName : lists){
                File file = new File(fileName);
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                String name = config.getString("name");
                int score = config.getInt("score");
                scoreMap.put(name,score);
            }
            plugin.getLogger().info("completed load data file.");
            //  Load
            loadOnlinePlayerRanking();
        }catch (NullPointerException e){
            plugin.getLogger().warning("MiningRankingの取得に失敗。(Dataが存在しない)");
        }
    }

    public void loadOnlinePlayerRanking(){
        playerRankingMap.clear();
        new BukkitRunnable(){
            @Override
            public void run(){
                List<Map.Entry<String, Integer>> list_entries = new ArrayList<Map.Entry<String, Integer>>(scoreMap.entrySet());
                Collections.sort(list_entries, new Comparator<Map.Entry<String, Integer>>() {
                    //compareを使用して値を比較する
                    public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
                        return obj2.getValue().compareTo(obj1.getValue());
                    }
                });
                int i = 1;
                for(Map.Entry<String, Integer> entry : list_entries) {
                    playerRankingMap.put(entry.getKey(), i);
                    i++;
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public int getRanking(String name){
        if(!playerRankingMap.containsKey(name)) return 0;
        return playerRankingMap.get(name);
    }

    public HashMap<String,Integer> getScoreMap(){
        return scoreMap;
    }

    public void clearScoreMap(){
        scoreMap.clear();
    }

}
