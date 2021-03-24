#ifndef __ARRAYS__
#define __ARRAYS__

#include <stdio.h>
#include <stdlib.h>
#include <limits.h>

void print_integer_array(int*, int);
void array_shift_left(int*, int, int);
void array_shift_right(int*, int, int);
int* arrays_merge(int*, int, int*, int, int*);
void array_reverse(int*, int);
int array_max_value(int*, int);
int array_max_third_value(int*, int);
int array_min_value(int*, int);
int array_min_third_value(int*, int);
int array_remove_value(int*, int, int);
void selection_sort(int*, int);
void bubble_sort(int*, int);
void insertion_sort(int*, int);

#endif