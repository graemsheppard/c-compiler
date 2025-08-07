int testVar = 30;

int func1(int i, int j) {
    int h = 3;
    int func2(int k, int l) {
        exit(k + l + h);
    }
    int k = func2(i, j);
}

int m = func1(12, 22);
