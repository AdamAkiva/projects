#include <stdio.h>
#include <stdlib.h>

#include "../headers/etc.h"

// Params: base - integer, exponent - integer
// Return: float for x^y (float incase of negative exponent)
float power(float base, int exponent)
{
    float res = base;
    
    if (exponent < 0)
    {
        exponent *= -1;
        res = 1.0 / res;
    }
    
    for (int i = 1; i < exponent; i++)
    {
        res = res * base;
    }
    
    return res;
}

// Params: limit - integer for the upper limit of primes
// Return: char[] sized limit + 1, char[i] == '1': number is prime, char[i] == '0': number is not prime
char* find_primes_up_to(int limit)
{
    char* temp = malloc(sizeof(char) * (limit + 1));
    for (int i = 0; i <= limit; i++)
    {
        temp[i] = '1';
    }
    for (int i = 2; i*i <= limit; i++)
    {
        if (temp[i] == '1')
        {
            for (int j = i * 2; j <= limit; j += i)
            {
                temp[j] = '0';
            }
        }
    }
    return temp;
}

// Params: limit - integer for the upper limit of fibonacci sequence,
// size - pointer to an integer for  the number of elements in the array
// Return: integer[] with values
int* find_fibonacci_up_to(int limit, int* size)
{
    int* res = (int*) malloc(0);
    int f1 = 0, f2 = 1, f3 = 1, n = 0;
    while (f1 <= limit)
    {
        res = (int*) realloc(res, sizeof(int) * (n + 1));
        res[n++] = f1;
        f1 = f2;
        f2 = f3;
        f3 = f1 + f2;
    }
    *size = n;
    return res;
}

// Params: n - integer for the number of elements
// Return: integer[] with values
int* number_of_elements_for_fibonacci(int n)
{
    int* res = (int*) malloc(sizeof(int) * n);
    int f1 = 0, f2 = 1, f3 = 1;
    for (int i = 0; i < n; i++)
    {
        res[i] = f1;
        f1 = f2;
        f2 = f3;
        f3 = f1 + f2;
    }
    return res;
}