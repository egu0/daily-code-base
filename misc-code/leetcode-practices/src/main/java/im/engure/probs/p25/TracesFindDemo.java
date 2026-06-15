package im.engure.probs.p25;

/*

给出一个矩阵，0代表不可走，1代表可走。
给出其中两点，分别代表起点和终点，问两者是否可达。


1 1 1 1
0 0 0 0
1 1 1 1
1 1 0 1

 */

public class TracesFindDemo {

    public static void main(String[] args) {
        int[][] matrix = new int[][]{{1, 1, 1, 1}, {0, 0, 0, 0}, {1, 1, 1, 1}, {1, 1, 0, 1}};
        System.out.println(go(matrix, 0, 0, 0, 3));
        System.out.println(go(matrix, 0, 0, 3, 3));
        System.out.println(go(matrix, 2, 1, 3, 3));
    }

    static boolean go(int[][] matrix, int srcX, int srcY, int targetX, int targetY) {
        int[][] visit = new int[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) visit[i][j] = -1;
            }
        }
        return process(matrix, visit, srcX, srcY, targetX, targetY);
    }

    static boolean process(int[][] matrix, int[][] visit, int srcX, int srcY, int targetX, int targetY) {
        if (srcX < 0 || srcX >= matrix.length || srcY < 0 || srcY >= matrix[0].length) return false;
        if (srcX == targetX && srcY == targetY) return true;
        if (visit[srcX][srcY] == 1 || visit[srcX][srcY] == -1) return false;
        visit[srcX][srcY] = 1;//走过
        boolean l = process(matrix, visit, srcX, srcY - 1, targetX, targetY);
        boolean r = process(matrix, visit, srcX, srcY + 1, targetX, targetY);
        boolean t = process(matrix, visit, srcX - 1, srcY, targetX, targetY);
        boolean b = process(matrix, visit, srcX + 1, srcY, targetX, targetY);
        return l || r || t || b;
    }

}
