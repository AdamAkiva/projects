#include <stdio.h>
#include <stdlib.h>

#include "../headers/etc.h"

float power(int base, int exponent)
{
    if (base == 0) return 0;
    if (exponent == 0) return 1;
    int exp = exponent < 0 ? exponent *= -1 : exponent; 
    int res = 1;
    for (int i = 0; i < exp; i++) res *= base;
    return exponent < 0 ? 1 / res : res;
}

void find_primes_up_to(int limit)
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
    for (int i = 0; i <= limit; i++)
    {
        if (temp[i] == '1')
        {
            printf("%d ", i);
        }
    }
}

void find_fibonacci_up_to(int limit)
{
    int f1 = 0, f2 = 1, f3 = 1;
    printf("0, 1");
    while (f3 <= limit)
    {
        printf(" %d ", f1);
        f1 = f2;
        f2 = f3;
        f3 = f1 + f2;
    }
    printf("\n");
}