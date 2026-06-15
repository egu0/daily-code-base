#include<cstdio>
#include<iostream>
#include<vector>

using namespace std;

int main() {
	cout << "hi" << endl;
	return 0;
}

struct ListNode {
    int val;
    ListNode *next;
    ListNode(int x) : val(x), next(NULL) {}
};

class Solution {
public:
    // O(m+n) time, O(1) Space
    ListNode *getIntersectionNode(ListNode *headA, ListNode *headB) {
	    NULL
    }


    /*
    // Time: O(m+n), Space: O(n)
    ListNode *getIntersectionNode(ListNode *headA, ListNode *headB) {
	vector<int> nums;
	ListNode *cur = headA;
	while (cur != NULL) {
	    nums.push_back(cur->val);
	    cur->val = 0;
	    cur = cur->next;
	}
	cur = headB;
	while (cur != NULL) {
	    if (cur->val == 0) {
		    break;
	    }
	    cur = cur->next;
	}

	ListNode *cur2 = headA;
	for(vector<int>::iterator it = nums.begin(); it != nums.end(); it++) {
		cur2->val = *it;
	    	cur2 = cur2->next;
	}

	return cur;
    }
    */
};








