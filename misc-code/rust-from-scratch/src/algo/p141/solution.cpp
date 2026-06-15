#include<iostream>

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
    // fast-slow pointer
    bool hasCycle(ListNode *head) {
	if (head == NULL or head->next == NULL) {
		return false;
	}
	
	ListNode *slow = head;
	ListNode *fast = head->next;

	while (slow != fast) {
	   if (fast == NULL || fast->next == NULL) {
	       return false;
	   }

	   slow = slow->next;
	   fast = fast->next->next;
	}

        return true;
    }
};

