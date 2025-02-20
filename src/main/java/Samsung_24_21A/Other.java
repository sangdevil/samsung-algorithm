package Samsung_24_21A;

import java.util.*;
import java.io.*;

/**
 * 미지의 공간 (면) 한 변 길이 N, 이 위에 탈출구 존재
 * 시간의 벽 (정육면체) 한 변 길이 M, 이 위에 타임머신 존재,
 *
 * 빈 공간 = 0, 장애물 = 1
 *
 * 위치 2 -> 타임머신
 * 위치 3 -> 시간의 벽
 * 위치 4 -> 탈출구
 *
 * 시간의 벽 to 미지의 공간으로 이어진 빈 공간은 오직 하나.
 *
 * << 시간 이상 현상 F개 >>
 * -> 빈 공간 r,c에서 시작하고 매 v의 배수 턴 마다 방향 d로 한 칸씩 확산(기존 것들은 그대로 유지)
 * -> 더 이상 확산할 수 없는 경우 멈춤
 * -> d는 동(0), 서(1), 남(2), 북(3)
 * -> 하나의 턴에서 시간 이상 현상이 먼저 일어난 후 타임머신이 이동한다.
 *
 * << 타임머신>>
 * 매 턴마다 한 칸씩 이동
 * 장애물과 시간 이상 현상 피해 탈출구
 * 최소한으로 필요한 턴 수
 */
public class Other {
    static int N, M, F, exitDir;
    static int[][] unknownSpace, timeWall;
    static List<TimeAnomaly> timeAnomalies = new ArrayList<>();
    static Index timeMachine, unknownSpaceExitIndex, timeWallExitIndex, escapeIndex; // 미지의 공간, 시간의 벽  둘 사이의 탈출 지점
    static int[] dy = {0,0,1,-1}; // 동 서 남 북
    static int[] dx = {1,-1,0,0};
    static class Index {
        int y, x;
        public Index(int y, int x) {
            this.y = y;
            this.x = x;
        }
        @Override
        public String toString() {
            return "[" + y + ", " + x + "]";
        }
    }

    static class TimeAnomaly {
        int r, c, d, v;
        public TimeAnomaly(int r, int c, int d, int v) {
            this.r = r;
            this.c = c;
            this.d = d;
            this.v = v;
        }
        @Override
        public String toString() {
            return "[" + r + ", " + c + ", " + d + ", " + v + "]";
        }
    }

    static void initTimeAnomalies(BufferedReader br, StringTokenizer st) throws IOException {
        while (F-- > 0) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            int v = Integer.parseInt(st.nextToken());
            timeAnomalies.add(new TimeAnomaly(r, c, d, v));
            unknownSpace[r][c] = -1; // 초기 위치를 -1로 표시
        }
    }

    static void initUnknownSpace(BufferedReader br, StringTokenizer st) throws IOException {
        unknownSpace = new int[N][N];
        for (int i = 0; i < N; i++) { // 0은 빈공간, 1은 장애물, 3은 시간의 벽, 4는 탈출구
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                unknownSpace[i][j] = Integer.parseInt(st.nextToken());
                if (unknownSpace[i][j] == 4) escapeIndex = new Index(i, j);
            }
        }
    }

    static void initTimeWall(BufferedReader br, StringTokenizer st) throws IOException {
        // 시간의 벽 전개도 (십자가 형태로 펼침)
        int timeWallSize = 3*M;
        timeWall = new int[timeWallSize][timeWallSize];

        // 동
        for (int i = 2*M; i <= 3*M - 1; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 2*M - 1; j >= M; j--) timeWall[j][i] = Integer.parseInt(st.nextToken());
        }

        // 서
        for (int i = M - 1; i >= 0; i--) {
            st = new StringTokenizer(br.readLine());
            for (int j = M; j <= 2*M - 1; j++) timeWall[j][i] = Integer.parseInt(st.nextToken());
        }

        // 남
        for (int i = 2*M; i < 3*M; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = M; j < 2*M; j++) timeWall[i][j] = Integer.parseInt(st.nextToken());
        }

        // 북
        for (int i = M - 1; i >= 0; i--) {
            st = new StringTokenizer(br.readLine());
            for (int j = 2*M - 1; j >= M; j--) timeWall[i][j] = Integer.parseInt(st.nextToken());
        }

        // 위
        for (int i = M; i < 2*M; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = M; j < 2*M; j++) {
                timeWall[i][j] = Integer.parseInt(st.nextToken());
                if (timeWall[i][j] == 2) timeMachine = new Index(i, j);
            }
        }
    }

    static void findExitToUnknownSpace() {
        int sy = 0, sx = 0;

        boolean stopFlag = false;
        for (int i = 0; i < N; i++) {
            if (stopFlag) break;
            for (int j = 0; j < N; j++) {
                if (unknownSpace[i][j] == 3) {
                    sy = i - 1;
                    sx = j - 1;
                    stopFlag = true;
                    break;
                }
            }
        }

        int ey = sy + M + 1, ex = sx + M + 1;
        boolean found = false;

        int count = 0;
        exitDir = -1;
        for (int i = sx; i <= ex; i++) {
            if (found) break;
            if (0 <= sy && sy < N && 0 <= i && i < N) { // 상
                if (unknownSpace[sy][i] == 0) {
                    unknownSpaceExitIndex = new Index(sy, i);
                    exitDir = 3;
                    found = true;
                    break;
                }
            }
            if (0 <= ey && ey < N && 0 <= i && i < N) { // 하
                if (unknownSpace[ey][i] == 0) {
                    unknownSpaceExitIndex = new Index(ey, i);
                    exitDir = 2;
                    found = true;
                    break;
                }
            }
            count++;
        }

        if (!found) {
            count = 0;
            for (int i = sy; i <= ey; i++) {
                if (found) break;
                if (0 <= i && i < N && 0 <= sx && sx < N) { // 좌
                    if (unknownSpace[i][sx] == 0) {
                        unknownSpaceExitIndex = new Index(i, sx);
                        exitDir = 1;
                        found = true;
                        break;
                    }
                }
                if (0 <= i && i < N && 0 <= ex && ex < N) { // 우
                    if (unknownSpace[i][ex] == 0) {
                        unknownSpaceExitIndex = new Index(i, ey);
                        exitDir = 0;
                        found = true;
                        break;
                    }
                }
                count++;
            }
        }

        switch (exitDir) {
            case -1: {
                System.out.println(-1);
                System.exit(0);
            }
            case 0: {
                timeWallExitIndex = new Index(M + count - 1, 3 * M - 1); // 동
                break;
            }
            case 1: {
                timeWallExitIndex = new Index(M + count - 1, 0); // 서
                break;
            }
            case 2: {
                timeWallExitIndex = new Index(3 * M - 1, M + count - 1); // 남
                break;
            }
            case 3: {
                timeWallExitIndex = new Index(0, M + count - 1); // 북
                break;
            }
        }

    }

    static Index convert(int y, int x, int type) {
        // 좌측 상단 또는 우측 하단 - 타입 1
        if (type == 0) return new Index(x, y);

            // 좌측 하단 또는 우측 상단 - 타입 2
        else return new Index(3*M - x - 1, 3*M - y - 1);
    }

    static int timeWallBfs() {
        boolean[][] visited = new boolean[3*M][3*M];
        Queue<Index> queue = new LinkedList<>();
        queue.add(timeMachine);
        visited[timeMachine.y][timeMachine.x] = true;

        int time = -1;
        while (!queue.isEmpty()) {
            time++;

            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Index current = queue.poll();
                System.out.printf("current, %d, %d\n", current.x, current.y);

                for (int j = 0; j < 4; j++) {
                    int ny = current.y + dy[j];
                    int nx = current.x + dx[j];

                    System.out.printf("next, %d, %d\n", nx, ny);
                    if (ny == timeWallExitIndex.y && nx == timeWallExitIndex.x) return time + 1;

                    if (0 <= ny && ny < 3*M && 0 <= nx && nx < 3*M  && !visited[ny][nx] && timeWall[ny][nx] != 1) { // 범위 안에 들어올 때

                        if ((0 <= ny && ny < M  && 0 <= nx && nx < M) || (2*M <= ny && ny < 3*M && 2*M <= nx && nx < 3*M)) {
                            // 좌측 상단 또는 우측 하단
                            Index convert = convert(current.y, current.x, 0);
                            System.out.printf("convert to other wall, type 1, %d, %d\n", convert.x, convert.y);
                            if (!visited[convert.y][convert.x] && timeWall[convert.y][convert.x] != 1) {
                                visited[convert.y][convert.x] = true;
                                System.out.println("valid, added");
                                queue.add(convert);
                            }
                        } else if ((0 <= ny && ny < M  && 2*M <= nx && nx < 3*M) || (2*M <= ny && ny < 3*M && 0 <= nx && nx < M)) {
                            // 좌측 하단 또는 우측 상단
                            Index convert = convert(current.y, current.x, 1);
                            System.out.printf("convert to other wall, type 2, %d, %d\n", convert.x, convert.y);
                            if (!visited[convert.y][convert.x] && timeWall[convert.y][convert.x] != 1) {
                                visited[convert.y][convert.x] = true;
                                System.out.println("valid, added");

                                queue.add(convert);
                            }
                        } else {
                            visited[ny][nx] = true;
                            System.out.println("valid, added");
                            queue.add(new Index(ny,nx));
                        }
                    }
                }
            }
        }

        return -1;
    }

    public static int unknownSpaceBfs(int alreadyTime) {
        int time = alreadyTime + 1;
        // 이미 흐른 시간 처리(시간 이상 현상)
        for (TimeAnomaly ta : timeAnomalies) { // 어차피 10개 밖에 없으니까 매번 돌려 걍
            int move = time / ta.v;
            int ny = ta.r;
            int nx = ta.c;
            while (move -- > 0) {
                ny += dy[ta.d];
                nx += dx[ta.d];
                if (0 <= ny && ny < N && 0 <= nx && nx < N
                        && unknownSpace[ny][nx] != 1 && unknownSpace[ny][nx] != 3 && unknownSpace[ny][nx] != 4) unknownSpace[ny][nx] = -1; // 범위 안에 들어오고 장애물 or 탈출구 이면 X
            }
        }

        // bfs(이때 계속 시간 이상 현상 처리)
        boolean[][] visited = new boolean[N][N];

        Queue<Index> queue = new LinkedList<>();
        queue.add(unknownSpaceExitIndex); // 여기서 시작
        visited[unknownSpaceExitIndex.y][unknownSpaceExitIndex.x] = true;

        while (!queue.isEmpty()) {
            time++;
            // 시간 이상 현상 처리
            processTimeAnomaly(time);
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Index current = queue.poll();
                for (int j = 0; j < 4; j++) {
                    int ny = current.y + dy[j];
                    int nx = current.x + dx[j];

                    if (ny == escapeIndex.y && nx == escapeIndex.x) return time; // 탈출 처리

                    if (0 <= ny && ny < N && 0 <= nx && nx < N && !visited[ny][nx]
                            && unknownSpace[ny][nx] != 1 && unknownSpace[ny][nx] != -1 && unknownSpace[ny][nx] != 3) {
                        visited[ny][nx] = true;
                        queue.add(new Index(ny,nx));
                    }
                }
            }
        }

        return -1;
    }

    static void processTimeAnomaly(int currentTime) {

        for (int i = 0; i < timeAnomalies.size(); i++) {
            TimeAnomaly ta = timeAnomalies.get(i);
            if (currentTime % ta.v == 0) {
                int move = currentTime / ta.v;
                int ny = ta.r + (dy[ta.d] * move);
                int nx = ta.c + (dx[ta.d] * move);
                if (0 <= ny && ny < N && 0 <= nx && nx < N
                        && unknownSpace[ny][nx] != 1 && unknownSpace[ny][nx] != 3 && unknownSpace[ny][nx] != 4) unknownSpace[ny][nx] = -1;
                else {
                    timeAnomalies.remove(i);
                    i--;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken()); // 미지의 공간 한 변 길이
        M = Integer.parseInt(st.nextToken()); // 시간의 벽 한 변 길이
        F = Integer.parseInt(st.nextToken()); // 시간 이상 현상 갯 수

        // 미지의 공간, 시간의 벽, 시간의 이상 현상 초기화
        initUnknownSpace(br, st);
        initTimeWall(br, st);
        initTimeAnomalies(br, st);

        // 시간의 벽 to 미지의 공간 탈출 지점 찾기
        findExitToUnknownSpace();

        // timeMachine to timeWallExitIndex까지 탐색 시작
        int escapeTimeWall = timeWallBfs(); // 몇 초 걸리는지

        if (escapeTimeWall == -1) {
            System.out.println(-1);
            return;
        }
        // 미지의 공간 탐색 시작 이때 이미 흐른 시간 처리
        int finalTime = unknownSpaceBfs(escapeTimeWall);

        if (finalTime == -1) System.out.println(-1);
        else System.out.println(finalTime);
    }
}


