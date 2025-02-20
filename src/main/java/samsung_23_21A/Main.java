package samsung_23_21A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Main {

    public static class Knight {
        int startX, startY, w, h, maxHealth, turnDamage, currentHealth, index;
        boolean move;

        Knight(int startX, int startY, int w, int h, int health, int index) {
            this.startX = startX;
            this.startY = startY;
            this.w = w;
            this.h = h;
            this.maxHealth = health;
            this.turnDamage = 0;
            this.currentHealth = health;
            this.index = index;
            this.move = false;
        }

        public boolean isOut() {
            return startX < 0 || startX >= L || startY < 0 || startY >= L;
        }

        public boolean isAlive() {
            return currentHealth > 0;
        }

    }

    public static boolean overlapWithWall(Knight cur, int[] dir) {

        int newStartX = cur.startX + dir[0];
        int newStartY = cur.startY + dir[1];
//        System.out.printf("moved x,y : (%d, %d)\n", newStartX, newStartY);
        // overlap with wall outside
        if (newStartX < 0 || newStartX + cur.w - 1 >= L || newStartY < 0 || newStartY + cur.h - 1 >= L) {
//            System.out.println("overlap with outside wall");
            return true;
        }

        // overlap with wall inside
        for (int y = newStartY; y < newStartY + cur.h; y++) {
            for (int x = newStartX; x < newStartX + cur.w; x++) {
                if (map[y][x] == 2) {
//                    System.out.println("overlap with inside wall");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * return whether k1, moved to dir overlap with k2.
     * @param k1
     * @param k2
     * @param dir
     * @return
     */
    public static boolean overlapWithKnight(Knight k1, Knight k2, int[] dir) {
        if (!k2.isAlive() || k1.equals(k2)) return false;
        int x1 = k1.startX + dir[0];
        int y1 = k1.startY + dir[1];
        int x2 = k2.startX;
        int y2 = k2.startY;
//        System.out.printf("comparing knight %d and knight %d\n", k1.index, k2.index);
        boolean overlappedX = (x1 <= x2) ? (x2 <= x1 + k1.w - 1) : (x1 <= x2 + k2.w - 1);
        boolean overlappedY = (y1 <= y2) ? (y2 <= y1 + k1.h - 1) : (y1 <= y2 + k2.h - 1);
//        System.out.printf("x, y : %b, %b\n", overlappedX, overlappedY);
        if (overlappedX && overlappedY) {
//            System.out.println("overlapped");
            return true;
        } else {
//            System.out.println("not overlapped");
            return false;
        }
    }

    public static void addKnights(Knight curKnight, int[] dir, ArrayDeque<Knight> queue) {
        for (Knight k : knights) {
            if (overlapWithKnight(curKnight, k, dir)) {
                if (!k.move) {
                    queue.add(k);
                }
            }
        }
    }

    public static void moveKnights(int[] dir) {
        for (Knight k : knights) {
            if (k.move) {
                k.startX += dir[0];
                k.startY += dir[1];
            }
        }
    }

    public static void movechain(Knight startKnight, int[] dir) {

        if (!startKnight.isAlive()) return;
        // before move, all knight's move set to false;
        for (Knight k : knights) {
            k.move = false;
        }

        ArrayList<Knight> moveList = new ArrayList<>();
        ArrayDeque<Knight> queue = new ArrayDeque<>();
        queue.add(startKnight);
        Knight curKnight;
        boolean canMove = true;
        while (!queue.isEmpty()) {
            curKnight = queue.poll();
//            System.out.printf("check if knight %d's move overlap with wall\n", curKnight.index);
            if (overlapWithWall(curKnight, dir)) {
                canMove = false;
                break;
            }
//            System.out.printf("knight %d moved\n", curKnight.index);
            curKnight.move = true;
            addKnights(curKnight, dir, queue);
        }
        if (canMove) {
//            System.out.println("All move committed");
            moveKnights(dir);
        } else {
//            System.out.println("All moves are aborted");
            startKnight.move = false;
        }
    }

    public static int calculateDamage(Knight k) {
        if (!k.isAlive()) return 0;
        int damage = 0;
        for (int y = k.startY; y < Math.min(k.startY + k.h, L); y++) {
            for (int x = k.startX; x < Math.min(k.startX + k.w, L); x++) {
                if (map[y][x] == 1) {
                    damage += 1;
                }
            }
        }
        return damage;
    }

    public static void doDamageAmongKnights(Knight startKnight) {
        if (!startKnight.move)
            return;
        for (Knight k : knights) {
            k.turnDamage = calculateDamage(k);
        }
        startKnight.turnDamage = 0;
        for (Knight k : knights) {
            if (k.move) k.currentHealth -= k.turnDamage;
        }
    }

    public static void showKnights() {
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < L; y++) {
            for (int x = 0; x < L; x++) {
                knightMap[y][x] = 0;
            }
        }
        for (int i = 0; i < knights.length; i++) {
            Knight k = knights[i];
            if (!k.isAlive()) continue;
            sb.append(String.format("Knight %d has (%d / %d) health, and currently On (%d, %d)\n",
                    i + 1, k.currentHealth, k.maxHealth, k.startX, k.startY));
            for (int y = k.startY; y < Math.min(k.startY + k.h, L); y++) {
                for (int x = k.startX; x < Math.min(k.startX + k.w, L); x++) {
                    knightMap[y][x] = i + 1;
                }
            }
        }

        sb.append("Currently, Knights are on the map : \n");

        for (int y = 0; y < L; y++) {
            for (int x = 0; x < L; x++) {
                sb.append(knightMap[y][x]);
                if (map[y][x] == 2) {
                    sb.append("W");
                } else if (map[y][x] == 1) {
                    sb.append("B");
                } else {
                    sb.append("O");
                }
                if (knightMap[y][x] < 10){
                    sb.append(" ");
                }

                sb.append(" ");
            }
            sb.append("\n");
        }

        System.out.println(sb.toString());
    }



    public static void proceed() {
        for (int[] command : commands) {
            int knightIndex = command[0] - 1;
            int[] dir = directions[command[1]];
//            System.out.printf("King move %dth knight on %d direction\n", knightIndex + 1, command[1]);
            movechain(knights[knightIndex], dir);
            doDamageAmongKnights(knights[knightIndex]);
//            showKnights();
        }

        int totalDamage = 0;
        for (Knight k : knights) {
            if (k.isAlive()) {
                totalDamage += k.maxHealth - k.currentHealth;
            }
        }
        System.out.println(totalDamage);
    }


    public static int[][] knightMap;
    public static int[][] map;
    public static Knight[] knights;
    public static int[][] commands;
    public static int L,N,Q;
    public static int[][] directions = new int[][]{{0,-1}, {1,0}, {0,1}, {-1,0}};

    public static void initMap(StringTokenizer st, BufferedReader br) throws IOException {

        map = new int[L][L];
        // for debugging
        knightMap = new int[L][L];
        for (int y = 0; y < L; y++) {
            st = new StringTokenizer(br.readLine());
            for (int x = 0; x < L; x++) {
                map[y][x] = Integer.parseInt(st.nextToken());

                // for debugging
                knightMap[y][x] = 0;
            }
        }

        knights = new Knight[N];
        int startX,startY,w,h,maxHealth;
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            startY = Integer.parseInt(st.nextToken()) - 1;
            startX = Integer.parseInt(st.nextToken()) - 1;
            h = Integer.parseInt(st.nextToken());
            w = Integer.parseInt(st.nextToken());
            maxHealth = Integer.parseInt(st.nextToken());
            knights[i] = new Knight(startX, startY, w, h, maxHealth, i + 1);
        }

        commands = new int[Q][2];
        for (int i = 0; i < Q; i++) {
            st = new StringTokenizer(br.readLine());
            commands[i][0] = Integer.parseInt(st.nextToken());
            commands[i][1] = Integer.parseInt(st.nextToken());
        }

    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        L = Integer.parseInt(st.nextToken());
        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());
        initMap(st, br);
        proceed();
    }
}
