package net.testusuke.blockmining;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BlockWorld {
    public ArrayList<World> worldList = new ArrayList<>();
    private final BlockMining plugin;
    public BlockWorld(BlockMining plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig(){
        worldList.clear();
        try {
            for (String key : plugin.getConfig().getConfigurationSection("world").getKeys(false)) {
                worldList.add(Bukkit.getServer().getWorld(key));
            }
        }catch (NullPointerException e){
            plugin.getLogger().warning("データが存在しません。BlockWorld");
        }
        plugin.getLogger().info("completed load World List");
    }

    public void saveWorldList(){
        plugin.getConfig().set("world" ,null);
        plugin.saveConfig();
        for(World world : worldList){
            plugin.getConfig().set("world." + world.getName(), 0);
        }
        plugin.saveConfig();
    }

    public ArrayList<World> getWorldList(){
        return worldList;
    }

    public void registerWorld(Player player,World world){
        if(!worldList.contains(world)){
            player.sendMessage(plugin.prefix + "§aワールドを登録しました");
        }else {
            player.sendMessage(plugin.prefix + "§cすでに登録済です。");
        }
        worldList.add(world);
    }

    public void unregisterWorld(Player player,World world){
        if(worldList.contains(world)){
            player.sendMessage(plugin.prefix + "§aワールドを削除しました");
        }else {
            player.sendMessage(plugin.prefix + "§cすでに削除済です。");
        }
        worldList.remove(world);
    }

    public boolean contains(World world){
        return worldList.contains(world);
    }


}
