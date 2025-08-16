int t = 0;
int k = 0;
int test[10];
t = test[1];
int newln = 10;
if (t != 0) {
    int j = 1;
} else {
    while (k < 100) {
        int j = 0;
        while (j < 10) {
            int* str = itoa(k + j, &t);
            print(str, t);
            print(&newln, 1);
            j = j + 1;
        }
        k = k + 10;
    }
}
