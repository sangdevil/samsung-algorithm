package samsung_23_12A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Main {
    public static class Runner {
        int x, y, squareLength, totalDistance, startX, startY;
        boolean hasExited;
        Runner(int x, int y) {
            this.x = x;
            this.y = y;
            this.squareLength = 0;
            this.hasExited = false;
            this.totalDistance = 0;
            this.startX = 0;
            this.startY = 0;
        }

        /**
         * not an M1 distance, this is an minimum length of an sqaure, which is formed by
         * including Runner and Exit.
         * Be careful that if exit and runner is on the same (x,y) it still has length 1.
         */
        public void calculateDistance() {
            this.squareLength = Math.max(Math.abs(this.x - exit.x), Math.abs(this.y - exit.y)) + 1;
        }
    }

    public static class Pair {
        int x, y;
        Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static Pair exit, start;
    public static Runner[] runners;
    public static int N,M,K;
    public static int[][] map;
    public static int[][] directions = new int[][]{{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

    public static Runner findTargetRunner() {
        for (Runner runner : runners) {
            if (!runner.hasExited) {
                runner.calculateDistance();
                findSqaureStartXY(runner);
            }
        }


        Arrays.sort(runners, (r1, r2) -> {
            if (r1.hasExited == r2.hasExited) {
                if (r1.squareLength == r2.squareLength) {
                    if (r1.startY == r2.startY) {
                        return r1.startX - r2.startX;
                    } else {
                        return r1.startY - r2.startY;
                    }
                } else {
                    return r1.squareLength - r2.squareLength;
                }
            } else {
                return Boolean.compare(r1.hasExited, r2.hasExited);
            }
        });

        return runners[0];
    }

    public static void findSqaureStartXY(Runner runner){
        int d = runner.squareLength - 1;
        int[] xCandidates = new int[]{0, runner.x - d, exit.x - d};
        int[] yCandidates = new int[]{0, runner.y - d, exit.y - d};
        Arrays.sort(xCandidates);
        Arrays.sort(yCandidates);
        int startX = 0;
        int startY = 0;
        for (int x : xCandidates) {
            if (x >= 0 && x >= runner.x - d && x >= exit.x - d && x <= runner.x && x <= exit.x && x <= N - d - 1) {
                startX = x;
                break;
            }
        }
        for (int y : yCandidates) {
//            System.out.printf("y : %d,", y);
            if (y >= 0 && y >= runner.y - d && y >= exit.y - d && y <= runner.y && y <= exit.y && y <= N - d - 1) {
                startY = y;
                break;
            }
        }
//        System.out.println();
        runner.startX = startX;
        runner.startY = startY;
    }

    public static void rotateMapBySqure(int d) {
        int tmp;
        // first, rotate clock wise.
        for (int y = 0; y < d; y++) {
            for (int x = y; x < d; x++) {
                tmp = map[start.y + y][start.x + x];
                map[start.y + y][start.x + x] = map[start.y + x][start.x + y];
                map[start.y + x][start.x + y] = tmp;
            }
        }
        for (int y = 0; y < d; y++) {
            for (int x = 0; x < d / 2; x++) {
                tmp = map[start.y + y][start.x + x];
                map[start.y + y][start.x + x] = map[start.y + y][start.x + d - 1 - x];
                map[start.y + y][start.x + d - 1 - x] = tmp;
            }
        }

        // second, rotate runners.
        for (Runner runner : runners) {
            if (runner.hasExited) continue;
            if (start.x <= runner.x && runner.x < start.x + d && start.y <= runner.y && runner.y < start.y + d) {
                tmp = runner.x - start.x;
                runner.x = start.x + (runner.y - start.y);
                runner.y = start.y + tmp;
                runner.x = start.x + d - 1 - (runner.x - start.x);
            }
        }

        // third, rotate Exit.

        tmp = exit.x - start.x;
        exit.x = start.x + (exit.y - start.y);
        exit.y = start.y + tmp;
        exit.x = start.x + d - 1 - (exit.x - start.x);

        // then do the damage.

        for (int y = start.y; y < start.y + d; y++) {
            for (int x = start.x; x < start.x + d; x++) {
                if (map[y][x] > 0) map[y][x]--;
            }
        }
    }

    public static int distanceFromExit(int x, int y) {
        return Math.abs(x - exit.x) + Math.abs(y - exit.y);
    }

    public static void moveRunners() {

        int nx,ny,d;

        for (Runner runner : runners) {
            if (runner.hasExited) continue;
            d = distanceFromExit(runner.x, runner.y);
            for (int[] dir : directions) {
                nx = runner.x + dir[0];
                ny = runner.y + dir[1];
                if (distanceFromExit(nx, ny) < d && map[ny][nx] == 0) {
                    runner.x = nx;
                    runner.y = ny;
                    runner.totalDistance += 1;
                    break;
                }
            }
        }
    }

    public static void exitRunners() {
        for (Runner runner : runners) {
            if (!runner.hasExited && runner.x == exit.x && runner.y == exit.y) {
                runner.hasExited = true;
            }
        }
    }

    public static void showMap() {
        StringBuilder sb = new StringBuilder();
        int runnerNum = 0;
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                for (Runner runner : runners) {
                    if (!runner.hasExited && x == runner.x && y == runner.y) {
                        runnerNum++;
                    }
                }
                if ((x == exit.x && y == exit.y)) {
                    sb.append("E");
                    sb.append(runnerNum);
                } else if (runnerNum > 0) {
                    sb.append("R");
                    sb.append(runnerNum);
                } else {
                    sb.append(map[y][x]);
                    sb.append(" ");
                }
                runnerNum = 0;
                sb.append(" ");
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

    public static void proceed() {
        Runner target;
        boolean finish;
        for (int k = 1; k <= K; k++) {
//            System.out.printf("current turn : %d\n", k);
            finish = true;
//            System.out.printf("before move, map is : \n");
//            showMap();
            moveRunners();
            exitRunners();
//            System.out.println("after move, map is : ");
//            showMap();
            for (Runner runner : runners) {
                if (!runner.hasExited) {
                    finish = false;
                    break;
                }
            }
            if (finish) {
//                System.out.println("finished.");
                break;
            }
            target = findTargetRunner();
//            System.out.printf("target runner : (%d,%d)\n", target.x, target.y);
            start.x = target.startX;
            start.y = target.startY;
//            System.out.printf("now, start is (%d,%d)\n", start.x, start.y);
            rotateMapBySqure(target.squareLength);
//            System.out.printf("After turn, map is now :\n");
//            showMap();
        }
        int totalDistance = 0;
        for (Runner runner : runners) {
            totalDistance += runner.totalDistance;
        }
        System.out.printf("%d\n", totalDistance);
        System.out.printf("%d %d\n", exit.y + 1, exit.x + 1);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        map = new int[N][N];
        for (int y = 0; y < N; y++) {
            st = new StringTokenizer(br.readLine());
            for (int x = 0; x < N; x++) {
                map[y][x] = Integer.parseInt(st.nextToken());
            }
        }
        runners = new Runner[M];
        int x,y;
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            y = Integer.parseInt(st.nextToken());
            x = Integer.parseInt(st.nextToken());
            runners[i] = new Runner(x - 1, y - 1);
        }
        st = new StringTokenizer(br.readLine());
        y = Integer.parseInt(st.nextToken());
        x = Integer.parseInt(st.nextToken());
        exit = new Pair(x - 1, y - 1);
        start = new Pair(0, 0);

        proceed();
    }
}
