package Samsung_16_21A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.StringTokenizer;

public class Main {

    public static class Pair {
        int x;
        int y;

        Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Cube {
        int H;
        int E;
        int W;
        int S;
        int N;
        int L;
        char currentLow;

        Cube() {
            this.H = 0;
            this.E = 0;
            this.W = 0;
            this.S = 0;
            this.N = 0;
            this.L = 0;
            this.currentLow = 'L';
        }

        public int getHighValue() {
            return this.H;
        }

        public int getLowValue() {
            return this.L;
        }



        public void setLowValue(int value) {
            this.L = value;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("H : %d, L : %d, E : %d, W: %d, N: %d, S: %d\n", this.H, this.L, this.E, this.W, this.N, this.S));
            sb.append(String.format("currentHighValue : %d, currentLowValue : %d\n",
                    getHighValue(), getLowValue()));
            return sb.toString();
        }

        public void changeFace(int[] direction) {
            // Move east
            if (direction[0] == 1 && direction[1] == 0) {
                // H -> E -> L -> W, allocate reverse.
                int tmp = this.E;
                this.E = this.H;
                this.H = this.W;
                this.W = this.L;
                this.L = tmp;
            } else if (direction[0] == -1 && direction[1] == 0) {
                // H -> W -> L -> E, allocate reverse.
                int tmp = this.W;
                this.W = this.H;
                this.H = this.E;
                this.E = this.L;
                this.L = tmp;
            } else if (direction[0] == 0 && direction[1] == -1) {
                // H -> S -> L -> N, allocate reverse.
                int tmp = this.S;
                this.S = this.H;
                this.H = this.N;
                this.N = this.L;
                this.L = tmp;
            } else if (direction[0] == 0 && direction[1] == 1) {
                // H -> N -> L -> S, allocate reverse.
                int tmp = this.N;
                this.N = this.H;
                this.H = this.S;
                this.S = this.L;
                this.L = tmp;
            }
        }
    }

    public static int[][] faceMapValue;
    public static int[][] map;
    public static int[][] directionArray;
    public static int n, m, k;
    public static int startX, startY;


    // map of
    // x N x
    // E H W
    // x S x
    // x L x
    public static Pair changeFaceMapXY(int curFX, int curFY, int[] direction) {
        int nx = curFX + direction[0];
        int ny = curFY + direction[1];
        if ((nx == 0 && ny == 0) || (nx == 2 && ny == 2)) {
            nx = curFY;
            ny = curFX;
        } else if ((nx == 2 && ny == 0) || (nx == 0 && ny == 2)) {
            nx = 2 - curFY;
            ny = 2 - curFX;
        } else if (nx == 0 && ny == 3) {
            nx = 2;
            ny = 1;
        } else if (nx == 2 && ny == 3) {
            nx = 0;
            ny = 1;
        }
        return new Pair(nx, ny);
    }

    public static void initMap(StringTokenizer st, BufferedReader br) throws IOException {
        map = new int[n][m];
        for (int y = 0; y < n; y++) {
            st = new StringTokenizer(br.readLine());
            for (int x = 0; x < m; x++) {
                map[y][x] = Integer.parseInt(st.nextToken());
            }
        }
        faceMapValue = new int[3][2];
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 2; x++) {
                faceMapValue[y][x] = 0;
            }
        }
        st = new StringTokenizer(br.readLine());
        directionArray = new int[k][2];
        for (int i = 0; i < k; i++) {
            int x = Integer.parseInt(st.nextToken());
            if (x == 1) {
                directionArray[i][0] = 1;
                directionArray[i][1] = 0;
            } else if (x == 2) {
                directionArray[i][0] = -1;
                directionArray[i][1] = 0;
            } else if (x == 3) {
                directionArray[i][0] = 0;
                directionArray[i][1] = -1;
            } else if (x == 4) {
                directionArray[i][0] = 0;
                directionArray[i][1] = 1;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        startY = Integer.parseInt(st.nextToken());
        startX = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        initMap(st, br);

        int curX = startX;
        int curY = startY;

        Cube cube = new Cube();
        for (int[] direction : directionArray) {
            int nX = curX + direction[0];
            int nY = curY + direction[1];
            if (nX < 0 || nX >= m || nY < 0 || nY >= n) {
                continue;
            }
            if (map[nY][nX] == 0) {
                cube.changeFace(direction);
                map[nY][nX] = cube.getLowValue();
            } else {
                cube.changeFace(direction);
                cube.setLowValue(map[nY][nX]);
                map[nY][nX] = 0;
            }
//            System.out.println(cube);
            System.out.println(cube.getHighValue());
            curX = nX;
            curY = nY;
        }
    }
}
