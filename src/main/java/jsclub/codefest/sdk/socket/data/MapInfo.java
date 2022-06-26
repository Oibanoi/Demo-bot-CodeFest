package jsclub.codefest.sdk.socket.data;

import com.google.gson.Gson;

import jsclub.codefest.sdk.constant.MapEncode;
import jsclub.codefest.sdk.model.Hero;
import java.util.ArrayList;
import java.util.List;

public class MapInfo {
    public String myId;
    public MapSize size;
    public List<Player> players;
    public int[][] map;
    public List<Bomb> bombs;
    public List<Spoil> spoils;
    public List<Gift> gifts;
    public List<Viruses> viruses;
    public List<Human> human;
    public List<Node> walls = new ArrayList<>();
    public List<Node> balk = new ArrayList<>();
    public List<Node> blank = new ArrayList<>();
    public List<Node> teleportGate = new ArrayList();
    public List<Node> quarantinePlace = new ArrayList();

    public Player getPlayerByKey(String key) {
        Player player = null;
        if (players != null ) {
            for (Player p : players) {
                if (key.startsWith(p.id)) {
                    player = p;
                    break;
                }
            }
        }
        return player;
    }

    public List<Viruses> getVirus() {
        return viruses;
    }

    public List<Human> getDhuman() {
        List<Human> dhumanList = new ArrayList<>();
        if(human!=null) {
            for (Human dhuman : human) {
                if (dhuman.infected) {
                    dhumanList.add(dhuman);
                }
            }
        }
        return dhumanList;
    }

    public List<Human> getNHuman() {
        List<Human> nhumanList = new ArrayList<>();
        if(human!=null) {
            for (Human nhuman : human) {
                if (!nhuman.infected && nhuman.curedRemainTime == 0) {
                    nhumanList.add(nhuman);
                }
            }
        }
        return nhumanList;
    }

    public void updateMapInfo() {
        for (int i = 0; i < size.rows; i++) {
            int[] map=new int[this.map[0].length];
            for (int j=0;j<this.map[0].length;j++)
                map[j]=this.map[i][j];
            //int[] map = this.map.get(i);
            for (int j = 0; j < size.cols; j++) {
                switch (map[j]) {
                    case MapEncode.ROAD:
                        blank.add(new Node(j,i));
                        break;
                    case MapEncode.WALL:
                        walls.add(new Node(j,i));
                        break;
                    case MapEncode.BALK:
                        balk.add(new Node(j,i));
                        break;
                    case MapEncode.TELEPORT_GATE:
                        teleportGate.add(new Node(j,i));
                        break;
                    case MapEncode.QUARANTINE_PLACE:
                        quarantinePlace.add(new Node(j,i));
                        break;
                    default:
                        walls.add(new Node(j,i));
                        break;
                }
            }
        }
    }

    public Position getEnemyPosition(Hero hero) {
        Position position = null;
        if (hero != null) {
            for (Player player : players) {
                if (!hero.getPlayerName().startsWith(player.id)) {
                    position = player.currentPosition;
                    break;
                }
            }
        }
        return position;
    }

    public Position getCurrentPosition(Hero hero) {
        Position position = null;
        if (hero != null) {
            for (Player player : players) {
                if (hero.getPlayerName().startsWith(player.id)) {
                    position = player.currentPosition;
                    break;
                }
            }
        }
        return position;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
