package samsung_22_12A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {

    public static class People {
        int x, y, nx, ny, index, teamIndex;

        People(int x, int y, int index, int teamIndex) {
            this.x = x;
            this.y = y;
            this.index = index;
            this.teamIndex = teamIndex;
        }
    }

    public static class Team {
        ArrayList<People> peoples;
        int point;

        Team(ArrayList<People> peoples) {
            this.peoples = peoples;
        }
    }

    public static int[][] dirs = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    public static int[][] map;
    public static boolean[][] visited;
    public static Team[] teams;
    public static ArrayList<People> allPeoples;
    public static int n, m, k;

    public static void moveCircle(ArrayList<People> peoples) {
//        for (People p : peoples) {
//            int[][] nextLocation = new int[2][2];
//            int i = 0;
//            for (int[] dir : dirs) {
//                int nx = p.x + dir[0];
//                int ny = p.y + dir[1];
//                if (nx < 0 || nx >= n || ny < 0 || ny >= n) continue;
//                if (map[ny][nx] > 0) {
//                    nextLocation[i++] = new int[]{nx,ny};
//                }
//            }
//            int p1x = nextLocation[0][0];
//            int p1y = nextLocation[0][1];
//            int p2x = nextLocation[1][0];
//            int p2y = nextLocation[1][1];
//            int p1 = map[p1y][p1x];
//            int p2 = map[p2y][p2x];
//            if (p1 > 0 && p2 > 0) {
//                if (p.index == 1) {
//                    if (p1 < p2) {
//                        p.nx = p2x;
//                        p.ny = p2y;
//                    } else {
//                        p.nx = p1x;
//                        p.ny = p1y;
//                    }
//                } else {
//                    if (p1 < p2) {
//                        p.nx = p1x;
//                        p.ny = p1y;
//                    } else {
//                        p.nx = p2x;
//                        p.ny = p2y;
//                    }
//                }
//            } else if (p1 > 0) {
//                p.nx = p1x;
//                p.ny = p1y;
//            } else if (p2 > 0) {
//                p.nx = p2x;
//                p.ny = p2y;
//            }
//        }

        int[][] nextLocation = new int[2][2];
        int i = 0;
        People head = peoples.get(0);
        for (int[] dir : dirs) {
            int nx = head.x + dir[0];
            int ny = head.y + dir[1];
            if (nx < 0 || nx >= n || ny < 0 || ny >= n) continue;
            if (map[ny][nx] > 0) {
                nextLocation[i++] = new int[]{nx, ny};
            }
        }
        int p1x = nextLocation[0][0];
        int p1y = nextLocation[0][1];
        int p2x = nextLocation[1][0];
        int p2y = nextLocation[1][1];
        int p1 = map[p1y][p1x];
        int p2 = map[p2y][p2x];
        if (p1 > 0 && p2 > 0) {
            if (p1 < p2) {
                head.nx = p2x;
                head.ny = p2y;
            } else {
                head.nx = p1x;
                head.ny = p1y;
            }
        } else if (p1 > 0) {
            head.nx = p1x;
            head.ny = p1y;
        } else if (p2 > 0) {
            head.nx = p2x;
            head.ny = p2y;
        }
        int headX = head.x;
        int headY = head.y;
        for (i = 1; i < peoples.size(); i++) {
            People p = peoples.get(i);
            p.nx = headX;
            p.ny = headY;
            headX = p.x;
            headY = p.y;
        }

        for (People p : peoples) {
//            System.out.printf("cur : (%d, %d) -> %d, next : (%d, %d) -> %d\n", p.x, p.y, map[p.y][p.x],
//                    p.nx, p.ny, map[p.ny][p.nx]);
            if (map[p.y][p.x] == 3) {
                map[p.y][p.x] = 4;
            }
            p.x = p.nx;
            p.y = p.ny;
            map[p.y][p.x] = p.index == 1 ? 1 : p.index == peoples.size() ? 3 : 2;
        }
    }

    public static Team makeTeam(int x, int y, int teamIndex) {
        Queue<People> queue = new ArrayDeque<>();
        ArrayList<People> peoples = new ArrayList<>();
        People start = new People(x, y, 1, teamIndex);
        queue.add(start);
        peoples.add(start);
        visited[y][x] = true;
        while (!queue.isEmpty()) {
            People cur = queue.poll();
            for (int[] dir : dirs) {
                int nx = cur.x + dir[0];
                int ny = cur.y + dir[1];
                if (nx < 0 || nx >= n || ny < 0 || ny >= n || visited[ny][nx]) {
                    continue;
                }
                int diff = map[ny][nx] - map[cur.y][cur.x];
                if (map[ny][nx] < 4 && (diff == 1 || diff == 0)) {
                    People newOne = new People(nx, ny, cur.index + 1, teamIndex);
                    queue.add(newOne);
                    peoples.add(newOne);
                    visited[ny][nx] = true;
                }
            }
        }

        return new Team(peoples);
    }

    public static People selectPeople(ArrayList<People> peoples, int round) {
        round = round % (4 * n);
        int game = (round / n) % 4;
        int startCoordinate = round % n;
//        System.out.printf("round : %d, game : %d, start : %d\n", round, game, startCoordinate);
        People selected = null;
        if (game == 0) {
            for (People p : peoples) {
                if (p.y == startCoordinate) {
                    if (selected == null) {
                        selected = p;
                    } else if (p.x < selected.x) {
                        selected = p;
                    }
                }
            }
        } else if (game == 1) {
            for (People p : peoples) {
                if (p.x == startCoordinate) {
                    if (selected == null) {
                        selected = p;
                    } else if (p.y > selected.y) {
                        selected = p;
                    }
                }
            }
        } else if (game == 2) {
            for (People p : peoples) {
                if (p.y == n - 1 - startCoordinate) {
                    if (selected == null) {
                        selected = p;
                    } else if (p.x > selected.x) {
                        selected = p;
                    }
                }
            }
        } else if (game == 3) {
            for (People p : peoples) {
                if (p.x == n - 1 - startCoordinate) {
                    if (selected == null) {
                        selected = p;
                    } else if (p.y < selected.y) {
                        selected = p;
                    }
                }
            }
        }
        return selected;
    }

    public static void changeDirections(ArrayList<People> peoples) {
        Collections.reverse(peoples);
        for (int i = 0; i < peoples.size(); i++) {
            People cur = peoples.get(i);
            cur.index = i + 1;
            map[cur.y][cur.x] = cur.index == 1 ? 1 : cur.index == peoples.size() ? 3 : 2;
        }
    }

    public static boolean catchBall(ArrayList<People> peoples, int round) {

        People selected = selectPeople(peoples, round);
        if (selected != null) {
            Team team = teams[selected.teamIndex];
            team.point += selected.index * selected.index;
            changeDirections(team.peoples);
        }
        return selected != null;
    }

    public static void showMap(boolean mapShow, boolean teamShow) {
        StringBuilder sb = new StringBuilder("Currently, \n");
        if (mapShow) {
            for (int y = 0; y < n; y++) {
                for (int x = 0; x < n; x++) {
                    sb.append(map[y][x]);
                    sb.append(" ");
                }
                sb.append("\n");
            }
        }
        if (teamShow) {
            int i = 0;
            for (Team team : teams) {
                sb.append(String.format("Team %d has point %d, consists of peoples, \n", i++, team.point));
                for (People p : team.peoples) {
                    sb.append(String.format("[(%d, %d), index : %d], ", p.x, p.y, p.index));
                }
                sb.append("\n");
            }
        }

        System.out.println(sb);
    }

    public static void proceed() {
        for (int round = 0; round < k; round++) {
//            System.out.printf("Current round : %d\n", round);
//            showMap(true, false);
//            int game = ((round - 1) / n) % 4;
//            int startCoordinate = (round - 1) % n;
//            System.out.printf("cur round : %d, game : %d, start : %d\n", round % (4 * n), game, startCoordinate);
            int i = 0;
            for (Team team : teams) {
                moveCircle(team.peoples);
//                System.out.printf("Team %d moved, \n", i++);
//                showMap(true, false);
            }
//            showMap(false);
            catchBall(allPeoples, round);
//            showMap(true, true);
        }
        int sum = Arrays.stream(teams).mapToInt((t) -> t.point).sum();
        System.out.println(sum);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        map = new int[n][n];
        visited = new boolean[n][n];
        teams = new Team[m];
        for (int y = 0; y < n; y++) {
            st = new StringTokenizer(br.readLine());
            for (int x = 0; x < n; x++) {
                map[y][x] = Integer.parseInt(st.nextToken());
            }
        }
        int i = 0;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                if (map[y][x] == 1 && !visited[y][x]) {
                    teams[i] = makeTeam(x, y, i);
                    i++;
                }
            }
        }
        allPeoples = new ArrayList<>();
        for (Team team : teams) {
            allPeoples.addAll(team.peoples);
        }

        proceed();
    }

}
