package Samsung_24_11A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

    public static class Exploit {
        int degree;
        int score;
        int centerX;
        int centerY;
        int[][] afterExploitMap;
        Exploit(int degree, int score, int centerX, int centerY, int[][] afterExploitMap) {
            this.degree = degree;
            this.score = score;
            this.centerX = centerX;
            this.centerY = centerY;
            this.afterExploitMap = afterExploitMap;
        }
    }

    public static class ExploitRecord {
        boolean[][] trace;
        int score;
        ExploitRecord(boolean[][] trace, int score) {
            this.trace = trace;
            this.score = score;
        }
    }

    public static class Node {
        int x;
        int y;
        int length;
        Node(int x, int y, int length) {
            this.x = x;
            this.y = y;
            this.length = length;
        }
    }

    public static int[][] originalMap;

    public static int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public static Queue<Integer> treasureQueue;

    public static int K,M;

    public static ExploitRecord getExploitRecordFromXY(int x, int y, boolean[][] visited, int targetNumber) {
        boolean[][] trace = new boolean[5][5];

        Node current = new Node(x, y, 1);
        Queue<Node> queue = new LinkedList<>();
        queue.add(current);
        while (!queue.isEmpty()) {
            current = queue.poll();
            visited[current.y][current.x] = true;
            trace[current.y][current.x] = true;
            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];
                if (nx < 0 || nx >= 5 || ny < 0 || ny >= 5) {
                    continue;
                }
                if (originalMap[ny][nx] == targetNumber && !visited[ny][nx]) {
                    queue.add(new Node(nx, ny, current.length + 1));
                }
            }
        }
        int score = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (trace[i][j]) {
                    score++;
                }
            }
        }

        return new ExploitRecord(trace, score);
    }

    public static Exploit getExploitRecordFromCurrentMap(int centerX, int centerY, int degree) {

        boolean[][] visited = new boolean[5][5];
        int[][] afterExploitMap = new int[5][5];
        int totalScore = 0;
//        System.out.printf("try for (%d, %d), degree : %d\n", centerX, centerY, degree);
//        System.out.println("current map is ");
//        showMap(originalMap);
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                afterExploitMap[y][x] = originalMap[y][x];
            }
        }

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                ExploitRecord record = getExploitRecordFromXY(x, y, visited, originalMap[y][x]);
                if (record.score > 2) {
                    // exploit successful, delete tresures and get score.
                    totalScore += record.score;
//                    System.out.printf("exploit successful, (%d, %d), score : %d\n",x, y,totalScore);
                    // for trace in treasure, delete it(set to 0)
                    for (int ry = 0; ry < 5; ry++) {
                        for (int rx = 0; rx < 5; rx++) {
                            if (record.trace[ry][rx]) {
                                afterExploitMap[ry][rx] = 0;
                            }
                        }
                    }
                }
            }
        }
        return new Exploit(degree, totalScore, centerX, centerY, afterExploitMap);


    }
    public static void rotateMap(int centerX, int centerY) {
//        System.out.printf("rotate for %d, %d\n", centerX, centerY);
        int startX = centerX - 1;
        int startY = centerY - 1;
        for (int y = 0; y <= 2; y++) {
            for (int x = y; x <= 2; x++) {
                int tmp = originalMap[startY + y][startX + x];
                originalMap[startY + y][startX + x] = originalMap[startY + x][startX + y];
                originalMap[startY + x][startX + y] = tmp;
            }
        }

//        System.out.println("after aT");
//        showMap(originalMap);

        for (int y = startY; y <= startY + 2; y++) {
            int tmp = originalMap[y][startX + 2];
            originalMap[y][startX + 2] = originalMap[y][startX];
            originalMap[y][startX] = tmp;
        }

    }


    public static Exploit selectOneFromRotations(int centerX, int centerY) {

        Exploit[] searches = new Exploit[4];

//        System.out.printf("search for (%d, %d). original map is \n", centerX, centerY);
//        showMap(originalMap);

        rotateMap(centerX, centerY);
        Exploit exploitFromDegree90 = getExploitRecordFromCurrentMap(centerX, centerY, 90);
        searches[0] = exploitFromDegree90;

        rotateMap(centerX, centerY);
        Exploit exploitFromDegree180 = getExploitRecordFromCurrentMap(centerX, centerY, 180);
        searches[1] = exploitFromDegree180;

        rotateMap(centerX, centerY);
        Exploit exploitFromDegree270 = getExploitRecordFromCurrentMap(centerX, centerY, 270);
        searches[2] = exploitFromDegree270;

        rotateMap(centerX, centerY);
        Exploit exploitFromDegree0 = getExploitRecordFromCurrentMap(centerX, centerY, 0);
        searches[3] = exploitFromDegree0;

        Arrays.sort(searches, (Exploit e1, Exploit e2) -> {
            if (e1.score == e2.score) {
                return Integer.compare(e2.degree, e1.degree);
            } else {
                return Integer.compare(e1.score, e2.score);
            }
        } );

        return searches[3];

    }

    public static Exploit selectOneFromCoordinates() {

        int number = 0;
        Exploit[] searches = new Exploit[9];
        for (int y = 1; y <= 3; y++) {
            for (int x = 1; x <= 3; x++) {
                Exploit exploit = selectOneFromRotations(x, y);
                searches[number++] = exploit;
            }
        }
        Arrays.sort(searches, (Exploit e1, Exploit e2) -> {
            if (e1.score == e2.score) {
                if (e1.degree == e2.degree) {
                    if (e1.centerX == e2.centerX) {
                        return Integer.compare(e2.centerY, e1.centerY);
                    } else {
                        return Integer.compare(e2.centerX, e1.centerX);
                    }
                } else {
                    return Integer.compare(e2.degree, e1.degree);

                }
            } else {
                return Integer.compare(e1.score, e2.score);
            }
        } );

        return searches[8];
    }

    public static void fillMap() {

        for (int x = 0; x < 5; x++) {
            for (int y = 4; y >= 0; y--) {
                if (originalMap[y][x] == 0) {
                    originalMap[y][x] = treasureQueue.poll();
                }
            }
        }
    }

    public static void initMapAndTreasure(StringTokenizer st, BufferedReader br) throws IOException {

        originalMap = new int[5][5];
        treasureQueue = new LinkedList<>();

        for (int y = 0; y < 5; y++) {
            st = new StringTokenizer(br.readLine());
            for (int x = 0; x < 5; x++) {
                originalMap[y][x] = Integer.parseInt(st.nextToken());
            }
        }
        st = new StringTokenizer(br.readLine());
        for (int m = 0; m < M; m++) {
            treasureQueue.add(Integer.parseInt(st.nextToken()));

        }

    }

    public static void eraseMap(int[][] newMap) {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                originalMap[y][x] = newMap[y][x];
            }
        }
    }
    public static void showMap(int[][] newMap) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                sb.append(newMap[y][x] + " ");
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

    public static void proceed() {

        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < K; k++) {
//            System.out.printf("%d th exploit\n", k + 1);
            Exploit currentExploit = selectOneFromCoordinates();
            if (currentExploit.score == 0) {
                break;
            }
            int totalScore = 0;
            // erase elements for treasures
            eraseMap(currentExploit.afterExploitMap);
            totalScore += currentExploit.score;

            // proceed the filling & exploit.
            fillMap();
//            System.out.printf("after first search, score is %d, and map is : \n", currentExploit.score);
//            showMap(originalMap);

            currentExploit = getExploitRecordFromCurrentMap(0, 0, 0);
            int number = 0;
            while (currentExploit.score > 0) {
//                System.out.printf("%dth try after %dth search\n", number + 1, k + 1);
                totalScore += currentExploit.score;
                eraseMap(currentExploit.afterExploitMap);
//                System.out.printf("after %dth try, score is %d, and map is : \n", number, currentExploit.score);
//                showMap(originalMap);
                fillMap();
                currentExploit = getExploitRecordFromCurrentMap(0, 0, 0);
                number++;
            }
            sb.append(totalScore);
            sb.append(" ");
        }
        sb.append("\n");
        System.out.println(sb.toString());
    }

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        initMapAndTreasure(st, br);

        proceed();

    }
}
