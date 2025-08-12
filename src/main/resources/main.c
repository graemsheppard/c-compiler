int testVar = 1;

int func1(int i, int j) {
    return i + j;
}

int func2(int i, int j) {
    return i * j;
}

exit(func1(2, func2(2, 4)));