package net.testusuke.blockmining;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class BlockVault {
    private final BlockMining plugin;
    //  BlockList
    HashMap<Material,Integer> vaultMap = new HashMap<>();
    public String guiName;
    public String materialGuiName;
    public BlockVault(BlockMining plugin){
        this.plugin = plugin;
        //  Load
        loadVaultMap();
    }

    public void loadPrefix(){
        guiName = plugin.prefix + "§a採掘マテリアル設定";
        materialGuiName = plugin.prefix + "§aブロックリスト";
    }

    public void loadVaultMap() {
        vaultMap.clear();
        try {
            for (String key : plugin.getConfig().getConfigurationSection("vaultmap").getKeys(false)) {
                int vault = plugin.getConfig().getInt("vaultmap." + key);
                vaultMap.put(Material.getMaterial(key), vault);
                plugin.getLogger().info("load config... material: " + key + " vault: " + vault);
            }
        }catch (NullPointerException e){
            plugin.getLogger().warning("データが存在しません。BlockVault");
        }
        plugin.getLogger().info("completed load material,vault hashmap");
    }

    public void openRegisterGUI(Player player){
        Inventory inventory = Bukkit.createInventory((InventoryHolder) null, 54, guiName);
        int i = 0;
        for(Material material : vaultMap.keySet()){
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(vaultMap.get(material) + "");
            item.setItemMeta(meta);
            inventory.setItem(i,item);
            i++;
        }
        player.openInventory(inventory);
    }

    public void closeRegisterGUI(Player player, Inventory inventory){
        vaultMap.clear();
        for(ItemStack item : inventory.getContents()){
            try{
                if(item.getType() ==  Material.AIR || !item.hasItemMeta())continue;
                ItemMeta meta = item.getItemMeta();
                String name = meta.getDisplayName();
                Material material = item.getType();
                if(!material.isBlock()){
                    player.sendMessage(plugin.prefix + "§cError: ブロックではありません。");
                    player.getInventory().addItem(item);
                    continue;
                }
                int vault = 0;
                try{
                    vault = Integer.parseInt(name);
                }catch (NumberFormatException e){
                    player.sendMessage(plugin.prefix + "§cError: 値の取得に失敗しました。正しく入力してください。");
                    player.getInventory().addItem(item);
                    continue;
                }
                if(vault <= 0){
                    player.sendMessage(plugin.prefix + "§cError: 値が負の数または０です。");
                    player.getInventory().addItem(item);
                    continue;
                }
                //  addMap
                vaultMap.put(material,vault);
                player.sendMessage(plugin.prefix + "§aマテリアル設定 Material: " + material.name() + " vault: " + vault);
            }catch (NullPointerException e){
                continue;
            }
        }
        player.sendMessage(plugin.prefix + "§a採掘マテリアルの設定が完了しました。");
        player.sendMessage(vaultMap.toString());
    }

    public void saveVaultMap(){
        plugin.getConfig().set("vaultmap", null);
        plugin.saveConfig();
        for(Material key: vaultMap.keySet()){
            plugin.getConfig().set("vaultmap." + key.name(), vaultMap.get(key));
        }
        plugin.saveConfig();
    }

    public int getVault(Block block){
        Material material = block.getType();
        return vaultMap.get(material);
    }

    public void openMaterialList(Player player){
        Inventory inventory = Bukkit.createInventory((InventoryHolder) null, 54, materialGuiName);
        int i = 0;
        for(Material material : vaultMap.keySet()){
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(vaultMap.get(material) + "");
            i++;
        }
        player.openInventory(inventory);
    }
    
}
