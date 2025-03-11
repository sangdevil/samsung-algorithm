package samsung_22_21A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
    public static class Gun{
        int x, y, ad;
        Gun(int x, int y, int ad){
            this.x = x;
            this.y = y;
            this.ad = ad;
        }
    }
    public static class Player {
        int x, y, ad, num, dirNum, point;
        int bx, by;
        Gun gun;
        Player(int x, int y, int ad, int num, int dirNum) {
            this.x = x;
            this.y = y;
            this.ad = ad;
            this.num = num;
            this.dirNum = dirNum;
            this.point = 0;
            this.gun = null;
        }
    }
    public static int[][] playerMap;
    public static Gun[] guns;
    public static Player[] players;
    public static int n,m,k;
    public static int[][] directions = new int[][]{{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

    public static void pickOneGun(Player p) {
//        System.out.printf("pick gun\n");
        Gun pickedGun = null;
        for (Gun gun : guns) {
            if (gun.x == p.x && gun.y == p.y) {
//                System.out.printf("gun %d exist,", gun.ad);
                if (pickedGun == null) {
                    pickedGun = gun;
                } else {
                    if (gun.ad > pickedGun.ad) {
                        pickedGun = gun;
                    }
                }
            }
        }
//        System.out.println();
        if (p.gun == null) {
            p.gun = pickedGun;
        } else {
            if (pickedGun != null && pickedGun.ad > p.gun.ad) {
                p.gun = pickedGun;
            }
        }
    }

    public static void movePlayer(Player p, int nx, int ny) {
        playerMap[p.by][p.bx] = 0;
        p.x = nx;
        p.y = ny;
        playerMap[ny][nx] = p.num;
        if (p.gun != null) {
            p.gun.x = p.x;
            p.gun.y = p.y;
        }
    }

    public static void calculateCoordinate(Player p){
        int nx = p.x + directions[p.dirNum][0];
        int ny = p.y + directions[p.dirNum][1];
        if (nx < 0 || nx >= n || ny < 0 || ny >= n) {
            nx = p.x - directions[p.dirNum][0];
            ny = p.y - directions[p.dirNum][1];
            p.dirNum = (p.dirNum + 2) % 4;
        }
        p.x = nx;
        p.y = ny;
//        System.out.printf("Player %d with ad %d tried to move to (%d, %d)\n", p.num, p.ad, p.x, p.y);
    }

    public static void fight(Player p1, Player p2) {
        int p1ad = p1.gun == null ? p1.ad : p1.ad + p1.gun.ad;
        int p2ad = p2.gun == null ? p2.ad : p2.ad + p2.gun.ad;
//        System.out.printf("player (%d : %d), (%d : %d) fight\n", p1.num, p1ad, p2.num, p2ad);
        p2.bx = p2.x;
        p2.by = p2.y;
        movePlayer(p1, p1.x, p1.y);
        boolean p1Win = (p1ad == p2ad) ? (p1.ad > p2.ad) : (p1ad > p2ad);

//        System.out.printf("player %d win, player %d lose\n", p1Win ? p1.num : p2.num, p1Win ? p2.num : p1.num);
        if (p1Win) {
            p1.point += p1ad - p2ad;
            loseInteraction(p2);
            winInteraction(p1);
        } else {
            p2.point += p2ad - p1ad;
            loseInteraction(p1);
            winInteraction(p2);
        }
    }

    public static void loseInteraction(Player loser) {
        loser.gun = null;
        int nx = loser.x + directions[loser.dirNum][0];
        int ny = loser.y + directions[loser.dirNum][1];
        while (nx < 0 || nx >= n || ny < 0 || ny >= n || playerMap[ny][nx] > 0) {
            loser.dirNum = (loser.dirNum + 1) % 4;
            nx = loser.x + directions[loser.dirNum][0];
            ny = loser.y + directions[loser.dirNum][1];
        }
        movePlayer(loser, nx, ny);
        pickOneGun(loser);
        assert playerMap[loser.y][loser.x]  == loser.num;
//        System.out.printf("loser is on (%d, %d), picked %d gun\n", loser.x, loser.y, loser.gun == null ? 0 : loser.gun.ad);
    }

    public static void winInteraction(Player winner) {
        pickOneGun(winner);
        playerMap[winner.y][winner.x] = winner.num;

        assert playerMap[winner.y][winner.x]  == winner.num;
//        System.out.printf("winner is on (%d, %d), picked %d gun\n", winner.x, winner.y, winner.gun == null ? 0 : winner.gun.ad);
    }

    public static void showMap() {
        StringBuilder sb = new StringBuilder("Currently, Player map is : \n");
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                if (playerMap[y][x] > 0) {
                    sb.append("P");
                    sb.append(playerMap[y][x]);
                    sb.append(" ");
                } else {
                    sb.append("0  ");
                }
            }
            sb.append("\n");
        }
        for (Player p : players) {
            sb.append(String.format("Player %d at (%d, %d) has %d point, and gun with ad %d\n", p.num, p.x, p.y,
                    p.point, p.gun == null ? 0 : p.gun.ad));
        }
        System.out.println(sb);
    }

    public static void process() {
        for (int turn = 0; turn < k; turn++) {
//            System.out.printf("current turn : %d\n", turn);
//            showMap();
            for (Player p : players) {
                // move not committed on map.
                p.bx = p.x;
                p.by = p.y;
                calculateCoordinate(p);
                if (playerMap[p.y][p.x] > 0) {
                    fight(p, players[playerMap[p.y][p.x] - 1]);
                } else {
                    movePlayer(p, p.x, p.y);
                    pickOneGun(p);
                }
            }
        }
//        System.out.println("all turn done,");
//        showMap();
        for (Player p : players) {
            System.out.printf("%d ", p.point);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        playerMap = new int[n][n];
        List<Gun> tmpGuns = new ArrayList<>();



        for (int y = 0; y < n; y++) {
            st = new StringTokenizer(br.readLine());
            for (int x = 0; x < n; x++) {
                int ad = Integer.parseInt(st.nextToken());
//                System.out.printf("(%d,%d) : %d, ",x,y, ad);
                if (ad > 0) {
                    tmpGuns.add(new Gun(x, y, ad));
                }
            }
//            System.out.println();
        }

        guns = new Gun[tmpGuns.size()];
        int gunNum = 0;
        for (Gun gun : tmpGuns) {
            guns[gunNum++] = gun;
        }

        players = new Player[m];
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken()) - 1;
            int x = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken());
            int ad = Integer.parseInt(st.nextToken());
            players[i] = new Player(x, y, ad, i + 1, d);
        }

        for (Player p : players) {
            playerMap[p.y][p.x] = p.num;
        }


        process();


    }

}
