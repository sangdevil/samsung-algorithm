package samsung_22_11A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    public static class Monster {
        int x, y, score, increasing;

        Monster(int x, int y, int score, int increasing) {
            this.x = x;
            this.y = y;
            this.score = score;
            this.increasing = increasing;
        }
    }

    public static class Runner {
        int x, y, index, dirNum;
        boolean isOut;

        Runner(int x, int y, int index, int dirNum) {
            this.x = x;
            this.y = y;
            this.index = index;
            this.dirNum = dirNum;
            this.isOut = false;
        }
    }

    public static int[][] dirs = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    public static int[][] moveMap;
    public static boolean[][] treeMap;
    public static Runner[] runners;
    public static Monster monster;
    public static int n, m, h, k, turn;

    public static void setMap() {
        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{0, 0});
        int time = 1;
        int dirNum = 0;
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            moveMap[cur[1]][cur[0]] = time++;
            if (time > n * n) {
                break;
            }
            int nx = cur[0] + dirs[dirNum][0];
            int ny = cur[1] + dirs[dirNum][1];
            if (nx < 0 || nx >= n || ny < 0 || ny >= n || moveMap[ny][nx] > 0) {
                dirNum = (dirNum + 1) % 4;
                nx = cur[0] + dirs[dirNum][0];
                ny = cur[1] + dirs[dirNum][1];
            }
            queue.add(new int[]{nx, ny});
//            System.out.printf("(nx, ny) : (%d,%d)\n", nx, ny);
        }

    }

    public static int[] selectNext() {
        int cx = monster.x;
        int cy = monster.y;
        int curV = moveMap[cy][cx];
        for (int[] dir : dirs) {
            int nx = cx + dir[0];
            int ny = cy + dir[1];
            if (nx < 0 || nx >= n || ny < 0 || ny  >= n) continue;
            if (moveMap[ny][nx] == curV + monster.increasing) {
                return new int[]{nx, ny};
            }
        }
        throw new RuntimeException("Cannot find next Coordinate");
    }

    public static void moveMonster() {
//        System.out.printf("Monster, currently : (%d, %d), dir : %d\n", monster.x, monster.y, monster.increasing);
        int[] nextXY = selectNext();
        monster.x = nextXY[0];
        monster.y = nextXY[1];
        if (monster.x == 0 && monster.y == 0) {
            monster.increasing = 1;
        } else if (monster.x == (n / 2) && monster.y == (n / 2)) {
            monster.increasing = -1;
        }
//        System.out.printf("moved to : (%d, %d), dir : %d\n", monster.x, monster.y, monster.increasing);
    }

    public static void moveRunner(Runner r) {
        if (r.isOut) return;
        int dist = Math.abs(r.x - monster.x) + Math.abs(r.y - monster.y);
        if (dist > 3) return;
//        System.out.printf("Runner %d, currently : (%d, %d)\n", r.index, r.x, r.y);
        int nx = r.x + dirs[r.dirNum][0];
        int ny = r.y + dirs[r.dirNum][1];
        if (nx < 0 || nx >= n || ny < 0 || ny >= n) {
            r.dirNum = (r.dirNum + 2) % 4;
            nx = r.x + dirs[r.dirNum][0];
            ny = r.y + dirs[r.dirNum][1];
        }
        if (nx == monster.x && ny == monster.y) {
            return;
        } else {
            r.x = nx;
            r.y = ny;
        }
//        System.out.printf("moved to (%d, %d)\n", nx, ny);
    }

    public static boolean checkDetected(Runner r, int x, int y) {
        if (r.x == x && r.y == y) {
            if (treeMap[y][x]) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static void detect() {
//        System.out.println("Detect started");
        int[] nextMonsterXY = selectNext();
        int[] sight = new int[]{nextMonsterXY[0] - monster.x, nextMonsterXY[1] - monster.y};
        int nx = monster.x;
        int ny = monster.y;
        int curSight = 0;
        int outRunners = 0;
        while (nx >= 0 && nx < n && ny >= 0 && ny < n && curSight < 3) {
//            System.out.printf("detect for (%d, %d)\n", nx, ny);
            for (Runner r : runners) {
                if (r.isOut) {
                    continue;
                }
                if (checkDetected(r, nx, ny)) {
//                    System.out.printf("runner : %d detected\n", r.index);
                    outRunners++;
                    r.isOut = true;
                }
            }
            nx += sight[0];
            ny += sight[1];
            curSight++;
        }
        monster.score += outRunners * turn;
    }

    public static void proceed() {
        for (turn = 1; turn <= k; turn++) {
//            System.out.printf("Turn %d, proceed\n", turn);
            for (Runner r : runners) {
                moveRunner(r);
            }
            moveMonster();
            detect();
//            showMap(false);
        }
    }

    public static void showMap(boolean showBasic) {

        StringBuilder sb = new StringBuilder();
        if (showBasic) {
            sb.append("Map of moveMap is : \n");
            for (int y = 0; y < n; y++) {
                for (int x = 0; x < n; x++) {
                    int val = moveMap[y][x];
                    if (val < 10) {
                        sb.append(val);
                        sb.append("  ");
                    } else {
                        sb.append(val);
                        sb.append(' ');
                    }
                }
                sb.append('\n');
            }
//            System.out.println(sb.toString());
        }
        sb.append("Map of Tree is : \n");
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                if (treeMap[y][x]) {
                    sb.append("T ");
                } else {
                    sb.append("O ");
                }
            }
            sb.append("\n");
        }

        sb.append("Map of Monster is : \n");
        char[][] monMap = new char[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(monMap[i], 'O');
        }
        monMap[monster.y][monster.x] = 'M';
        int[] monterNextXY = selectNext();
        int[] sight = new int[]{monterNextXY[0] - monster.x, monterNextXY[1] - monster.y};
        int curSight = 1;
        int nx = monterNextXY[0];
        int ny = monterNextXY[1];
        while (nx >= 0 && nx < n && ny >= 0 && ny < n && curSight < 3) {
            monMap[ny][nx] = 'X';
            nx += sight[0];
            ny += sight[1];
            curSight ++;
        }
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                sb.append(monMap[y][x]);
                sb.append(' ');
            }
            sb.append("\n");
        }
        sb.append("Runner Map is : \n");
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                boolean occupied = false;
                for (Runner r : runners) {
                    if (r.isOut) {
                        continue;
                    }
                    if (r.x == x && r.y == y) {
                        sb.append('R');
                        sb.append(r.index);
                        occupied = true;
                    }
                }
                if (!occupied) {
                    sb.append("O   ");
                } else {
                    sb.append("  ");
                }
            }
            sb.append("\n");
        }

        System.out.println(sb.toString());
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        h = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        runners = new Runner[m];
        treeMap = new boolean[n][n];
        moveMap = new int[n][n];
        monster = new Monster(n / 2, n / 2, 0, -1);
        for (int i = 0; i < m; i++) {
//            System.out.println(st.nextToken());
            st = new StringTokenizer(br.readLine());

            int y = Integer.parseInt(st.nextToken()) - 1;
            int x = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken());
            d = d == 2 ? 0 : 1;
            runners[i] = new Runner(x, y, i, d);
        }
        for (int i = 0; i < h; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken()) - 1;
            int x = Integer.parseInt(st.nextToken()) - 1;
            treeMap[y][x] = true;
        }

        setMap();

//        System.out.println("At first, showing map : ");
//        showMap(true);
        proceed();
        System.out.println(monster.score);

    }
}
