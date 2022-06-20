package jsclub.codefest.bot;

import io.socket.emitter.Emitter.Listener;
import jsclub.codefest.bot.constant.GameConfig;
import jsclub.codefest.sdk.algorithm.AStarSearch;
import jsclub.codefest.sdk.algorithm.BaseAlgorithm;
import jsclub.codefest.sdk.model.Bomberman;
import jsclub.codefest.sdk.model.Hero;
import jsclub.codefest.sdk.socket.data.GameInfo;
import jsclub.codefest.sdk.socket.data.Node;
import jsclub.codefest.sdk.socket.data.Player;
import jsclub.codefest.sdk.util.GameUtil;
import yonko.codefest.service.socket.data.MapInfo;
import java.util.Random;

public class Main {
    public static String getRandomPath() {
        Random rand = new Random();
        int random_integer = rand.nextInt(5);

        return "1234b".charAt(random_integer) + "";
    }
    public static String tactic(GameInfo gameInfo){
        MapInfo map=gameInfo.map_info;
        Player me=map.getPlayerByKey("player1-xxx");
        Bomberman b1=new Bomberman();
        b1.initPlayerInfo(me,map);
        Node target=new Node(20,7);
        AStarSearch a=new AStarSearch();
        a.updateMapProp(map.size.cols, map.size.rows);
        //System.out.println(me.toString());
        String fullStep = a.aStarSearch(b1, target, BaseAlgorithm.FULL_STEP);
        System.out.println(fullStep);
        return fullStep;
    }

    public static void main(String[] aDrgs) {
        Hero player1 = new Hero("player1-xxx", GameConfig.GAME_ID);
        Listener onTickTackListener = objects -> {
            GameInfo gameInfo = GameUtil.getMapInfo(objects);
            //chienthuat()
            player1.move(tactic(gameInfo));
        };
        player1.setOnTickTackListener(onTickTackListener);
        player1.connectToServer();
    }
}
