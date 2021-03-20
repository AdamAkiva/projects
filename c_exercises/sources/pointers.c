#include "../headers/pointers.h"

void swap_pointers(void* v1, void* v2)
{
    void* t = v1;
    v1 = v2;
    v2 = t;
}

void swap_integer_values(int* n1, int* n2)
{
    int* t = n1;
    n1 = n2;
    n2 = t;
}