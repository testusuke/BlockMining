package net.testusuke.blockmining;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;

public class MoneyScoreBoard {
    //  BlockMining
    private BlockMining plugin = BlockMining.getPlugin();
    private Player player;
    private ScoreboardManager manager;
    private Scoreboard scoreboard;
    //  Objective
    private Objective objective;
    //  Vault
    private ArrayList<Integer> vaultList = new ArrayList<>();

    public MoneyScoreBoard(Player player){
        this.player = player;
        createScoreBoard(player);
        int ticks = 20 * 3;
        loadScore(ticks);
    }

    private void createScoreBoard(Player player){
        manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("score_" + player.getName(),"dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§6Block §aMining");
        Score score = objective.getScore("§6§lランキング: " + plugin.miningRanking.getRanking(player.getName()) + " 位");
        score.setScore(12);
        Score score1 = objective.getScore("取得額");
        score1.setScore(11);

        player.setScoreboard(scoreboard);
    }

    public void addMoney(int vault){
        vaultList.add(vault);
    }

    public synchronized ArrayList<Integer> getLastVaultList(){
        int index = vaultList.size();
        if(index == 0){
            return null;
        }
        ArrayList<Integer> lastVaultList = new ArrayList<>();
        for(int i = 1; i < 10; i++){
            int when = vaultList.get(index - i);
            if(when == 0){
                break;
            }
            lastVaultList.add(when);

        }
        return  lastVaultList;
    }

    public void loadScore(int ticks){
        new BukkitRunnable(){
            @Override
            public void run(){
                //scoreboard.getObjective("score_" + player.getName());
                /*
                objective = null;
                objective = scoreboard.registerNewObjective("score_" + player.getName() , "dummy");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                objective.setDisplayName("§6Block §aMining");
                */
                scoreboard.resetScores(player);
                Score score = objective.getScore("§6§lランキング: " + plugin.miningRanking.getRanking(player.getName()) + " 位");
                score.setScore(12);
                Score score1 = objective.getScore("取得額");
                score1.setScore(11);

                ArrayList<Integer> vaultList = getLastVaultList();
                if(vaultList != null) {
                    int i = 10;
                    for (int vault : vaultList) {
                        if (i <= 0) {
                            break;
                        }
                        Score score_vault = objective.getScore("§l+" + vault);
                        score_vault.setScore(i);
                        i--;
                    }
                }
                player.setScoreboard(scoreboard);
            }
        }.runTaskTimer(plugin,ticks,ticks);
    }

}
