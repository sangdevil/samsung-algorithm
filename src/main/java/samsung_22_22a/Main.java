package samsung_22_22a;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

    public static long maxMemoryUsed = 0;

    public static void trackMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        maxMemoryUsed = Math.max(maxMemoryUsed, usedMemory);
    }

    public static class People {
        int x, y, xs, ys, turn;
        boolean hasReached;

        People(int x, int y, int xs, int ys, int turn) {
            this.x = x;
            this.y = y;
            this.xs = xs;
            this.ys = ys;
            this.turn = turn;
            this.hasReached = false;
        }
    }

    public static class Camp {
        int x, y;
        boolean hasReached;

        Camp(int x, int y) {
            this.x = x;
            this.y = y;
            this.hasReached = false;
        }
    }

    public static class Pair {
        int x, y, d;
        boolean visited;

        Pair(int x, int y, int d) {
            this.x = x;
            this.y = y;
            this.d = d;
            this.visited = false;
        }
    }

    public static int N, M, Time;
    public static Camp[] camps;
    /**
     * Assume that peoples are sorted in the standard of turn.
     */
    public static People[] peoples;
    public static int[][] directions = new int[][]{{0, -1}, {-1, 0}, {1, 0}, {0, 1}};
    public static int[][] map;
    public static int[][] distanceMap;
    public static Queue<Pair> queue = new ArrayDeque<>();

    public static void calculateMinimumDistanceFromStore(int xs, int ys) {
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                distanceMap[y][x] = Integer.MAX_VALUE;
            }
        }

        queue.clear();
        queue.add(new Pair(xs, ys, 0));
        distanceMap[ys][xs] = 0;

        while (!queue.isEmpty()) {
            Pair current = queue.poll();
            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];
                if (nx < 0 || nx >= N || ny < 0 || ny >= N) continue;
                if (map[ny][nx] == 0 && distanceMap[ny][nx] > current.d + 1) {
                    distanceMap[ny][nx] = current.d + 1;
                    queue.add(new Pair(nx, ny, current.d + 1));
                }
            }
        }

//        StringBuilder sb = new StringBuilder();
//        for (int y = 0; y < N; y++) {
//            for (int x = 0; x < N; x++) {
//                if (distanceMap[y][x] == Integer.MAX_VALUE) {
//                    sb.append("M");
//                } else {
//                    sb.append(distanceMap[y][x]);
//                }
//                if (distanceMap[y][x] < 10 || distanceMap[y][x] == Integer.MAX_VALUE) {
//                    sb.append(" ");
//                }
//                sb.append(" ");
//            }
//            sb.append("\n");
//        }
//        System.out.println(sb);
    }

    public static Camp selectCamp(People people) {
        Camp selected = null;
        int minD = N * N;
        int currentD;
//        int theoriticalMinD;
        calculateMinimumDistanceFromStore(people.xs, people.ys);
        for (Camp camp : camps) {
            if (camp.hasReached) continue;

            currentD = distanceMap[camp.y][camp.x];
//            System.out.printf("target Store : (%d,%d), distance : %d\n", people.xs, people.ys, currentD);
            if (currentD < minD) {
                selected = camp;
                minD = currentD;
            } else if (currentD == minD) {
                if (camp.y < selected.y) {
                    selected = camp;
                } else if (camp.y == selected.y) {
                    if (camp.x < selected.x) {
                        selected = camp;
                    }
                }
            }
//            System.out.printf("current Camp : (%d,%d)\n", camp.x, camp.y);
//            System.out.printf("select Camp : (%d,%d)\n", selected.x, selected.y);
        }
        return selected;
    }

    public static void movePeopleToCamp(People people, Camp camp) {
        people.x = camp.x;
        people.y = camp.y;
        camp.hasReached = true;
        map[people.y][people.x] = 1;

    }

    public static void movePeoples() {
        int nx, ny, minD, currentD;
        for (People people : peoples) {
            if (people.hasReached || people.turn >= Time) continue;
            minD = 2 * N;

            calculateMinimumDistanceFromStore(people.xs, people.ys);
            int finalX = people.x;
            int finalY = people.y;
//            System.out.printf("people %d has moved.\n", people.hashCode());
            for (int[] dir : directions) {
                nx = people.x + dir[0];
                ny = people.y + dir[1];
//                System.out.printf("nx, ny : (%d,%d)\n", nx, ny);
                if (nx < 0 || nx >= N || ny < 0 || ny >= N) continue;
//                System.out.println("not reached");
                if (distanceMap[ny][nx] < minD) {
                    finalX = nx;
                    finalY = ny;
                    minD = distanceMap[ny][nx];
                }
            }
            people.x = finalX;
            people.y = finalY;
        }
    }

    public static void lockStoreAndPeople() {
        for (People people : peoples) {
            if (people.hasReached || people.turn >= Time) continue;
            if (people.x == people.xs && people.y == people.ys) {
                map[people.y][people.x] = 1;
//                System.out.printf("here?\n");
                people.hasReached = true;
            }
        }
    }

    public static boolean checkFinish() {
        for (People people : peoples) {
            if (!people.hasReached) return false;
        }
        return true;
    }

    public static void showMap() {
        StringBuilder sb = new StringBuilder();
        int currentPeopleNum;
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                currentPeopleNum = 0;
                for (People people : peoples) {
                    if (people.hasReached || people.turn > Time) continue;
                    if (people.x == x && people.y == y) {
                        currentPeopleNum++;
                    }
                }
                if (currentPeopleNum > 0) {
                    sb.append(currentPeopleNum);
                    if (map[y][x] == 1) {
                        sb.append("X");
                    } else {
                        sb.append("O");
                    }
                    sb.append(" ");

                } else {
                    if (map[y][x] == 1) {
                        sb.append("X");
                    } else {
                        sb.append("O");
                    }
                    sb.append("  ");
                }
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

    public static void proceed() {
        Camp selectedCamp = camps[0];
        int ret = 1;

        while(Time <= 100) {
//            System.out.printf("Current time : %d\n", Time);

            movePeoples();
            lockStoreAndPeople();
            trackMemoryUsage();
            if (Time <= M) {
                selectedCamp = selectCamp(peoples[Time - 1]);
                movePeopleToCamp(peoples[Time - 1], selectedCamp);
//                System.out.printf("Camp Selected : (%d, %d)\n", selectedCamp.x, selectedCamp.y);
            }

//            System.out.println("Map is :");
//            showMap();
            if (checkFinish()) break;
            Time++;
        }
        System.out.println(Time);

        System.out.println(Time);
//        System.out.println("Total Time: " + Time);
        System.out.println("Max Memory Used: " + (maxMemoryUsed / 1024 / 1024) + " MB");
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        Time = 1;
        peoples = new People[M];
        map = new int[N][N];
        distanceMap = new int[N][N];
        int campNum = 0;
        for (int y = 0; y < N; y++) {
            st = new StringTokenizer(br.readLine());
            for (int x = 0; x < N; x++) {
                int i = Integer.parseInt(st.nextToken());
                map[y][x] = i;
                campNum += i;
            }
        }

        camps = new Camp[campNum];
        campNum = 0;
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                if (map[y][x] == 1) {
                    camps[campNum++] = new Camp(x, y);
                    map[y][x] = 0;
                }
            }
        }


        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken()) - 1;
            int x = Integer.parseInt(st.nextToken()) - 1;
            peoples[i] = new People(0, 0, x, y, i + 1);
        }
        Runtime runtime = Runtime.getRuntime();
        long startMemory = runtime.totalMemory() - runtime.freeMemory(); // 시작 시 메모리 사용량

        proceed();
//        System.gc();
        long endMemory = runtime.totalMemory() - runtime.freeMemory(); // 종료 시 메모리 사용량
        System.out.println("Final Memory Used: " + ((endMemory - startMemory) / 1024 / 1024) + " MB");

    }
}
