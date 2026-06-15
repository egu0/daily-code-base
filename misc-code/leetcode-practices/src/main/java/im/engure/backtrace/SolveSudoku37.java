package im.engure.backtrace;

/**
 * 解数独，经典回溯题目。其它还有n皇后等等
 *
 * @author engure
 */
public class SolveSudoku37 {
    static int[][] rowE;
    static int[][] colE;
    static int[][] areE;

    public void solveSudoku(char[][] board) {
        rowE = new int[9][9];
        colE = new int[9][9];
        areE = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != '.') {
                    int num = board[i][j] - '1';
                    int areNum = 3 * (i / 3) + j / 3;
                    rowE[i][num] = 1;
                    colE[j][num] = 1;
                    areE[areNum][num] = 1;
                }
            }
        }
        process(board, 0, 0);
    }

    private boolean process(char[][] board, int i, int j) {
        if (i == 9 && j == 0) {
            return true;
        }
        if (board[i][j] == '.') {
            for (int k = 0; k < 9; k++) {
                int areNum = 3 * (i / 3) + j / 3;
                if (rowE[i][k] == 0 && colE[j][k] == 0 && areE[areNum][k] == 0) {
                    board[i][j] = (char) ('1' + k);
                    rowE[i][k] = 1;
                    colE[j][k] = 1;
                    areE[areNum][k] = 1;
                    boolean res = process(board, (j + 1 == 9) ? i + 1 : i, (j + 1 == 9) ? 0 : j + 1);
                    if (res) {
                        return true;
                    }
                    board[i][j] = '.';
                    rowE[i][k] = 0;
                    colE[j][k] = 0;
                    areE[areNum][k] = 0;
                }
            }
            return false;
        } else {
            return process(board, (j + 1 == 9) ? i + 1 : i, (j + 1 == 9) ? 0 : j + 1);
        }
    }

}
