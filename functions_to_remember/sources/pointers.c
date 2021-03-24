#include <stdio.h>

#include "../headers/pointers.h"

// Params: v1 - void address of a void pointer, v2 - void address of a void pointer
// Swap pointers (not the values in them)
void swap_pointers(void** v1, void** v2)
{
    void* t = *v1;
    *v1 = *v2;
    *v2 = t;
}

// Params: n1 - pointer to an integer, n2 - pointer to an integer
// Swap the integer values
void swap_integer_values(int* n1, int* n2)
{
    int t = *n1;
    *n1 = *n2;
    *n2 = t;
}