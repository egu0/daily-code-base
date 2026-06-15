package im.engure.backtrace;

/**
 * 有效数独
 *
 * @author engure
 */
class IsValidSudoku36 {
    public boolean isValidSudoku(char[][] board) {
        // i行是否存在j，0否，1是
        int[][] rowE = new int[9][9];
        int[][] colE = new int[9][9];
        // i区是否存在j
        int[][] areE = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != '.') {
                    int num = board[i][j] - '1';
                    int areNum = 3 * (i / 3) + j / 3;
                    if (rowE[i][num] == 1 || colE[j][num] == 1 || areE[areNum][num] == 1) {
                        return false;
                    }
                    rowE[i][num] = 1;
                    colE[j][num] = 1;
                    areE[areNum][num] = 1;
                }
            }
        }
        return true;
    }
}