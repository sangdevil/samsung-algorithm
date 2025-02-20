package Samsung_24_22A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {

    public static int mx,my;

    public static int[][] directions = new int[][]{{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
    public static int N, M, startX,startY, parkX, parkY;
    public static Medusa medusa;
    public static int dist(int x, int y) {
        return Math.abs(mx -x) + Math.abs(my - y);
    }

    public static class Pair {
        int number;
        int direction;
        public Pair(int number, int direction) {
            this.number = number;
            this.direction = direction;
        }

    }
    public static class Solider {
        int x;
        int y;

        public Solider(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean isStoned() {
            return soldierMap[y][x];
        }

        public boolean faceMedusa() {
            return x == mx && y == my;
        }

        public void rotate() {
            int tmp = this.x;
            this.x = N - this.y - 1;
            this.y = tmp;
        }


        public int moveFirst() {
            // up, down, left, right
            int d = dist(this.x, this.y);
            // up
            int nx = this.x;
            int ny = this.y - 1;
            if (dist(nx, ny) < d && !soldierMap[ny][nx]) {
                this.x = nx;
                this.y = ny;
                return 1;
            }
            // down
            nx = this.x;
            ny = this.y + 1;
            if (dist(nx, ny) < d && !soldierMap[ny][nx]) {
                this.x = nx;
                this.y = ny;
                return 1;
            }
            // left
            nx = this.x - 1;
            ny = this.y;
            if (dist(nx, ny) < d && !soldierMap[ny][nx]) {
                this.x = nx;
                this.y = ny;
                return 1;
            }
            // right
            nx = this.x + 1;
            ny = this.y;
            if (dist(nx, ny) < d && !soldierMap[ny][nx]) {
                this.x = nx;
                this.y = ny;
                return 1;
            }
            return 0;
        }

        public int moveSecond() {
            // left, right, up, down
            int d = dist(this.x, this.y);
            // left
            int nx = this.x - 1;
            int ny = this.y;
            if (dist(nx, ny) < d && !soldierMap[ny][nx]) {
                this.x = nx;
                this.y = ny;
                return 1;
            }
            // right
            nx = this.x + 1;
            ny = this.y;
            if (dist(nx, ny) < d && !soldierMap[ny][nx]) {
                this.x = nx;
                this.y = ny;
                return 1;
            }
            // up
            nx = this.x;
            ny = this.y + 1;
            if (dist(nx, ny) < d && !soldierMap[ny][nx]) {
                this.x = nx;
                this.y = ny;
                return 1;
            }
            // down
            nx = this.x;
            ny = this.y - 1;
            if (dist(nx, ny) < d && !soldierMap[ny][nx]) {
                this.x = nx;
                this.y = ny;
                return 1;
            }
            return 0;
        }
    }

    public static class Medusa {
        int x;
        int y;
        Medusa(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public void rotate() {
            int tmp = this.x;
            this.x = N - this.y - 1;
            this.y = tmp;
        }
    }
    public static class MedusaNode {
        Medusa medusa;
        ArrayList<Medusa> route;
        MedusaNode(Medusa medusa) {
            this.medusa = medusa;
            this.route = new ArrayList<>();
        }
    }

    public static int[][] bfs() {

        Medusa[][] previousMedusaMap = new Medusa[N][N];
        Medusa node = new Medusa(mx, my);
        Queue<Medusa> queue = new LinkedList<>();
        queue.add(node);
        while (!queue.isEmpty()) {
            node = queue.poll();
//            System.out.printf("current : %d, %d\n", node.x, node.y);
            if (node.x == parkX && node.y == parkY) {
                break;
            }
            for (int[] dir : directions) {
                int nx = node.x + dir[0];
                int ny = node.y + dir[1];
//                System.out.printf("next : %d, %d\n", nx, ny);
                if (nx < 0 || ny < 0 || nx >= N || ny >= N || medusaMap[ny][nx]) {
                    continue;
                }
                if (previousMedusaMap[ny][nx] == null) {
                    previousMedusaMap[ny][nx] = node;
                    Medusa newNode = new Medusa(nx, ny);
                    queue.add(newNode);
                }
            }
        }
        ArrayList<Medusa> medusaRoute = new ArrayList<>();
        medusaRoute.add(new Medusa(parkX, parkY));
        node = previousMedusaMap[parkY][parkX];
        if (node == null) {
            return null;
        }
        while (node.x != startX || node.y != startY) {
            medusaRoute.add(node);
            node = previousMedusaMap[node.y][node.x];
        }
        int[][] ret = new int[medusaRoute.size()][2];
        for (int i = 0; i < medusaRoute.size(); i++) {
            ret[medusaRoute.size() - i - 1][0] = medusaRoute.get(i).x;
            ret[medusaRoute.size() - i - 1][1] = medusaRoute.get(i).y;
        }

        return ret;

    }


    public static ArrayList<Solider> soldiers;
    public static boolean[][] medusaMap;
    public static boolean[][] soldierMap;

    public static void initSoliders(StringTokenizer st, BufferedReader br) throws IOException {
        st = new StringTokenizer(br.readLine());
        soldiers = new ArrayList<>();
        for (int i = 0; i < M; i++) {
            int y = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            soldiers.add(new Solider(x, y));
        }

    }
    public static void initMap(StringTokenizer st, BufferedReader br) throws IOException {
        medusaMap = new boolean[N][N];
        soldierMap = new boolean[N][N];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                medusaMap[i][j] = Integer.parseInt(st.nextToken()) == 1;
            }
        }
    }

    public static void rotateMap() {
        // on counterClockWise

//        System.out.println("before rotate");
//        showMap(soldierMap);

        for (int y = 0; y < N; y++) {
            for (int x = y; x < N; x++) {
                boolean tmp = soldierMap[y][x];
                soldierMap[y][x] = soldierMap[x][y];
                soldierMap[x][y] = tmp;
            }
        }
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N / 2; x++) {
                boolean tmp = soldierMap[y][x];
                soldierMap[y][x] = soldierMap[y][N - 1 - x];
                soldierMap[y][N - 1 - x] = tmp;
            }
        }

//        System.out.println("after rotate");
//        showMap(soldierMap);
    }

    public static void rotateCharacters() {
        for (Solider soldier : soldiers) {
            soldier.rotate();
        }
        // rotate Medusa
        int tmp = mx;
        mx = N - my - 1;
        my = tmp;
    }

    public static void fillZeroOnSoldierMap() {
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                soldierMap[y][x] = false;
            }
        }
    }
    public static void fillStoneOfMedusaSight() {

        // fill 1 on medusa's sight
        for (int y = my + 1; y <= N - 1; y++) {
            int low = Math.max(0, mx - y + my);
            int high = Math.min(mx + y - my, N - 1);
            for (int x = low; x <= high; x++) {
                soldierMap[y][x] = true;
            }
        }
    }

    public static void fillZeroOfSoliderSight(Solider soldier) {
        // fill 0 on soldier's sight
        int sx = soldier.x;
        int sy = soldier.y;

        // if soldier's y is smaller than medusa, pass.
        if (my >= sy) return;
        // if on same x
        if (sx == mx) {
            for (int y = sy + 1; y <= N - 1; y++) {
                soldierMap[y][sx] = false;
            }
        } else {
            float m = (float) (sy - my) / (sx - mx);
            if (m >= 1) {
                for (int y = sy + 1; y <= N - 1; y++) {
                    int high = Math.min(sx + y - sy, N - 1);
                    for (int x = sx; x <= high; x++) {
                        soldierMap[y][x] = false;
                    }
                }
            } else if (m <= -1) {
                for (int y = sy + 1; y <= N - 1; y++) {
                    int low = Math.max(0, sx - y + sy);
                    for (int x = low; x <= sx; x++) {
                        soldierMap[y][x] = false;
                    }
                }
            }
        }
    }

    public static void showMap(boolean[][] map) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                sb.append((map[y][x] ? 1 : 0 )+ " ");
            }
            sb.append("\n");
        }
        System.out.printf(sb.toString());
    }

    public static void showSoldiers() {
        StringBuilder sb = new StringBuilder();
        int number = 1;
        for (Solider soldier : soldiers) {
            sb.append("soldier number " + number++);
            sb.append(String.format(":(%d,%d)\n", soldier.x, soldier.y));
        }
        System.out.printf(sb.toString());
    }

    public static void showMedusa() {
        StringBuilder sb = new StringBuilder();
        System.out.printf("medusa : %d, %d\n", mx, my);
    }

    public static int calculateStonedSoldiers() {
//        System.out.printf("before calculate stoned soldiers, map : \n");
//        showMap(soldierMap);
        int number = 0;
        for (Solider soldier : soldiers) {
            if (soldier.isStoned()) {
                number++;
            }
        }
//        System.out.printf("stoned soldier : %d\n", number);

        return number;
    }

    public static int calculateOneSide() {
        fillZeroOnSoldierMap();
        fillStoneOfMedusaSight();
        for (Solider soldier : soldiers) {
            fillZeroOfSoliderSight(soldier);
        }
        return calculateStonedSoldiers();
    }


    public static Pair selectOneSide() {
//        System.out.println("Calculating downside ");
//        showSoldiers();
//        showMedusa();
        Pair down = new Pair(calculateOneSide(), 3);

        rotateCharacters();
        rotateMap();
//        System.out.println("Calculating rightSide ");
//        showSoldiers();
//        showMedusa();
        Pair right = new Pair(calculateOneSide(), 1);

        rotateCharacters();
        rotateMap();
//        System.out.println("Calculating upSide ");
//        showSoldiers();
//        showMedusa();
        Pair up = new Pair(calculateOneSide(), 4);

        rotateCharacters();
        rotateMap();
//        System.out.println("Calculating leftSide ");
//        showSoldiers();
//        showMedusa();
        Pair left = new Pair(calculateOneSide(), 2);

        Pair[] possiblities = new Pair[]{up, down, left, right};
        Arrays.sort(possiblities, (o1, o2) -> {
            if (o1.number == o2.number) {
                return o1.direction - o2.direction;
            } else {
                return o1.number - o2.number;
            }
        });

        return possiblities[3];
    }

    public static void setMapAccordingly(int direction) {
//        System.out.printf("chosen direction : %d\n", direction);
        // assume that map is being rotated 3 times on counterClockWise
        // current map is filled as "left"
        // up = 4, down = 3, left= 2, right = 1
        // so, return back to original

        rotateCharacters();

        // now, every Character is on original(down, default)
        // up

        if (direction == 4) {

            rotateCharacters();
            rotateCharacters();

            calculateOneSide();

            // rotate the map accordingly.
            rotateMap();
            rotateMap();

            rotateCharacters();
            rotateCharacters();
            // down, needs 1 time
        } else if (direction == 3) {
            calculateOneSide();
            // left, needs 2 time
        } else if (direction == 2) {
            rotateCharacters();
            rotateCharacters();
            rotateCharacters();

            calculateOneSide();

            rotateMap();

            rotateCharacters();

            // right, needs 3 time;
        } else {
            rotateCharacters();

            calculateOneSide();

            rotateMap();
            rotateMap();
            rotateMap();

            rotateCharacters();
            rotateCharacters();
            rotateCharacters();

        }
    }

    public static void showCharacterMap() {
        StringBuilder sb = new StringBuilder("Character map is \n");

        for (int y = 0; y < N; y++) {
            for (int x = 0; x< N; x++) {
                int number = 1;
                boolean found = false;
                for (Solider soldier : soldiers) {
                    if (x == soldier.x && y == soldier.y) {
                        sb.append("S" + number + " ");
                        found = true;
                    }
                    number++;
                }
                if (x == mx && y == my) {
                    sb.append("M ");
                    found = true;
                }
                if (!found) sb.append("0 ");
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }

    public static void proceed(int[][] route) {
        for (int[] currentCoordinate : route) {
            mx = currentCoordinate[0];
            my = currentCoordinate[1];
//            System.out.printf("current, medusa : %d, %d\n", mx, my);

            ArrayList<Solider> removeList = new ArrayList<>();
            for (Solider soldier : soldiers) {
                if (soldier.faceMedusa()) {
                    removeList.add(soldier);
                }
            }
            soldiers.removeAll(removeList);

            if (mx == parkX && my == parkY) {
                System.out.println("0");
                break;
            }
            Pair currentSide = selectOneSide();
            setMapAccordingly(currentSide.direction);
//            showMedusa();
//            showSoldiers();
//            showMap(soldierMap);
//            showCharacterMap();
            int distance = 0;
            int stonedSoldiers = 0;
            removeList = new ArrayList<>();
            int number = 1;
            for (Solider solider : soldiers) {
//                System.out.printf("soldier number : %d\n", number);
                if (solider.isStoned()) {
                    stonedSoldiers++;
                } else {
                    distance += solider.moveFirst();
                    distance += solider.moveSecond();
                }
                number++;
            }
            for (Solider soldier : soldiers) {
                if (soldier.faceMedusa()) {
                    removeList.add(soldier);
                }
            }
            soldiers.removeAll(removeList);
            System.out.printf("%d %d %d\n", distance, stonedSoldiers, removeList.size());
        }
    }


    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        st = new StringTokenizer(br.readLine());
        startY = Integer.parseInt(st.nextToken());
        startX = Integer.parseInt(st.nextToken());
        mx = startX;
        my = startY;
        parkY = Integer.parseInt(st.nextToken());
        parkX = Integer.parseInt(st.nextToken());
        medusa = new Medusa(mx, my);
        initSoliders(st, br);
        initMap(st, br);
        int[][] route = bfs();
        if (route == null) {
            System.out.println("-1");
            return;
        }
        proceed(route);
    }
}
