#include <stdio.h>

#include "../headers/pointers.h"

void swap_pointers(void* v1, void* v2, size_t size)
{
    unsigned char* c1 = v1, *c2 = v2, temp;
    for (int i = 0; i < size; i++)
    {
        temp = c1[i];
        c1[i] = c2[i];
        c2[i] = temp;

        // also valid
        // temp = *(c1 + i);
        // *(c1 + i) = *(c2 + i);
        // *(c2 + i) = temp;
    }
}

void swap_integer_values(int* n1, int* n2)
{
    int t = *n1;
    *n1 = *n2;
    *n2 = t;
}