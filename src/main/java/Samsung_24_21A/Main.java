package Samsung_24_21A;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Main {


    public enum Face {
        H, N, E, S, W, M
    }

    public static class Coordinate {
        int x;
        int y;
        int time;
        Face face;
        int cubeSize;
        int mapSize;

        public Coordinate(int x, int y, int time, Face face, int cubeSize, int mapSize) {
            this.x = x;
            this.y = y;
            this.time = time;
            this.face = face;
            this.cubeSize = cubeSize;
            this.mapSize = mapSize;
        }

    }

    public static class MyMap {
        int mapSize;
        int[][] map;
        boolean[][] visited;
        Face face;

        public MyMap(int mapSize, int[][] map, Face face) {
            this.mapSize = mapSize;
            this.map = map;
            this.face = face;
            this.visited = new boolean[mapSize][mapSize];
        }

    }

    public static class Fire {
        int x;
        int y;
        int turn;
        int[] direction;
        boolean stop;

        public Fire(int x, int y, int[] direction, int turn) {
            this.x = x;
            this.y = y;
            this.turn = turn;
            this.direction = direction;
            this.stop = false;
        }
    }


    public static int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    public static int cubeStartX;
    public static int cubeStartY;

    public static MyMap highMap;
    public static MyMap northMap;
    public static MyMap southMap;
    public static MyMap eastMap;
    public static MyMap westMap;
    public static MyMap groundMap;
    public static Fire[] fires;


    public static Coordinate changeFace(Coordinate curC) {

        int x = curC.x;
        int y = curC.y;
        int nextX = curC.x;
        int nextY = curC.y;
//        System.out.printf("before changeFace, x,y,face : %d, %d, %s\n", x, y, getFace(curC.face));
        Face nextFace = curC.face;
        int cubeSize = curC.cubeSize;
        switch (curC.face) {
            case E:
                // DOWN, goto Map.
                if (nextY == cubeSize) {
                    nextFace = Face.M;
                    nextX = cubeStartX + cubeSize;
                    nextY = cubeStartY + cubeSize - 1 - x;
                    //
                } else if (nextY == -1) {
                    // UP, goto H.
                    nextFace = Face.H;
                    nextX = cubeSize - 1 - x;
                    nextY = cubeSize - 1;
                } else if (nextX == cubeSize) {
                    // RIGHT
                    nextFace = Face.N;
                    nextX = 0;
                    nextY = y;
                } else if (nextX == -1) {
                    // LEFT
                    nextFace = Face.S;
                    nextX = cubeSize - 1;
                    nextY = y;
                }
                break;
            case S:
                // DOWN, goto Map.
                if (nextY == cubeSize) {
                    nextFace = Face.M;
                    nextX = cubeStartX + x;
                    nextY = cubeStartY + cubeSize;
                    //
                } else if (nextY == -1) {
                    // UP, goto H.
                    nextFace = Face.H;
                    nextX = x;
                    nextY = cubeSize - 1;
                } else if (nextX == cubeSize) {
                    // RIGHT
                    nextFace = Face.E;
                    nextX = 0;
                    nextY = y;
                } else if (nextX == -1) {
                    // LEFT
                    nextFace = Face.W;
                    nextX = cubeSize - 1;
                    nextY = y;
                }
                break;
            case W:
                // DOWN, goto Map.
                if (nextY == cubeSize) {
                    nextFace = Face.M;
                    nextX = cubeStartX - 1;
                    nextY = cubeStartY + x;
                    //
                } else if (nextY == -1) {
                    // UP, goto H.
                    nextFace = Face.H;
                    nextX = x;
                    nextY = 0;
                } else if (nextX == cubeSize) {
                    // RIGHT
                    nextFace = Face.S;
                    nextX = 0;
                    nextY = y;
                } else if (nextX == -1) {
                    // LEFT
                    nextFace = Face.N;
                    nextX = cubeSize - 1;
                    nextY = y;
                }
                break;
            case N:
                // DOWN, goto Map.
                if (nextY == cubeSize) {
                    nextFace = Face.M;
                    nextX = cubeStartX + cubeSize - 1 - x;
                    nextY = cubeStartY - 1;
                    //
                } else if (nextY == -1) {
                    // UP, goto H.
                    nextFace = Face.H;
                    nextX = cubeSize - 1 - x;
                    nextY = 0;
                } else if (nextX == cubeSize) {
                    // RIGHT
                    nextFace = Face.W;
                    nextX = 0;
                    nextY = y;
                } else if (nextX == -1) {
                    // LEFT
                    nextFace = Face.E;
                    nextX = cubeSize - 1;
                    nextY = y;
                }
                break;
            case H:
                // DOWN, goto S.
                if (nextY == cubeSize) {
                    nextFace = Face.S;
                    nextX = x;
                    nextY = 0;
                    //
                } else if (nextY == -1) {
                    // UP, goto N.
                    nextFace = Face.N;
                    nextX = cubeSize - 1 - x;
                    nextY = 0;
                } else if (nextX == cubeSize) {
                    // RIGHT
                    nextFace = Face.E;
                    nextX = cubeSize - 1 - y;
                    nextY = 0;
                } else if (nextX == -1) {
                    // LEFT
                    nextFace = Face.W;
                    nextX = y;
                    nextY = 0;
                }
                break;
        }
        curC.x = nextX;
        curC.y = nextY;
        curC.face = nextFace;

//        System.out.printf("after changeFace, x,y,face : %d, %d, %s\n", curC.x, curC.y, getFace(curC.face));

        return curC;
    }

    public static Coordinate  nextCoordinate(Coordinate curC, int[] direction) {

        Coordinate nextC = new Coordinate(curC.x, curC.y, curC.time, curC.face, curC.cubeSize, curC.mapSize);

        nextC.x += direction[0];
        nextC.y += direction[1];
        nextC = changeFace(nextC);
        nextC.time += 1;
        return nextC;

    }

    public static MyMap getMap(Face face) {
        switch (face) {
            case H:
                return highMap;
            case N:
                return northMap;
            case S:
                return southMap;
            case E:
                return eastMap;
            case W:
                return westMap;
            default:
                return groundMap;
        }

    }

    public static boolean checkPossible(Coordinate curC) {
        // First, check if currentFace is map and out of map.
        if (curC.face == Face.M) {
            int mapSize = curC.mapSize;
            if (curC.x < 0 || curC.x >= mapSize || curC.y < 0 || curC.y >= mapSize) {
                return false;
            }
        }

        // Second, check hasVisited
        switch (curC.face) {
            case H:
                if (highMap.visited[curC.y][curC.x])
                    return false;
                break;
            case N:
                if (northMap.visited[curC.y][curC.x])
                    return false;
                break;
            case E:
                if (eastMap.visited[curC.y][curC.x])
                    return false;
                break;
            case S:
                if (southMap.visited[curC.y][curC.x])
                    return false;
                break;
            case W:
                if (westMap.visited[curC.y][curC.x])
                    return false;
                break;
            case M:
                if (groundMap.visited[curC.y][curC.x])
                    return false;
                break;
        }

        // Next, check if current location is valid.
        MyMap currentMap = getMap(curC.face);
        int currentValue = currentMap.map[curC.y][curC.x];
        return currentValue == 0 || currentValue == 4;

    }

    public static String getFace(Face face) {
        switch (face) {
            case H:
                return "H";

            case E:
                return "E";

            case W:
                return "W";

            case S:
                return "S";

            case N:
                return "N";

            case M:
                return "M";

        }
        return "";
    }

    public static void updateFires(int curTime) {

//        System.out.printf("update fire for time : %d\n", curTime);
        for (Fire fire : fires) {
            if (fire.stop || curTime % fire.turn != 0) {
                continue;
            }
            int num = curTime / fire.turn;
            int nextFireX = fire.x + num * fire.direction[0];
            int nextFireY = fire.y + num * fire.direction[1];
//            System.out.printf("update fire for (%d, %d)\n", nextFireX, nextFireY);
            if (nextFireX < 0 || nextFireX >= groundMap.mapSize || nextFireY < 0 || nextFireY >= groundMap.mapSize
                || (groundMap.map[nextFireY][nextFireX] > 0 && groundMap.map[nextFireY][nextFireX] < 5) ) {
//                System.out.println("is not valid");
                fire.stop = true;
                continue;
            }
            groundMap.map[nextFireY][nextFireX] = 5;
//            System.out.printf("is valid and updated\n");
        }


    }

    public static void main(String[] args) {
        // Please write your code here.
        Scanner sc = new Scanner(System.in);
        int[] firstLine = Arrays.stream(sc.nextLine().split(" "))
                .mapToInt(Integer::parseInt)
                .toArray();
        int mapSize = firstLine[0];
        int cubeSize = firstLine[1];
        int fireNumber = firstLine[2];
        int[][] mapH = new int[cubeSize][cubeSize];
        int[][] mapN = new int[cubeSize][cubeSize];
        int[][] mapS = new int[cubeSize][cubeSize];
        int[][] mapE = new int[cubeSize][cubeSize];
        int[][] mapW = new int[cubeSize][cubeSize];
        int[][] mapG = new int[mapSize][mapSize];

        fires = new Fire[fireNumber];


        // fill groundMap
        for (int i = 0; i < mapSize; i++) {
            mapG[i] = Arrays.stream(sc.nextLine().split(" "))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }

        // fill eastMap;
        for (int i = 0; i < cubeSize; i++) {
            mapE[i] = Arrays.stream(sc.nextLine().split(" "))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }

        // fill westMap;
        for (int i = 0; i < cubeSize; i++) {
            mapW[i] = Arrays.stream(sc.nextLine().split(" "))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }

        // fill southMap;
        for (int i = 0; i < cubeSize; i++) {
            mapS[i] = Arrays.stream(sc.nextLine().split(" "))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }

        // fill northMap;
        for (int i = 0; i < cubeSize; i++) {
            mapN[i] = Arrays.stream(sc.nextLine().split(" "))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }
        // fill highMap;
        for (int i = 0; i < cubeSize; i++) {
            mapH[i] = Arrays.stream(sc.nextLine().split(" "))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }

        int curX = 0;
        int curY = 0;
        int retX = 0;
        int retY = 0;
        for (int y = 0; y < cubeSize; y++) {
            for (int x = 0; x < cubeSize; x++) {
                if (mapH[y][x] == 2) {
                    curX = x;
                    curY = y;
                    break;
                }
            }
        }
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                if (mapG[y][x] == 4) {
                    retX = x;
                    retY = y;
                }

            }
        }
        boolean hasFound = false;
        for (int y = 0; y < mapSize; y++) {
            if (hasFound) break;
            for (int x = 0; x < mapSize; x++) {
//                System.out.printf("x, y, v : %d, %d, %d\n", x, y, mapG[y][x]);
                if (mapG[y][x] == 3) {
                    cubeStartX = x;
                    cubeStartY = y;
                    hasFound = true;
                    break;
                }
            }
        }

//        System.out.printf("startX, startY : %d, %d\n", cubeStartX, cubeStartY);
//        System.out.println("mapG is");
//        for (int y = 0; y < mapSize; y++) {
//            for (int x = 0; x < mapSize; x++) {
//                System.out.printf(mapG[y][x] + " ");
//            }
//            System.out.println();
//        }
        Coordinate curC = new Coordinate(curX, curY, 0, Face.H, cubeSize, mapSize);

        // make Fires;
        for (int i = 0; i < fireNumber; i++) {
            int[] line = Arrays.stream(sc.nextLine().split(" "))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            fires[i] = new Fire(line[1], line[0], directions[line[2]], line[3]);
        }

        highMap = new MyMap(cubeSize, mapH, Face.H);
        northMap = new MyMap(cubeSize, mapN, Face.N);
        southMap = new MyMap(cubeSize, mapS, Face.S);
        eastMap = new MyMap(cubeSize, mapE, Face.E);
        westMap = new MyMap(cubeSize, mapW, Face.W);
        groundMap = new MyMap(mapSize, mapG, Face.M);


        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(curC);
        MyMap curMap;
        boolean hasReached = false;
        updateFires(0);
        while (!queue.isEmpty()) {
            curC = queue.poll();
            curMap = getMap(curC.face);
            curMap.visited[curC.y][curC.x] = true;
//            System.out.printf("current, (%d, %d), time : %d, face : %s\n",
//                    curC.x, curC.y, curC.time, getFace(curC.face));
            if (curC.face == Face.M && curC.x == retX && curC.y == retY) {
                System.out.println(curC.time);
                hasReached = true;
                break;
            }
            updateFires(curC.time + 1);
            for (int[] dir : directions) {
                Coordinate nextC = nextCoordinate(curC, dir);
//                System.out.printf("next? (%d, %d), time : %d, face : %s\n",
//                        nextC.x, nextC.y, nextC.time, getFace(nextC.face));
                if (checkPossible(nextC)) {
//                    System.out.printf("next added\n");
                    queue.add(nextC);
                }
            }
        }
        if (!hasReached) System.out.println("-1");

    }
}


