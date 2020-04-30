package net.testusuke.blockmining;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class MiningMoneyTask extends BukkitRunnable {
    private final BlockMining plugin;

    HashMap<Player,Integer> moneyMap = new HashMap<>();
    public MiningMoneyTask(BlockMining plugin){
        this.plugin = plugin;
    }

    public void addMoney(Player player,int vault){
        int before = moneyMap.get(player);
        moneyMap.put(player,before + vault);
    }

    public int getMoney(Player player){
        return  moneyMap.get(player);
    }

    public boolean contains(Player player){
        return moneyMap.containsKey(player);
    }

    public void removePlayer(Player player){
        moneyMap.remove(player);
    }

    @Override
    public void run(){
        for(Player player : moneyMap.keySet()){
            int vault = moneyMap.get(player);
            VaultManager.economy.depositPlayer(player,vault);
        }
        moneyMap.clear();
    }
}
