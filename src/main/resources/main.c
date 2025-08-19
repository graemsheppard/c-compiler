int newln = 10;
int func (int i) {
    if (i <= 1) {
        return i;
    }
    return i * func(i - 1);
}

int len = 0;
int* str = itoa(func(5),&len);

print(str, len);
print(&newln, 1);
