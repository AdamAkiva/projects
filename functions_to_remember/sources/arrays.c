#include <stdio.h>
#include <stdlib.h>
#include <limits.h>

void print_integer_array(int* arr, int size)
{
    for (int i = 0; i < size; i++)
    {
        printf("%d ", arr[i]);
    }
    printf("\n");
}

void array_shift_left(int* arr, int size, int times)
{
    for (int i = 0; i < times; i++)
    {
        int first = arr[0];
        for (int j = 0; j < size - 1; j++)
        {
            arr[j] = arr[j + 1];
        }
        arr[size - 1] = first;
    }
}

void array_shift_right(int* arr, int size, int times)
{
    for (int i = 0; i < times; i++)
    {
        int last = arr[size - 1];
        for (int j = size - 1; j > 0; j--)
        {
            arr[j] = arr[j - 1];
        }
        arr[0] = last;
    }
}

int* arrays_merge(int* arr1, int size1, int* arr2, int size2, int* size3)
{
    int* res = (int*) malloc(sizeof(int) * (size1 + size2));
    int arr1_index = 0, arr2_index = 0 , i;
    for (i = 0; arr1_index < size1 && arr2_index < size2; i++)
    {
        if (arr1[arr1_index] > arr2[arr2_index]) res[i] = arr2[arr2_index++];
        else if (arr2[arr2_index] > arr1[arr1_index]) res[i] = arr1[arr1_index++];
        else
        {
            res[i] = arr1[arr1_index];
            arr1_index++;
            arr2_index++;
        }
    }
    if (arr1_index == size1)
        for (;arr2_index < size2;) res[i++] = arr2[arr2_index++];
    else
        for (;arr1_index < size1;) res[i++] = arr1[arr1_index++];
    res = (int*) realloc(res, sizeof(int) * (i - 1));
    *size3 = i - 1;
    return res;
}

void array_reverse(int* arr, int size)
{
    for (int i = 0; i < size / 2; i++)
    {
        int t = arr[size - 1 - i];
        arr[size - 1 - i] = arr[i];
        arr[i] = t;
    }
}

int array_max_value(int* arr, int size)
{
    if (size == 0) return 0;
    int max = arr[0];
    for (int i = 1; i < size; i++)
        if (max < arr[i]) max = arr[i];
    return max;
}

void array_max_3_values(int* arr, int size, int* max, int max_size)
{
    if (size <= 3) return;
    for (int i = 0; i < max_size; i++) max[i] = INT_MIN;
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
}

int array_min_value(int* arr, int size)
{
    if (size == 0) return 0;
    int min = arr[0];
    for (int i = 0; i < size; i++)
        if (min > arr[i]) min = arr[i];
    return min;
}

void array_min_3_values(int* arr, int size, int* min, int min_size)
{
    if (size <= 3) return;
    for (int i = 0; i < min_size; i++) min[i] = INT_MAX;
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
}

int array_remove_value(int* arr, int size, int value)
{
    int counter = 0;
    for (int i = 0; i < size; i++)
    {
        if (arr[i] == value)
        {
            counter++;
            for (int j = i; j < size - 1; j++) arr[j] = arr[j + 1];
        }
    }
    arr = (int*) realloc(arr, sizeof(int) * (size - counter));
    return size - counter;
}

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