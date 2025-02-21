package samsung_23_11A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Main {

    public static class Canon {
        int x, y, ad, lastTurn;
        boolean onAttackArea, isAlive;
        Canon before;

        Canon(int x, int y, int ad, int lastTurn) {
            this.x = x;
            this.y = y;
            this.ad = ad;
            this.lastTurn = lastTurn;
            this.onAttackArea = false;
            this.isAlive = true;
            this.before = null;
        }
    }

    public static int N, M, K;
    public static Canon[][] canonMap;
    public static Canon[] canons;
    public static int[][] directions = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

    public static void sortByCanonsStandard() {
        Arrays.sort(canons, (Canon c1, Canon c2) -> {
            if (c1.ad == c2.ad) {
                if (c1.lastTurn == c2.lastTurn) {
                    int c1Sum = c1.x + c1.y;
                    int c2Sum = c2.x + c2.y;
                    if (c1Sum == c2Sum) {
                        return c2.x - c1.x;
                    } else {
                        return c2Sum - c1Sum;
                    }
                } else {
                    return c2.lastTurn - c1.lastTurn;
                }
            } else {
                return c1.ad - c2.ad;
            }
        });
    }

    public static int changeX(int x, int dx) {
        int nx = x + dx;
        if (nx == -1) {
            nx = M - 1;
        } else if (nx == M) {
            nx = 0;
        }
        return nx;
    }

    public static int changeY(int y, int dy) {
        int ny = y + dy;
        if (ny == -1) {
            ny = N - 1;
        } else if (ny == N) {
            ny = 0;
        }
        return ny;
    }

    /**
     * if laser can reach target, mark onAttackArea of all the canons on route from attacker to target.
     * Attacker is not marked.
     * if laser cannot reach target, mark nothing.
     *
     * @param attacker
     * @param target
     */
    public static void getCanonsOnLaserBfs(Canon attacker, Canon target) {

        Canon cur = attacker;
        ArrayDeque<Canon> queue = new ArrayDeque<>();
        queue.add(cur);

        // initialize before each turn.
        for (Canon canon : canons) {
            canon.before = null;
            canon.onAttackArea = false;
        }
        attacker.before = attacker;
        boolean canReachTarget = false;
        while (!queue.isEmpty()) {
            cur = queue.poll();
//            System.out.println("current, ");
//            showCanon(cur);
            if (cur == target) {
                canReachTarget = true;
                break;
            }
            for (int[] dir : directions) {
                int nx = changeX(cur.x, dir[0]);
                int ny = changeY(cur.y, dir[1]);
//                System.out.printf("cx, cy : (%d, %d), nx, ny : (%d, %d)\n", cur.x, cur.y, nx, ny);
//                System.out.println("next, ");
//                showCanon(canonMap[ny][nx]);
                if (canonMap[ny][nx].isAlive && canonMap[ny][nx].before == null) {
//                    System.out.println("is added");
                    canonMap[ny][nx].before = cur;
                    queue.add(canonMap[ny][nx]);
                }
            }
        }
        if (!canReachTarget) {
            return;
        }


        // now, cur is target.
        cur.onAttackArea = true;
        while (true) {
            cur = cur.before;
            cur.onAttackArea = true;
            if (cur == attacker) {
                break;
            }
        }

        // now, cur is attacker.
        cur.onAttackArea = false;
    }

    public static void getCanonsOnBomb(Canon attacker, Canon target) {
        for (int y = target.y - 1; y <= target.y + 1; y++) {
            for (int x = target.x - 1; x <= target.x + 1; x++) {
                int nx = changeX(x, 0);
                int ny = changeY(y, 0);
                canonMap[ny][nx].onAttackArea = true;
            }
        }

        // exclude attacker.
        attacker.onAttackArea = false;
    }

    public static void doDamageOfAttack(Canon attacker, Canon target, int k) {

        // record turn
        attacker.lastTurn = k;

        int damage = attacker.ad + N + M;

        for (Canon canon : canons) {
            if (canon.isAlive && canon.onAttackArea) canon.ad -= damage / 2;
        }
        // normalize. target -> t.ad - original damage, attacker -> damage.
        attacker.ad = damage;
        target.ad -= damage - (damage / 2);
    }

    public static void checkCanonsAlive() {
        for (Canon canon : canons) {
            if (canon.ad <= 0) {
                canon.ad = 0;
                canon.isAlive = false;
            }
        }
    }

    public static void repairCanons(Canon attacker) {

        // +1 for all canons alive
        for (Canon canon : canons) {
            if (canon.isAlive) canon.ad += 1;
        }

        // except for attacker, and onAttackArea canons
        for (Canon canon : canons) {
            if (canon.isAlive && canon.onAttackArea) {
                canon.ad -= 1;
            }
        }
        attacker.ad -= 1;
    }

    public static int calculateRemainCanons() {
        int remain = 0;
        for (Canon canon : canons) {
            remain += canon.isAlive ? 1 : 0;
        }

        return remain;
    }

    public static void showMap() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < M; x++) {
                sb.append(canonMap[y][x].ad + " ");
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

    public static void showCanons() {
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < M; x++) {
                showCanon(canonMap[y][x]);
            }
        }
    }

    public static void showCanon(Canon c){

        System.out.printf("Canon at (%d, %d) with ad : %d, ", c.x, c.y, c.ad);
        System.out.printf("is currently being on attackArea : %b, ", c.onAttackArea);
        System.out.printf("last turn : %d, and before : %s\n", c.lastTurn,
                c.before == null ? "null" : String.format("Canon at :%d, %d", c.before.x, c.before.y));
    }

    public static void proceed() {

        // before proceeding, we should kill all the initial - ad - 0 canons
        checkCanonsAlive();


        for (int k = 1; k <= K; k++) {
            // check remaining canons
            if (calculateRemainCanons() == 1) {
                break;
            }
//            System.out.printf("current turn : %d, before attack, map is : \n", k);
//            showMap();


            // select Attacker, target
            // in this sorting, Dead canons would be on the first. So, we should conside it.
            sortByCanonsStandard();
//            System.out.println("canon sorted");
//            for (Canon c : canons) {
//                showCanon(c);
//            }
            int attackerIndex = 0;
            Canon attacker = canons[0];
            while(!attacker.isAlive){
                attacker = canons[attackerIndex++];
            }

            Canon target = canons[canons.length - 1];

//            System.out.println("attacker, ");
//            showCanon(attacker);
//
//            System.out.println("target, ");
//            showCanon(target);
            // select the form of attack, and mark onAttackArea
            // initializing for each canon is done on getCanonsOnLaserBfs
            getCanonsOnLaserBfs(attacker, target);
            if (!target.onAttackArea) {
                getCanonsOnBomb(attacker, target);
            }
//            System.out.println("after bfs, cannon is");
//            showCanons();


            // do damage and record turn.
            // turn start from 1.
            doDamageOfAttack(attacker, target, k);

//            System.out.println("after attack, cannon is");
//            showCanons();


            checkCanonsAlive();

            repairCanons(attacker);

//            System.out.printf("all turn doen, map is : \n");
//            showMap();
        }

        int maxAd = -1;
        for (Canon canon : canons) {
            if (canon.ad > maxAd) {
                maxAd = canon.ad;
            }
        }
        System.out.println(maxAd);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        // init.
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        canonMap = new Canon[N][M];
        canons = new Canon[N * M];

        for (int y = 0; y < N; y++) {
            st = new StringTokenizer(br.readLine());
            for (int x = 0; x < M; x++) {
//                System.out.println(x + "," + y);
                int ad = Integer.parseInt(st.nextToken());
                canonMap[y][x] = new Canon(x, y, ad, 0);
                canons[M * y + x] = canonMap[y][x];
            }
        }


        // proceed
        proceed();

    }
}
