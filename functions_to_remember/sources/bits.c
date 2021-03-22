#include "../headers/bits.h"

int add_numbers(int a, int b)
{
    while (b != 0)
    {
        int carry = a & b;  
        a = a ^ b; 
        b = carry << 1;
    }
    return a;
}

int subtract_numbers(int a, int b)
{
     while (b != 0)
    {
        int borrow = (~a) & b;
        a = a ^ b;
        b = borrow << 1;
    }
    return a;
}

int multiply_numbers(int a, int b)
{
    int res = 0;
    while (b != 0)
    {
        if (b & 1) res = add_numbers(res, a);
        a <<= 1;
        b >>= 1;
    }
    return res;
}