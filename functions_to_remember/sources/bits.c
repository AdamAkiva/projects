#include "../headers/bits.h"

// Params: x - integer, y - integer
// Retrun: x + y using bitwise operators
int add_numbers(int x, int y)
{
    while (y != 0)
    {
        int carry = x & y;  
        x = x ^ y; 
        y = carry << 1;
    }
    return x;
}

// Params: x - integer, y - integer
// Retrun: x - y using bitwise operators
int subtract_numbers(int x, int y)
{
     while (y != 0)
    {
        int borrow = (~x) & y;
        x = x ^ y;
        y = borrow << 1;
    }
    return x;
}

// Params: x - integer, y - integer
// Retrun: x * y using bitwise operators
int multiply_numbers(int x, int y)
{
    int res = 0;
    while (y != 0)
    {
        if (y & 1) res = add_numbers(res, x);
        x <<= 1;
        y >>= 1;
    }
    return res;
}