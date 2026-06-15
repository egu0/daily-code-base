package im.engure.list;

import java.util.Scanner;

/*
HJ51 输出单向链表中倒数第k个结点
 */
public class HJ51 {
    static int N;
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while(in.hasNext()) {
            int n = in.nextInt();
            Node head = new Node(in.nextInt(), null);
            Node cur = head;
            for (int i=1; i<n; i++ ){
                cur.next = new Node(in.nextInt(), null);
                cur = cur.next;
            }
            N = in.nextInt();
            process(head);
        }
    }

    public static int process(Node cur) {
        if (cur.next == null) {
            if (N == 1) {
                System.out.println(cur.x);
            }
            return 1;
        }
        int n = process(cur.next) + 1;
        if (n == N) {
            System.out.println(cur.x);
        }
        return n;
    }
}

class Node {
    int x;
    Node next;
    public Node(int x, Node n) {
        this.x = x;
        this.next = n;
    }
}