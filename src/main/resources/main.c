int t = 0;
if(t != 0) {
    exit(1);
} else {
    int* str = itoa(10000, &t);
    int newLn = 10;
    print(str, t);
    print(&newLn, 1);
}