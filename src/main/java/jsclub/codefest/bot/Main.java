package jsclub.codefest.bot;

import io.socket.emitter.Emitter.Listener;
import jsclub.codefest.bot.constant.GameConfig;
import jsclub.codefest.sdk.algorithm.AStarSearch;
import jsclub.codefest.sdk.algorithm.BaseAlgorithm;
import jsclub.codefest.sdk.model.Bomberman;
import jsclub.codefest.sdk.model.Hero;
import jsclub.codefest.sdk.socket.data.*;
import jsclub.codefest.sdk.util.GameUtil;

import java.util.*;

public class Main {
    public static String getRandomPath() {
        Random rand = new Random();
        int random_integer = rand.nextInt(5);

        return "1234b".charAt(random_integer) + "";
    }
    public static String tactic(GameInfo gameInfo) {
        //set up
        //setup(gameInfo);
        //
        MapInfo map = gameInfo.map_info;
        Player me = map.getPlayerByKey("player1-xxx");
        Bomberman b1 = new Bomberman();
        AStarSearch a=new AStarSearch();
        BaseAlgorithm base = new BaseAlgorithm();
        //set up
        map.updateMapInfo();
        b1.initPlayerInfo(me, map);
        b1.setBombs(map.bombs,b1.getEnemyPlayer().power,true);
        b1.setVirusLists(map.getVirus(),true);
        b1.setBoxs(map.balk);
        b1.setDangerHumanList(map.getNHuman(),true);
        //base.updateMapProp(map.size.cols, map.size.rows);
        //a.updateMapProp(map.size.cols, map.size.rows);
        //test output

        //tactic
//        System.out.println(b1.isEndanger(b1.getPosition(),map.getVirus(),map.getDhuman()));
//        //get path to safe place if player in danger
//        if (b1.isEndanger(b1.getPosition(),map.getVirus(),map.getDhuman())) {
        //String path = base.getEscapePath(b1,map);
////            if (path.isEmpty() || path.equals("")) {
////                path = hungry();
////            }
//           // return Dir.INVALID;
//            return path;
//        }

        //get food to eat
//        List <Node> foods=b1.listShouldEatSpoils;
//foods.removeAll(b1.boxsCanBreak);
//        System.out.println("Player:"+b1.getPosition());
//        for (Node i: foods) {
//            System.out.println(i);
//        }
//        Map<Node, Stack<Node>> findfood=base.getPathsToFood(b1,foods,true);
//        for (Map.Entry<Node, Stack<Node>> path : findfood.entrySet())
//        {
//            String step=a.aStarSearch(b1, path.getKey(),2);
//            if (!step.isEmpty())
//            {
//                //return Dir.INVALID;
//                return step;
//            }
//        }
        //get box to break
        List <Node> safeNode=b1.boxsCanBreak;
        safeNode.addAll(b1.getBlankPlace());
        safeNode.addAll(b1.listShouldEatSpoils);
        safeNode.removeAll(b1.getBombs());
        safeNode.removeAll(b1.getWalls());
        safeNode.removeAll(b1.dangerBombs);
        safeNode.removeAll(b1.getSelfisolatedZone());
        safeNode.removeAll(b1.getVirusLists());
        safeNode.removeAll(b1.getdangerHumanLists());
        List<Node> restrictedNode=new ArrayList<>();
        restrictedNode.addAll(b1.getBombs());
        restrictedNode.addAll(b1.getWalls());
        restrictedNode.addAll(b1.dangerBombs);
        restrictedNode.addAll(b1.getSelfisolatedZone());
        restrictedNode.addAll(b1.getVirusLists());
        restrictedNode.addAll(b1.getdangerHumanLists());
        Collections.sort(safeNode, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {

                if (o1.V==o2.V)
                    return  base.manhattanDistance(o1, b1.getPosition())-base.manhattanDistance(o2, b1.getPosition());
                return (int) (o2.V-o1.V);
            }
        });
        System.out.println(b1.getPosition());
        for (Node i:restrictedNode)
        {
            System.out.println(i+" value:"+i.V);
        }
        System.out.println(restrictedNode.get(0));
        System.out.println(b1.getPosition());
        System.out.println("-------------");
        String step=a.aStarSearch(map.map,restrictedNode,me.currentPosition,safeNode.get(0)); //   a.aStarSearch(map.map,restrictedNode,me.currentPosition, safeNode.get(0));
        if (!step.isEmpty())
        {
            //return Dir.INVALID;
            System.out.println("b"+step);
            return "b"+step;
        }
        //findfood.clear();
//        Map<Node, Stack<Node>>
//                findfood=base.getPathsToFood(b1,safeNode,true);
//        for (Map.Entry<Node, Stack<Node>> path : findfood.entrySet())
//        {
//            String step=a.aStarSearch(b1, path.getKey(),2);
//            if (!step.isEmpty())
//            {
//                //return Dir.INVALID;
//                System.out.println("b"+step);
//                return "b"+step;
//            }
//        }
//        System.out.println(me.toString());
//         String fullStep = a.aStarSearch(b1, target, BaseAlgorithm.FULL_STEP);
//         System.out.println(fullStep);
//         return fullStep;
        return Dir.INVALID;
    }
    public static void main(String[] aDrgs) {
        Hero player1 = new Hero("player1-xxx", GameConfig.GAME_ID);
        Listener onTickTackListener = objects -> {
            GameInfo gameInfo = GameUtil.getGameInfo(objects);
            MapInfo mapInfo = gameInfo.map_info;

            Position currentPosition = mapInfo.getCurrentPosition(player1);
            Position enemyPosisiton = mapInfo.getEnemyPosition(player1);

            List<Position> restrictPosition =  new ArrayList<Position>();



            player1.move(tactic(gameInfo));
        };
        player1.setOnTickTackListener(onTickTackListener);
        player1.connectToServer();
    }
}
