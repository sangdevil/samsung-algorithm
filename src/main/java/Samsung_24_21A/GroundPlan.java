package Samsung_24_21A;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringTokenizer;

public class GroundPlan {

    public static class Coordinate {
        int x;
        int y;
        char face;
        int time;

        public Coordinate(int x, int y, char face) {
            this.x = x;
            this.y = y;
            this.face = face;
            this.time = 0;
        }

    }

    public static class MyMap {
        int mapSize;
        char face;
        boolean[][] visited;
        int[][] map;

        public MyMap(int[][] map, int mapSize, char face) {
            this.mapSize = mapSize;
            this.face = face;
            this.visited = new boolean[mapSize][mapSize];
            this.map = map;
        }
    }

    public static class Fire {
        int x;
        int y;
        int[] direction;
        int turn;
        boolean stop;

        public Fire(int x, int y, int[] direction, int turn) {
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.turn = turn;
            this.stop = false;
        }
    }

    public static int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public static MyMap cubeMap;
    public static MyMap groundMap;
    public static Fire[] fires;
    // start (x,y) of cube on groundMap
    public static int startX, startY;

    // start (x, y) of face on cubeMap, on 'H' side.
    public static int initX, initY;

    // end (x,y) of exit on groundMap
    public static int endX, endY;
    public static int N, M, F;

    public static Coordinate nextCoordinate(Coordinate cur, int[] direction) {
        int nx = cur.x + direction[0];
        int ny = cur.y + direction[1];

//        System.out.printf("nx, ny = %d, %d\n", nx, ny);
        // if current coordinate is on domain1 or domain2, change nx, ny accordingly.
        if (cur.face == 'C') {
            if ((nx >= 0 && nx < M && ny >= 0 && ny < M) || (nx >= 2 * M && nx < 3 * M && ny >= 2 * M && ny < 3 * M)) {
//                System.out.println("on T1");
                nx = cur.y;
                ny = cur.x;
            } else if ((nx >= 2 * M && nx < 3 * M && ny >= 0 && ny < M) || (nx >= 0 && nx < M && ny >= 2 * M && ny < 3 * M)) {
//                System.out.println("on T2");
                nx = 3 * M - 1 - cur.y;
                ny = 3 * M - 1 - cur.x;
            }
//            System.out.println("not on T1, T2");
        }
        return changeFace(nx, ny, cur.face, cur.time + 1);
    }

    public static Coordinate changeFace(int nx, int ny, char nFace, int nTime) {
        Coordinate nextC = new Coordinate(nx, ny, nFace);
        nextC.time = nTime;
        if (nFace == 'C') {
            if (nextC.x == -1) {
                nextC.x = startX - 1;
                nextC.y = startY + ny - M;
                nextC.face = 'G';
                return nextC;
            } else if (nextC.x == 3 * M) {
                nextC.x = startX + M;
                nextC.y = startY + ny - M;
                nextC.face = 'G';
                return nextC;
            } else if (nextC.y == -1) {
                nextC.x = startX + nx - M;
                nextC.y = startY - 1;
                nextC.face = 'G';
                return nextC;
            } else if (nextC.y == 3 * M) {
                nextC.x = startX + nx - M;
                nextC.y = startY + M;
                nextC.face = 'G';
                return nextC;
            }
        }
        return nextC;
    }

    public static boolean isPossible(Coordinate cur) {
        if (cur.face == 'C') {
            return !cubeMap.visited[cur.y][cur.x] && (cubeMap.map[cur.y][cur.x] == 0 || cubeMap.map[cur.y][cur.x] == 4);
        } else if (cur.face == 'G') {
            if (cur.x < 0 || cur.x >= N || cur.y < 0 || cur.y >= N) {
                return false;
            }
            if (groundMap.visited[cur.y][cur.x]) {
                return false;
            }
            return groundMap.map[cur.y][cur.x] == 0 || groundMap.map[cur.y][cur.x] == 4;
        }
        return true;
    }

    public static void updateFires(int curTime) {
        for (Fire fire : fires) {
            if (!fire.stop) {
                if (curTime % fire.turn == 0) {
                    int newFireX = fire.x + (curTime / fire.turn) * fire.direction[0];
                    int newFireY = fire.y + (curTime / fire.turn) * fire.direction[1];
                    if (newFireX >= 0 && newFireX < N && newFireY >= 0 && newFireY < N
                            && (groundMap.map[newFireY][newFireX] == 0 || groundMap.map[newFireY][newFireX] == 5)) {
                        groundMap.map[newFireY][newFireX] = 5;
                    } else {
                        fire.stop = true;
                    }
                }
            }
        }
//        System.out.printf("after time %d, groundMap : \n", curTime);
//        for (int y = 0; y < N; y++) {
//            StringBuilder sb = new StringBuilder("");
//            for (int x = 0; x < N; x++) {
//                sb.append(groundMap.map[y][x]);
//                sb.append(" ");
//            }
//            System.out.println(sb);
//        }
//        System.out.println();
    }

    public static int bfs() {


        Coordinate cur = new Coordinate(initX, initY, 'C');
        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(cur);
        boolean[] updateFired = new boolean[10000];
        updateFires(cur.time);
        updateFired[cur.time] = true;
        while (!queue.isEmpty()) {
            cur = queue.poll();
            if (cur.face == 'C') {
                cubeMap.visited[cur.y][cur.x] = true;
            } else if (cur.face == 'G') {
                groundMap.visited[cur.y][cur.x] = true;
            }
            if (!updateFired[cur.time + 1]) {
                updateFires(cur.time + 1);
                updateFired[cur.time + 1] = true;
            }
//            System.out.printf("cur, on %c : (x,y,time) = (%d, %d, %d)\n", cur.face, cur.x, cur.y, cur.time);
            if (cur.face == 'G' && cur.x == endX && cur.y == endY) {
                return cur.time;
            }
            for (int i = 0; i < 4; i++) {
                Coordinate nextC = nextCoordinate(cur, directions[i]);
//                System.out.printf("next, on %c : (x,y,time) = (%d, %d, %d)\n", nextC.face, nextC.x, nextC.y, nextC.time);
                if (isPossible(nextC)) {
//                    System.out.printf("is possible\n");
                    queue.add(nextC);
                }
            }
        }
        return -1;
    }

    public static void initCoordinate() {
        boolean found = false;
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                if (groundMap.map[y][x] == 3) {
                    startX = x;
                    startY = y;
                    found = true;
                    break;
                }
            }
            if (found) break;
        }
        found = false;
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                if (groundMap.map[y][x] == 4) {
                    endX = x;
                    endY = y;
                    found = true;
                    break;
                }
            }
            if (found) break;
        }
        for (int y = 0; y < M; y++) {
            for (int x = 0; x < M; x++) {
                if (cubeMap.map[M + y][M + x] == 2) {
                    initX = M + x;
                    initY = M + y;
                    break;
                }
                ;
            }
        }
    }

    public static void initMap(StringTokenizer st, BufferedReader br) throws IOException {

        int[][] cubeMapInside = new int[3 * M][3 * M];
        int[][] groundMapInside = new int[N][N];
        // fill groundMap
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                groundMapInside[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        // fill east side of cube
        for (int y = 0; y < M; y++) {
            st = new StringTokenizer(br.readLine());
            for (int x = 0; x < M; x++) {
                cubeMapInside[2 * M - 1 - x][2 * M + y] = Integer.parseInt(st.nextToken());
            }
        }
        // fill west side of cube
        for (int y = 0; y < M; y++) {
            st = new StringTokenizer(br.readLine());
            for (int x = 0; x < M; x++) {
                cubeMapInside[M + x][M - 1 - y] = Integer.parseInt(st.nextToken());
            }
        }
        // fill south side of cube
        for (int y = 0; y < M; y++) {
            st = new StringTokenizer(br.readLine());
            for (int x = 0; x < M; x++) {
                cubeMapInside[2 * M + y][M + x] = Integer.parseInt(st.nextToken());
            }
        }
        // fill north side of cube
        for (int y = 0; y < M; y++) {
            st = new StringTokenizer(br.readLine());
            for (int x = 0; x < M; x++) {
                cubeMapInside[M - 1 - y][2 * M - 1 - x] = Integer.parseInt(st.nextToken());
            }
        }
        // fill high side of cube
        for (int y = 0; y < M; y++) {
            st = new StringTokenizer(br.readLine());
            for (int x = 0; x < M; x++) {
                cubeMapInside[M + y][M + x] = Integer.parseInt(st.nextToken());
            }
        }
        cubeMap = new MyMap(cubeMapInside, 3 * M, 'C');
        groundMap = new MyMap(groundMapInside, N, 'G');
//
//        System.out.println();
//        for (int y = 0; y < N; y++) {
//            StringBuilder sb = new StringBuilder("");
//            for (int x = 0; x < N; x++) {
//                sb.append(groundMap.map[y][x]);
//                sb.append(" ");
//            }
//            System.out.println(sb);
//        }
//        System.out.println();
//        for (int y = 0; y < 3 * M; y++) {
//            StringBuilder sb = new StringBuilder("");
//            for (int x = 0; x < 3 * M; x++) {
//                sb.append(cubeMap.map[y][x]);
//                sb.append(" ");
//            }
//            System.out.println(sb);
//        }
    }

    public static void initFires(StringTokenizer st, BufferedReader br) throws IOException {
        fires = new Fire[F];
        for (int i = 0; i < F; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            int[] direction = directions[Integer.parseInt(st.nextToken())];
            int turn = Integer.parseInt(st.nextToken());
            fires[i] = new Fire(x, y, direction, turn);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        F = Integer.parseInt(st.nextToken());

        initMap(st, br);
        initFires(st, br);
        initCoordinate();

        System.out.println(bfs());

    }
}
