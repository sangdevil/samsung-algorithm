package samsung_23_22A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Main {

    public static class Santa {
        int x, y, sleep, totalScore, index;
        boolean isOut;

        Santa(int x, int y, int index) {
            this.x = x;
            this.y = y;
            this.index = index;
            this.sleep = 0;
            this.totalScore = 0;
            this.isOut = false;
        }
    }

    public static class Rudolph {
        int x, y;

        Rudolph(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static final int[][] santaDirections = new int[][]{{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
    public static final int[][] rudolphDirections = new int[][]{{-1, -1}, {1, -1}, {1, 1}, {-1, 1}, {0, -1}, {1, 0}, {0, 1}, {-1, 0}};
    public static int[][] map;
    public static Rudolph rudolph;
    public static Santa[] santas;
    public static int N, M, P, santaPower, rudolphPower;

    public static int dist(int rx, int ry, int sx, int sy) {
        return (rx - sx) * (rx - sx) + (ry - sy) * (ry - sy);
    }

    public static void moveSanta(Santa santa) {

        if (santa.isOut) return;
        if (santa.sleep > 0) return;
        // First, move according to the rule. santa cannot move to the place where other santa is on.
        int[] possibilities = new int[]{dist(rudolph.x, rudolph.y, santa.x - 1, santa.y), dist(rudolph.x, rudolph.y, santa.x + 1, santa.y),
                dist(rudolph.x, rudolph.y, santa.x, santa.y - 1),dist(rudolph.x, rudolph.y, santa.x, santa.y + 1)};
        Arrays.sort(possibilities);
        int beforeD = dist(rudolph.x, rudolph.y, santa.x, santa.y);
        int minD = beforeD;
        int[] selectDir = null;
        for (int[] dir : santaDirections) {
            int nsx = santa.x + dir[0];
            int nsy = santa.y + dir[1];
            int curD = dist(rudolph.x, rudolph.y, nsx, nsy);
            if (curD < minD) {
                if (map[nsy][nsx] == 0 || map[nsy][nsx] == 100) {
                    //then can move.
                    selectDir = dir;
                    minD = curD;
                }
            }
        }



        if (selectDir == null) return;

        map[santa.y][santa.x] = 0;
        santa.x += selectDir[0];
        santa.y += selectDir[1];

        // Second, consider the case where santa crash rudolph.
        if (santa.x == rudolph.x && santa.y == rudolph.y) {
//            System.out.println("santa crashed");
            santa.x -= (santaPower) * selectDir[0];
            santa.y -= (santaPower) * selectDir[1];
            santa.totalScore += santaPower;
            santa.sleep = 2;

            // Third, check if santa is out of map.
            if (santa.x < 0 || santa.x >= N || santa.y < 0 || santa.y >= N) {
                santa.isOut = true;
            }

            // Lastly, interact with other santas.
            // I haven't set santa on Map yet.
            // interactSanta will fill the map of new (nsx, nsy).
            if (!santa.isOut) {
                interactSanta(santa, selectDir, false);
            }
        } else {
            map[santa.y][santa.x] = santa.index;
        }




    }

    public static void interactSanta(Santa santa, int[] moveDir, boolean forward) {
        int nsx = santa.x;
        int nsy = santa.y;
        if (map[nsy][nsx] == 0) {
            map[nsy][nsx] = santa.index;
            return;
        }

//        System.out.printf("interacting started\n");
        int beforeSantaIndex = santa.index;
        Santa curSanta = santa;
        while (nsx >= 0 && nsx < N && nsy >= 0 && nsy < N && map[nsy][nsx] != 0) {
            curSanta = santas[map[nsy][nsx] - 1];
            map[nsy][nsx] = beforeSantaIndex;
//            System.out.printf("nsx, nsy : (%d,%d), nextSanta : %d\n", nsx, nsy, curSanta.index);
            nsx += forward ? moveDir[0] : -moveDir[0];
            nsy += forward ? moveDir[1] : -moveDir[1];
            curSanta.x = nsx;
            curSanta.y = nsy;
            beforeSantaIndex = curSanta.index;
            if (curSanta.x < 0 || curSanta.x >= N || curSanta.y < 0 || curSanta.y >= N) {
                curSanta.isOut = true;
                break;
            }
        }
        if (!curSanta.isOut) map[curSanta.y][curSanta.x] = curSanta.index;
    }

    public static void moveRudolph() {


        // First, select Santa.
        Santa selected = null;
        int minD = 10000;
        for (Santa santa : santas) {
            if (santa.isOut) continue;
            int curD = dist(rudolph.x, rudolph.y, santa.x, santa.y);
            if (curD < minD) {
                selected = santa;
                minD = curD;
            } else if (curD == minD) {
                assert selected != null;
                if (selected.y < santa.y) {
                    selected = santa;
                } else if (selected.y == santa.y) {
                    if (selected.x < santa.x) {
                        selected = santa;
                    }
                }
            }
        }
//        System.out.printf("Santa selected : %d, with d : %d\n", selected.index, minD);

        // Second, select the direction.
        for (int[] dir : rudolphDirections) {
            int nrx = rudolph.x + dir[0];
            int nry = rudolph.y + dir[1];
            int curD = dist(nrx, nry, selected.x, selected.y);
            if (curD < minD) {
                minD = curD;
            }
        }
        int[] selectedDir = null;
        for (int[] dir : rudolphDirections) {
            int nrx = rudolph.x + dir[0];
            int nry = rudolph.y + dir[1];
            if (dist(nrx, nry, selected.x, selected.y) == minD) {
//                System.out.printf("rudolph moved to (%d, %d) direction\n", dir[0], dir[1]);
                rudolph.x = nrx;
                rudolph.y = nry;
                selectedDir = dir;
                map[nry][nrx] = 100;
                map[nry - dir[1]][nrx - dir[0]] = 0;
                break;
            }
        }

        if (rudolph.x == selected.x && rudolph.y == selected.y) {
            // Thrid, crash the santa.
            assert selectedDir != null;
            selected.x += rudolphPower * selectedDir[0];
            selected.y += rudolphPower * selectedDir[1];
            selected.totalScore += rudolphPower;
            selected.sleep = 2;

            // fourth, check if santa is out
            if (selected.x < 0 || selected.x >= N || selected.y < 0 || selected.y >= N) {
                selected.isOut = true;
            } else {
                // else, interact.
                interactSanta(selected, selectedDir, true);
            }
        }


    }

    public static boolean allOut() {
        for (Santa santa : santas) {
            if (!santa.isOut) return false;
        }
        return true;
    }


    public static void proceed() {

        for (int i = 1; i <= M; i++) {
            if (allOut()) break;
//            System.out.printf("Current turn : %d, initally, map is\n", i);
//            showMap();
            moveRudolph();
//            System.out.println("after move rudolph, map is :");
//            showMap();
            for (Santa santa : santas) {
                if (santa.isOut) continue;
//                System.out.printf("Santa %d moved, map is :\n", santa.index);
                moveSanta(santa);
//                showMap();

            }
            for (Santa santa : santas) {
                if (!santa.isOut) {
                    santa.totalScore += 1;
                    if (santa.sleep > 0) {
                        santa.sleep -= 1;
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Santa santa : santas) {
            sb.append(santa.totalScore);
            sb.append(" ");
        }
        System.out.println(sb);
    }

    public static void showMap() {
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                if (map[y][x] == 0) {
                    sb.append("0   ");
                } else if (map[y][x] == 100) {
                    sb.append("R   ");
                } else if (map[y][x] < 10) {
                    sb.append("S");
                    sb.append(map[y][x]);
                    sb.append("  ");
                } else {
                    sb.append("S");
                    sb.append(map[y][x]);
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        rudolphPower = Integer.parseInt(st.nextToken());
        santaPower = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(br.readLine());
        int ry = Integer.parseInt(st.nextToken()) - 1;
        int rx = Integer.parseInt(st.nextToken()) - 1;
        rudolph = new Rudolph(rx, ry);

        santas = new Santa[P];
        map = new int[N][N];
        for (int i = 1; i <= P; i++) {
            st = new StringTokenizer(br.readLine());
            int index = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken()) - 1;
            int x = Integer.parseInt(st.nextToken()) - 1;
//            System.out.printf("x, y, index : %d, %d, %d\n", x, y, index);
            santas[index - 1] = new Santa(x, y, index);
        }
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                map[y][x] = 0;
            }
        }
        for (Santa santa : santas) {
            map[santa.y][santa.x] = santa.index;
        }
        map[rudolph.y][rudolph.x] = 100;

        proceed();
    }


}
