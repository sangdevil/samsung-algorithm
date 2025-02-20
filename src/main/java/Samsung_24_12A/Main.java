package Samsung_24_12A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {


    public static class Golem {
        short x;
        short y;
        short direction;
        Golem(short x, short y, short direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
    }

    public static class Node {
        short x;
        short y;
        Node(short x, short y) {
            this.x = x;
            this.y = y;
        }
    }

    public static short[][] directions = new short[][]{{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
    public static short[][] map;
    public static Golem[] golems;
    public static boolean[][] visited;
    public static int R,C,K;

    public static boolean checkLeft(Golem golem) {
        int x = golem.x;
        int y = golem.y;
        if (x == 1 || y == R - 2) {
            return false;
        }
        if (y == 0) {
            return map[0][x-2] == 0 && map[1][x-2] ==0 && map[1][x-1] == 0 && map[2][x-1] == 0;
        } else if (y == -1) {
            return map[0][x-2] == 0 && map[0][x-1] == 0 && map[1][x-1] == 0;
        } else if (y == -2) {
            return map[0][x-1] == 0;
        }
        return map[y-1][x-1] == 0 && map[y][x-2] == 0 && map[y+1][x-2] ==0 && map[y+1][x-1] == 0 && map[y+2][x-1] == 0;
    }

    public static boolean checkRight(Golem golem) {
        int x = golem.x;
        int y = golem.y;
        if (x == C - 2 || y == R - 2) {
            return false;
        }
        if (y == 0) {
            return map[0][x+2] == 0 && map[1][x+2] ==0 && map[1][x+1] == 0 && map[2][x+1] == 0;
        } else if (y == -1) {
            return map[0][x+1] == 0 && map[0][x+2] ==0 && map[1][x+1] == 0;
        } else if (y == -2) {
            return map[0][x+1] == 0;
        }
        return map[y][x+2] == 0 && map[y+1][x+2] ==0 && map[y+1][x+1] == 0 && map[y+2][x+1] == 0;
    }


    public static void moveOnce(Golem golem) {
        if (golem.x == 0 ||golem.y == R - 2) {
            return;
        }
//        System.out.printf("golem move start, from %d, %d\n", golem.x, golem.y);
        // first move, check +y direction
        if ((golem.y == -2 && map[0][golem.x] == 0) ||
                (golem.y > -2 && map[golem.y + 1][golem.x - 1] == 0 && map[golem.y + 2][golem.x] == 0 && map[golem.y + 1][golem.x + 1] == 0)) {
            golem.y += 1;
        } else {
            // second move, check -x, +y direction
            if (checkLeft(golem)) {
                golem.x -= 1;
                golem.y += 1;
                if (golem.direction == 0) {
                    golem.direction = 3;
                } else {
                    golem.direction -= 1;
                }
            } else if (checkRight(golem)) {
                golem.x += 1;
                golem.y += 1;
                if (golem.direction == 3) {
                    golem.direction = 0;
                } else {
                    golem.direction += 1;
                }
            }
        }
//        System.out.printf("golem move end, at %d, %d\n", golem.x, golem.y);
    }

    public static void showMap() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < R; y++) {
            for (int x = 0; x < C; x++) {
                sb.append(map[y][x]);
                if (map[y][x] < 10) {
                    sb.append(" ");
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }


    public static void oneGolemPlace(Golem golem, short number) {

        while (true) {
            int cx = golem.x;
            int cy = golem.y;
            moveOnce(golem);
            if (cx == golem.x && cy == golem.y) {
                if (cy >= 1) {
                    map[cy - 1][cx] = number;
                    map[cy][cx - 1] = number;
                    map[cy][cx] = number;
                    map[cy][cx + 1] = number;
                    map[cy + 1][cx] = number;
                    // set exit as even number
                    map[cy + directions[golem.direction][1]][cx + directions[golem.direction][0]] = (short) (number + 1);
                }
                break;
            }
        }
//        System.out.printf("after place golem %d, map is \n", number);
//        showMap();
    }

    public static void eraseMap() {
        for (int y = 0; y < R; y++) {
            for (int x = 0; x < C; x++) {
                map[y][x] = 0;
            }
        }
    }

    public static int calculateScore(Golem golem) {

        Node current = new Node(golem.x, golem.y);
        int maxR = -1;
        for (int y = 0; y < R; y++) {
            for (int x = 0; x < C; x++) {
                visited[y][x] = false;
            }
        }
        Queue<Node> queue = new ArrayDeque<>();
        queue.add(current);
        while (!queue.isEmpty()) {
            current = queue.poll();
            visited[current.y][current.x] = true;
            if (current.y > maxR) {
                maxR = current.y;
            }

            for (short[] dir : directions) {
                short nx = (short) (current.x + dir[0]);
                short ny = (short) (current.y + dir[1]);
                if (nx < 0 || nx >= C || ny < 0 || ny >= R || visited[ny][nx]) {
                    continue;
                }
                // if currentBlock is non-ExitBlock, it can go to -1 <= diff <= 0 block. ( on the same golem )
                // if currentBlock is exitBlock, it can go to anywhere except 0.
                int diff = map[current.y][current.x] - map[ny][nx];
                if ((-1 <= diff && diff <= 0) || (map[current.y][current.x] % 2 == 0 && map[ny][nx] > 0)) {
                    queue.add(new Node(nx, ny));
                }
            }
        }
        return maxR + 1;
    }

    public static int golemTetrise(Golem[] golems) {
        int totalScore = 0;
        short number = 3;
        for (Golem golem : golems) {
            oneGolemPlace(golem, number);
            if (golem.y <= 0) {
//                System.out.printf("golem %d can't start, erase map \n", number);
                eraseMap();
                continue;
            }
            int score = calculateScore(golem);
//            System.out.printf("golem %d get score : %d \n", number, score);
            totalScore += score;
            number += 2;
        }

        return totalScore;
    }

    public static void initGolems(StringTokenizer st, BufferedReader br) throws IOException {
        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        golems = new Golem[K];
        map = new short[R][C];
        visited = new boolean[R][C];
        for (int y = 0; y < R; y++) {
            for (int x = 0; x < C; x++) {
                map[y][x] = 0;
            }
        }
        short col,exitDir;
        for (short i = 0; i < K; i++) {
            st = new StringTokenizer(br.readLine());
            col = (short) (Integer.parseInt(st.nextToken()) - 1);
            exitDir = (short) Integer.parseInt(st.nextToken());
            golems[i] = new Golem(col, (short) -2, exitDir);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        initGolems(st, br);
        System.out.println(golemTetrise(golems));
    }

}
