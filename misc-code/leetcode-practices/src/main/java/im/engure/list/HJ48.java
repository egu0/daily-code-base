package im.engure.list;

import java.util.Scanner;

/*
HJ48 从单向链表中删除指定值的节点
https://www.nowcoder.com/practice/f96cd47e812842269058d483a11ced4f
 */
public class HJ48 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        n--;
        Node head = new Node(in.nextInt());
        while (n-- > 0) {
            int val = in.nextInt();
            int prt = in.nextInt();
            Node cur = head;
            while (cur != null) {
                if (cur.x == prt) {
                    cur.next = new Node(val, cur.next);
                    break;
                }
                cur = cur.next;
            }
        }
        int del = in.nextInt();
        Node cur = head;
        while (cur != null) {
            if (cur.x != del) {
                System.out.print(cur.x + " ");
            }
            cur = cur.next;
        }
    }

    static class Node {
        int x;
        Node next;

        public Node(int i) {
            x = i;
        }

        public Node(int i, Node n) {
            x = i;
            next = n;
        }
    }
}
