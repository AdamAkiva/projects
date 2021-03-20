#ifndef __ARRAYS__
#define __ARRAYS__

void print_integer_array(int* arr, int size);
void array_shift_left(int* arr, int size, int times);
void array_shift_right(int* arr, int size, int times);
int* arrays_merge(int* arr1, int size, int* arr2, int size2, int* size3);
void array_reverse(int* arr, int size);
int array_max_value(int* arr, int size);
void array_max_3_values(int* arr, int size, int* max, int max_size);
int array_min_value(int* arr, int size);
void array_min_3_values(int* arr, int size, int* min, int min_size);
int array_remove_value(int* arr, int size, int value);

void selection_sort(int* arr, int size);
void bubble_sort(int* arr, int size);
void insertion_sort(int* arr, int size);

#endif