#include <stdio.h>
#include <stdlib.h>
#include <limits.h>

// Params: arr - int[], size - integer array size
// Prints array in a single line
void print_integer_array(int* arr, int size)
{
    for (int i = 0; i < size; i++) printf("%d ", arr[i]);
    printf("\n");
}

// Params: arr - integer[] to left shift, size - integer array size, times - amount of left shifts
// Shifts integer[] times to the left
void array_shift_left(int* arr, int size, int times)
{
    for (int i = 0; i < times; i++)
    {
        int first = arr[0];
        for (int j = 0; j < size - 1; j++) arr[j] = arr[j + 1];
        arr[size - 1] = first;
    }
}

// Params: arr - integer[] to right shift, size - integer array size, times - amount of right shifts
// Shifts integer[] times to the right
void array_shift_right(int* arr, int size, int times)
{
    for (int i = 0; i < times; i++)
    {
        int last = arr[size - 1];
        for (int j = size - 1; j > 0; j--) arr[j] = arr[j - 1];
        arr[0] = last;
    }
}

// Params: arr1 - sorted integer[], size1 - size of arr1, arr2 - sorted integer[], size2 - size of arr2,
// res_size - size of the merged integer[]
// Merge two sorted integer[] to one
// Return: integer[] containing both arrays in order
int* arrays_merge(int* arr1, int size1, int* arr2, int size2, int* res_size)
{
    *res_size = size1 + size2;
    int* res = (int*) malloc(sizeof(int) * (*res_size));
    int a1_index = 0, a2_index = 0 , res_index = 0;
    while (a1_index < size1 && a2_index < size2)
    {
        if (arr1[a1_index] < arr2[a2_index]) res[res_index++] = arr1[a1_index++];
        else if (arr2[a2_index] < arr1[a1_index]) res[res_index++] = arr2[a2_index++];
        else
        {
            res[res_index++] = arr1[a1_index];
            a1_index++;
            a2_index++;
        }
    }
    if (a1_index < size1)
        while (a1_index < size1) res[res_index++] = arr1[a1_index++];
    else if (a2_index < size2)
        while (a2_index < size2) res[res_index++] = arr2[a2_index++];
    if (*res_size != res_index)
    {
        *res_size = res_index;
        res = (int*) realloc(res, sizeof(int) * (*res_size));
    }
    return res;
}

// Params: arr - integer[], size - size of arr
// Reverse given integer[]
void array_reverse(int* arr, int size)
{
    for (int i = 0; i < size / 2; i++)
    {
        int t = arr[size - 1 - i];
        arr[size - 1 - i] = arr[i];
        arr[i] = t;
    }
}

// Params: arr - integer[], size - size of arr
// Find the max value in the array and return it, 0 if not found or 0 is the max value
int array_max_value(int* arr, int size)
{
    if (size == 0) return 0;
    int max = arr[0];
    for (int i = 1; i < size; i++)
        if (max < arr[i]) max = arr[i];
    return max;
}

// Params: arr - integer[], size - size of arr
// Find the third max value in the array and return it, 0 if the array size <= 3 or 0 is the max value
int array_max_third_value(int* arr, int size)
{
    if (size <= 3) return 0;
    int max[3];
    for (int i = 0; i < 3; i++) max[i] = INT_MIN; // (-2^31)
    for (int i = 0; i < size; i++)
    {
        if (arr[i] > max[0])
        {
            max[2] = max[1];
            max[1] = max[0];
            max[0] = arr[i];
        }
        else if (arr[i] > max[1])
        {
            max[2] = max[1];
            max[1] = arr[i];
        }
        else if (arr[i] > max[2])
        {
            max[2] = arr[i];
        }
    }
    return max[2];
}

// Params: arr - integer[], size - size of arr
// Find the min value in the array and return it, 0 if not found or 0 is the min value
int array_min_value(int* arr, int size)
{
    if (size == 0) return 0;
    int min = arr[0];
    for (int i = 0; i < size; i++)
        if (min > arr[i]) min = arr[i];
    return min;
}

// Params: arr - integer[], size - size of arr
// Find the third min value in the array and return it, 0 the array size <= 3 or 0 is the min value
int array_min_third_value(int* arr, int size)
{
    if (size <= 3) return 0;
    int min[3];
    for (int i = 0; i < 3; i++) min[i] = INT_MAX; // (2^31 -1)
    for (int i = 0; i < size; i++)
    {
        if (arr[i] < min[0])
        {
            min[2] = min[1];
            min[1] = min[0];
            min[0] = arr[i];
        }
        else if (arr[i] < min[1])
        {
            min[2] = min[1];
            min[1] = arr[i];
        }
        else if (arr[i] < min[2])
        {
            min[2] = arr[i];
        }
    }
    return min[2];
}

// Params: arr - integer[], size - size of arr, value - value to remove
// Remove value from the array
// Return: modified array size
int array_remove_value(int* arr, int size, int value)
{
    int counter = 0;
    for (int i = 0; i < size; i++)
    {
        if (arr[i] == value)
        {
            counter++;
            for (int j = i; j < size - counter - 1; j++) arr[j] = arr[j + 1];
        }
    }
    arr = (int*) realloc(arr, sizeof(int) * (size - counter));
    return size - counter;
}

// Params: arr - integer[], size - size of arr
// Sort integer[] using selection sort, see: https://www.geeksforgeeks.org/selection-sort/
void selection_sort(int* arr, int size)
{
    for (int i = 0; i < size - 1; i++)
    {
        int min_index = i;
        for (int j = i + 1; j < size; j++)
        {
            if (arr[j] < arr[min_index]) min_index = j;
        }
        if (min_index != i)
        {
            int t = arr[i];
            arr[i] = arr[min_index];
            arr[min_index] = t;
        }
    }
}

// Params: arr - integer[], size - size of arr
// Sort integer[] using bubble sort, see: https://www.geeksforgeeks.org/bubble-sort/
// (Exmaple for max runs to see why i and j until given value, do it for 5 4 3 2 1)
void bubble_sort(int* arr, int size)
{
    for (int i = 0; i < size - 1; i++)
    {
        for (int j = 0; j < size - i - 1; j++)
        {
            if (arr[j] > arr[j + 1])
            {
                int t = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = t;
            }
        }
    }
}

// Params: arr - integer[], size - size of arr
// Sort integer[] using insertion sort, see: https://www.geeksforgeeks.org/insertion-sort/
void insertion_sort(int* arr, int size)
{
    int i, value, j;
    for (i = 1; i < size; i++) {
        value = arr[i];
        j = i - 1;
        while (j >= 0 && arr[j] > value) {
            arr[j + 1] = arr[j];
            j = j - 1;
        }
        arr[j + 1] = value;
    }
}