package samsung_22_22a;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringTokenizer;

public class MemoryMax {
    public static long maxMemoryUsed = 0;
    public static void trackMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        maxMemoryUsed = Math.max(maxMemoryUsed, usedMemory);
    }
    public static class People {
        int x,y,xs,ys,turn;
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
        int x,y,d;
        boolean visited;

        Pair(int x, int y, int d) {
            this.x = x;
            this.y = y;
            this.d = d;
            this.visited = false;
        }
    }

    public static int N,M,Time;
    public static Camp[] camps;
    /**
     * Assume that peoples are sorted in the standard of turn.
     */
    public static People[] peoples;
    public static int[][] directions = new int[][]{{0,-1},{-1,0},{1,0},{0,1}};
    public static int[][] map;
    public static Pair[][] bfsMap;
    public static Queue<Pair> queue;
    public static void calculateMinimumDistanceFromStore(int xs, int ys) {
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                bfsMap[y][x].visited = false;
                bfsMap[y][x].d = 0;
            }
        }
//        System.out.printf("start bfs, map is : \n");
//        StringBuilder sb = new StringBuilder();
//        for (int y = 0; y < N; y++) {
//            for (int x = 0; x < N; x++) {
//                sb.append(map[y][x]);
//            }
//            sb.append("\n");
//        }
//        System.out.println(sb.toString());

        Pair current = bfsMap[ys][xs];
        queue = new ArrayDeque<>();
        queue.add(current);
        int nx,ny;
        while (!queue.isEmpty()) {
            current = queue.poll();
//            System.out.printf("current : (%d,%d) : %d\n", current.x, current.y, current.d);
            for (int[] dir : directions) {
                nx = current.x + dir[0];
                ny = current.y + dir[1];
                if (nx < 0 || nx >= N || ny < 0 || ny >= N) {
                    continue;
                }
                if (!bfsMap[ny][nx].visited && map[ny][nx] == 0) {
                    bfsMap[ny][nx].d = current.d + 1;
                    queue.add(bfsMap[ny][nx]);
                    bfsMap[ny][nx].visited = true;
                }
            }
        }

        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                if (!bfsMap[y][x].visited) {
                    bfsMap[y][x].d = Integer.MAX_VALUE;
                }
            }
        }
    }
    public static Camp selectCamp(People people) {
        Camp selected = camps[0];
        int minD = 2 * N;
        int currentD;
        int theoriticalMinD;
        calculateMinimumDistanceFromStore(people.xs, people.ys);
        for (Camp camp : camps) {
            if (camp.hasReached) continue;
            theoriticalMinD = Math.abs(people.xs - camp.x) + Math.abs(people.ys - camp.y);
            if (minD < theoriticalMinD) {
                continue;
            }
//            System.out.printf("current Camp : (%d,%d)\n", camp.x, camp.y);
//            System.out.printf("select Camp : (%d,%d)\n", selected.x, selected.y);
            currentD = bfsMap[camp.y][camp.x].d;
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
        int nx,ny,beforeD, currentD;

        for (People people : peoples) {
            if (people.hasReached || people.turn >= Time) continue;
//            System.out.printf("people %d has moved.\n", people.hashCode());
            beforeD = Math.abs(people.xs - people.x) + Math.abs(people.ys - people.y);
            for (int[] dir : directions) {
                nx = people.x + dir[0];
                ny = people.y + dir[1];
                currentD = Math.abs(people.xs - nx) + Math.abs(people.ys - ny);
                if (currentD < beforeD && map[ny][nx] == 0) {
                    people.x = nx;
                    people.y = ny;
                    break;
                }
            }
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

        People currentMovePeople;
        Camp selectedCamp;
        while (Time < 200) {
//            System.out.printf("Current time : %d\n", Time);
            movePeoples();
            lockStoreAndPeople();
            trackMemoryUsage(); // 메모리 사용량 측정

            if (Time <= M) {
                currentMovePeople = peoples[Time - 1];
                selectedCamp = selectCamp(currentMovePeople);
                movePeopleToCamp(currentMovePeople, selectedCamp);
//                System.out.printf("Camp Selected : (%d, %d)\n", selectedCamp.x, selectedCamp.y);
            }

//            System.out.println("Map is :");
//            showMap();
            if (checkFinish()) break;
            Time++;

        }

        System.out.println("Max Memory Used: " + (maxMemoryUsed / 1024 / 1024) + " MB");
        System.out.println(Time);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        Runtime runtime = Runtime.getRuntime();
        long startMemory = runtime.totalMemory() - runtime.freeMemory(); // 시작 시 메모리 사용량

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        Time = 1;
        peoples = new People[M];
        map = new int[N][N];
        bfsMap = new Pair[N][N];
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
                bfsMap[y][x] = new Pair(x, y, 0);
            }
        }


        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken()) - 1;
            int x = Integer.parseInt(st.nextToken()) - 1;
            peoples[i] = new People(0, 0, x, y, i+1);
        }

        proceed();
        long endMemory = runtime.totalMemory() - runtime.freeMemory(); // 종료 시 메모리 사용량
        System.out.println("Final Memory Used: " + ((endMemory - startMemory) / 1024 / 1024) + " MB");

    }
}

