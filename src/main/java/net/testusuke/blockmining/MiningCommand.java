package net.testusuke.blockmining;

import net.testusuke.blockmining.Data.MiningData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class MiningCommand implements CommandExecutor {
    private final BlockMining plugin;
    public MiningCommand(BlockMining plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("You can not use console!");
            return false;
        }

        if(args.length == 0){
            Player player = (Player)sender;
            sendMiningData(player);
        }

        if(args.length == 1){
            Player player = (Player)sender;
            if(args[0].equalsIgnoreCase("help")){
                sendHelp(player);
                return true;
            }

            if(args[0].equalsIgnoreCase("block")){
                if(!plugin.mode){
                    player.sendMessage(plugin.prefix + "§c現在使用できません。");
                    return false;
                }
                plugin.blockVault.openMaterialList(player);
            }

            if(args[0].equalsIgnoreCase("register")){
                if(!player.hasPermission("blockmining.admin")){
                    player.sendMessage(plugin.prefix + "§cあなたには権限あありません。");
                }
                player.sendMessage(plugin.prefix + "§a設定GUIを開きます。");
                plugin.blockVault.openRegisterGUI(player);
                return true;
            }

            if(args[0].equalsIgnoreCase("registerworld")){
                if(!player.hasPermission("blockmining.admin")){
                    player.sendMessage(plugin.prefix + "§cあなたには権限あありません。");
                }
                plugin.blockWorld.registerWorld(player,player.getWorld());
                return true;
            }
            if(args[0].equalsIgnoreCase("unregisterworld")){
                if(!player.hasPermission("blockmining.admin")){
                    player.sendMessage(plugin.prefix + "§cあなたには権限あありません。");
                }
                plugin.blockWorld.unregisterWorld(player,player.getWorld());
                return true;
            }

            if(args[0].equalsIgnoreCase("reload")){
                if(!player.hasPermission("blockmining.admin")){
                    player.sendMessage(plugin.prefix + "§cあなたには権限あありません。");
                }
                plugin.blockVault.saveVaultMap();
                plugin.blockWorld.saveWorldList();
                plugin.saveConfig();
                plugin.reloadConfig();
                plugin.loadConfig();
                plugin.blockVault.loadVaultMap();
                plugin.blockWorld.loadConfig();
                return true;
            }

            //  On
            if(args[0].equalsIgnoreCase("on")){
                if(!player.hasPermission("blockmining.admin"))return false;
                if(plugin.mode){
                    sendMessage(player,"§cすでにONになっています");
                }
                plugin.mode = true;
                sendMessage(player,"§aONになりました");
                return true;
            }
            //  Off
            if(args[0].equalsIgnoreCase("off")){
                if(!player.hasPermission("blockmining.admin"))return false;
                if(!plugin.mode){
                    sendMessage(player,"§cすでにOFFになっています");
                }
                plugin.mode = false;
                sendMessage(player,"§aOFFになりました");
                return true;
            }

            //  Ranking
            if(args[0].equalsIgnoreCase("ranking")){
                if(!plugin.mode){
                    player.sendMessage(plugin.prefix + "§c現在利用できません");
                    return false;
                }
                player.sendMessage(plugin.prefix + "§6ランキングを表示します。");
                showRanking(player);
                return true;
            }
        }
        return false;
    }

    private void sendHelp(Player player) {
        sendMessageNo(player, "§6======================================");
        sendMessageNo(player, "§e/bm </mining> <- ヘルプの表示");
        sendMessageNo(player, "/bm block <- 対象のブロックリストを表示します");
        sendMessageNo(player, "§e/bm ranking <- ランキングの表示");
        if(player.hasPermission("blockmining.admin")){
            sendMessageNo(player, "§c/bm on <- モードをONにします");
            sendMessageNo(player, "§c/bm off <- モードをOFFにします");
            sendMessageNo(player, "§c/bm reload <- コンフィグを再読み込みします");
            sendMessageNo(player, "§c/bm register <- 対象のブロックを設定します");
            sendMessageNo(player, "§cブロックを用意して名前に価格を書いて、GUIに入れてください");
            sendMessageNo(player, "§c/bm registerworld <- 対象のワールドを設定します(設定者のいるワールドが設定します)");
            sendMessageNo(player, "§c/bm unregisterworld <- 対象のワールドから削除します");
        }
        sendMessageNo(player, "§d§lCreated by " + plugin.author + " version: " + plugin.version);
        sendMessageNo(player, "§6======================================");
    }

    public void sendMiningData(Player player){
        if(!plugin.mode){
            player.sendMessage(plugin.prefix + "§c現在使用できません");
            return;
        }
        MiningData mb = plugin.miningDataMap.get(player);
        if (mb == null) {
            player.sendMessage(plugin.prefix + "§cデータがありません。");
            return;
        }
        int blocks = mb.getScore();
        int money = mb.getMoney();

        player.sendMessage(plugin.prefix + "§aあなたのスコア");
        player.sendMessage("採掘したブロック数: " + blocks);
        player.sendMessage("獲得金額: " + money);
        player.sendMessage("あなたのランキング: " + plugin.miningRanking.getRanking(player.getName()));

    }

    public void showRanking(Player player){
        sendMessageNo(player,"§c§l---=====>>> §a§lMining §6§lRanking§c§l <<<=====---");
        HashMap<String,Integer> rankingMap = plugin.miningRanking.getScoreMap();
        List<Map.Entry<String, Integer>> list_entries = new ArrayList<Map.Entry<String, Integer>>(rankingMap.entrySet());
        Collections.sort(list_entries, new Comparator<Map.Entry<String, Integer>>() {
            //compareを使用して値を比較する
            public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
                return obj2.getValue().compareTo(obj1.getValue());
            }
        });
        int i = 1;
        for(Map.Entry<String, Integer> entry : list_entries) {
            if(i > 10)break;
            String blocks = String.format("%,d", entry.getValue());
            player.sendMessage("§6§l" + i + "位 " + entry.getKey() + " §b§l" + blocks + " §a§lBlocks");
            i++;
        }

    }

    private void sendMessage(Player player,String message){
        player.sendMessage(plugin.prefix + message);
    }
    private void sendMessageNo(Player player,String message){
        player.sendMessage(message);
    }
}
