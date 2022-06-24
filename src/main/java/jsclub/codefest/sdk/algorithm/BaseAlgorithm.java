package jsclub.codefest.sdk.algorithm;

import jsclub.codefest.sdk.constant.ClientConfig;
import jsclub.codefest.sdk.model.Bomberman;
import jsclub.codefest.sdk.socket.data.Node;
import jsclub.codefest.sdk.socket.data.Dir;
import jsclub.codefest.sdk.socket.data.Player;
import jsclub.codefest.sdk.socket.data.Position;

import yonko.codefest.service.socket.data.MapInfo;

import java.util.*;

public class BaseAlgorithm {
    int mMapWidth, mMapHeight;

    /**
     * Calculate Manhattan distance
     * @param src source position
     * @param des destination position
     * @return shortest distance
     */
    public int manhattanDistance(Position src, Position des) {
        return Math.abs(src.getX() - des.getX()) + Math.abs(src.getY() - des.getY());
    }
    String getStepsInString(Node first, Stack<Node> path, int numOfSteps) {
        StringBuilder steps = new StringBuilder();
        Node previousStep = first;
        int size = numOfSteps == ClientConfig.GO_FULL_PATH ? path.size() : numOfSteps;
        for (int i = 0; i <= size; i++) {
            if (path.size() > 0) {
                Node nextStep = path.pop();
                int x = nextStep.getX();
                int y = nextStep.getY();
                if (x > previousStep.getX() && y == previousStep.getY()) {
                    steps.append(Dir.RIGHT);
                }
                if (x < previousStep.getX() && y == previousStep.getY()) {
                    steps.append(Dir.LEFT);
                }
                if (x == previousStep.getX() && y > previousStep.getY()) {
                    steps.append(Dir.DOWN);
                }
                if (x == previousStep.getX() && y < previousStep.getY()) {
                    steps.append(Dir.UP);
                }
                previousStep = nextStep;
            } else {
                break;
            }
        }
        return steps.toString();
    }
    protected double distanceBetweenTwoPoints(Position p1, Position p2) {
        //return Math.abs(src.getX() - des.getX()) + Math.abs(src.getY() - des.getY());
        return distanceBetweenTwoPoints(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }
    public String getPathToEnemy(Bomberman myPlayer, MapInfo info) {
        Bomberman cloneBommer = Bomberman.clone(myPlayer);
        Player enemyPlayer = cloneBommer.getEnemyPlayer();
        Node enemyPos = new Node(enemyPlayer.currentPosition.getX(), enemyPlayer.currentPosition.getY());
        AStarSearch algorithm = new AStarSearch();
        algorithm.updateMapProp(mMapWidth, mMapHeight);
        cloneBommer.setBombs(info.bombs, cloneBommer.getEnemyPlayer().power, true);
        cloneBommer.setBoxs(info.boxs);
        List<Node> restrictNode = new ArrayList<>();
        restrictNode.addAll(cloneBommer.getWalls());
        restrictNode.addAll(cloneBommer.getBombs());
        restrictNode.addAll(cloneBommer.getBoxs());
        restrictNode.addAll(cloneBommer.getSelfisolatedZone());
        restrictNode.addAll(cloneBommer.getVirusLists());
        restrictNode.addAll(cloneBommer.getdangerHumanLists());
        cloneBommer.setRestrictedNodes(restrictNode);
        return algorithm.aStarSearch(cloneBommer, enemyPos, FULL_STEP);
    }
    public void updateMapProp(int width, int height) {
        mMapWidth = width;
        mMapHeight = height;
    }
    public String getEscapePath(Bomberman ownBomPlayer, MapInfo mapInfo) {
        Bomberman cloneBommer = Bomberman.clone(ownBomPlayer);
        //1.Set bomb without effect tile for find escape way even through bomb effect tile
        //Get Blank space without bomb tile including
        List<Node> safeNodes = ownBomPlayer.getBlankPlace();
        safeNodes.removeAll(ownBomPlayer.getBombs());

        List<Node> restrictNode = new ArrayList<>();
        restrictNode.addAll(cloneBommer.getWalls());
        restrictNode.addAll(cloneBommer.getVirusLists());
        restrictNode.addAll(cloneBommer.getdangerHumanLists());
        restrictNode.addAll(cloneBommer.getBoxs());
        restrictNode.addAll(cloneBommer.getSelfisolatedZone());
        if (safeNodes.contains(ownBomPlayer.getPosition())) {
            restrictNode.addAll(cloneBommer.dangerBombs);
        } else {
            restrictNode.addAll(cloneBommer.getBombs());
        }
        safeNodes.removeAll(restrictNode);
        cloneBommer.setRestrictedNodes(restrictNode);
        //Find way out
        Map<Node, Stack<Node>> pathToAllSafePlace = sortByComparator(getPathsToFood(cloneBommer, safeNodes, true), false);
        if (pathToAllSafePlace.isEmpty()) {
            restrictNode.clear();
            cloneBommer.setBombs(mapInfo.bombs, cloneBommer.getEnemyPlayer().power, false);
            cloneBommer.setVirusLists(mapInfo.getVirus(),true);
            cloneBommer.setDangerHumanList(mapInfo.getDhuman(),true);
            restrictNode.addAll(cloneBommer.getBombs());
            restrictNode.addAll(cloneBommer.getBoxs());
            restrictNode.addAll(cloneBommer.getSelfisolatedZone());
            restrictNode.addAll(cloneBommer.getVirusLists());
            restrictNode.addAll(cloneBommer.getdangerHumanLists());
            cloneBommer.setRestrictedNodes(restrictNode);
            safeNodes.removeAll(restrictNode);
            pathToAllSafePlace = sortByComparator(getPathsToFood(cloneBommer, safeNodes, false), false);
        }

        AStarSearch algorithm = new AStarSearch();
        algorithm.updateMapProp(mMapWidth, mMapHeight);
        for (Map.Entry<Node, Stack<Node>> path : pathToAllSafePlace.entrySet()) {

            String steps = algorithm.aStarSearch(cloneBommer, path.getKey(), -1);
            if (!steps.isEmpty()) {

                return steps;
            }
        }
        return Dir.INVALID;
    }
    private Map<Node, Stack<Node>> sortByComparator(Map<Node, Stack<Node>> unsortedMap, boolean isReverse) {
        List<Map.Entry<Node, Stack<Node>>> list = new LinkedList<>(unsortedMap.entrySet());
        list.sort((o1, o2) -> {
            double o1Path = o1.getValue().size() / o1.getKey().getV();
            double o2Path = o2.getValue().size() / o2.getKey().getV();
            return Double.compare(o1Path, o2Path);
        });
        if (isReverse) {
            Collections.reverse(list);
        }
        Map<Node, Stack<Node>> listFood = new LinkedHashMap<>();
        for (Map.Entry<Node, Stack<Node>> entry : list) {
            Node food = Node.createFromPosition(entry.getKey());
            listFood.put(food, entry.getValue());
        }
        return listFood;
    }
    public Map<Node, Stack<Node>> getPathsToFood(Bomberman player,List<Node> targets, boolean isCollectSpoils)
    {
        AStarSearch a=new AStarSearch();
        a.updateMapProp(mMapWidth,mMapHeight);
        return a.getPathsToAllFoods(player,targets,isCollectSpoils);
    }
    private double distanceBetweenTwoPoints(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public static final int FULL_STEP = -1;
}
