int k = 0;
int arr[10];
int newln = 10;

while (k < 10) {
    arr[k] = k;
    k = k + 1;
}

k = 0;
while (k < 10) {
    int len = 0;
    int* str = itoa(arr[k], &len);
    print(str, len);
    print(&newln, 1);
    k = k + 1;
}
