package im.engure.binarytree.old;

public class ZheZhi {

	public static void main(String[] args) {
		func(1, 3, true);
	}

	public static void func(int i, int n, boolean b_ao) {
		if (i > n) {
			return;
		}

		// 上 凹
		func(i+1, n, true);

		System.out.println(i + ((b_ao)?"凹":"凸"));

		// 下 凸
		func(i+1, n, false);
	}
}
