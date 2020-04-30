package net.testusuke.blockmining;

import net.testusuke.blockmining.Data.MiningData;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.io.File;
import java.util.List;

public class BlockMiningEvent implements Listener {
    private BlockMining plugin;
    public BlockMiningEvent(BlockMining plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBlake(BlockBreakEvent event){
        if(!plugin.mode)return;
        Block block = event.getBlock();
        if(isPlaceBlock(block))return;
        if(!plugin.blockWorld.contains(block.getLocation().getWorld())) return;
        Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.CREATIVE)return;
        int vault = plugin.blockVault.getVault(block);
        plugin.moneyTask.addMoney(player,vault);
        plugin.scoreBoardMap.get(player).addMoney(vault);
        plugin.miningDataMap.get(player).addMoney(vault);
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        String inventoryName = inventory.getName();
        if(!inventoryName.equalsIgnoreCase(plugin.blockVault.guiName))return;
        if(!player.hasPermission("blockmining.register"))return;
        plugin.blockVault.closeRegisterGUI(player,inventory);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Block block = event.getBlock();
        if(!plugin.mode)return;
        if(!plugin.blockWorld.contains(block.getWorld()))return;
        setPlaceBlock(block);
    }

    public void setPlaceBlock(Block block){
        block.setMetadata("placeblock", new FixedMetadataValue(plugin, true));
    }

    public boolean isPlaceBlock(Block block){
        List<MetadataValue> list = block.getMetadata("placeblock");
        for(MetadataValue metadataValue : list){
            return metadataValue.asBoolean();
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        //  ScoreBoard
        MoneyScoreBoard moneyScoreBoard = new MoneyScoreBoard(player);
        plugin.scoreBoardMap.put(player,moneyScoreBoard);
        //  MiningData
        MiningData miningData = new MiningData(player);
        plugin.miningDataMap.put(player,miningData);
        //  Logger
        plugin.getLogger().info("load data of " + player.getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(!plugin.moneyTask.contains(player))return;
        int vault = plugin.moneyTask.getMoney(player);
        plugin.moneyTask.removePlayer(player);
        VaultManager.economy.depositPlayer(player,vault);
        plugin.miningDataMap.get(player).saveData();
        //  Remove from HashMap
        plugin.miningDataMap.remove(player);
        plugin.scoreBoardMap.remove(player);
    }

    @EventHandler
    public void  onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(event.getInventory().getName().equalsIgnoreCase(plugin.blockVault.materialGuiName)){
            event.setCancelled(true);
            player.sendMessage(plugin.prefix + "§cアイテムを取り出すことはできません。");
        }
    }
}
