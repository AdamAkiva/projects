#include <stdio.h>
#include <stdlib.h>

#include "../headers/menu.h"
#include "../headers/arrays.h"
#include "../headers/bits.h"
#include "../headers/pointers.h"
#include "../headers/etc.h"
#include "../headers/string.h"

void check_arrays()
{
    int* arr;
    int size;
    printf("Please enter the amount of numbers you want in the array: ");
    scanf("%d", &size);
    arr = (int*) malloc(sizeof(int) * size);
    printf("Please enter %d to populate the array: ", size);
    for (int i = 0; i < size; i++) scanf("%d", &arr[i]);
    printf("The inputted array: ");
    print_integer_array(arr, size);

    int shifts = 3;
    printf("Array shifted %d times to the right: ", shifts);
    array_shift_right(arr, size, shifts);
    print_integer_array(arr, size);
    printf("Array shifted %d times to the left: ", shifts);
    array_shift_left(arr, size, shifts);
    print_integer_array(arr, size);

    int* arr2;
    int size2, size3;
    printf("Please enter the amount of numbers you want in the second array: ");
    scanf("%d", &size2);
    arr2 = (int*) malloc(sizeof(int) * size2);
    printf("Please enter %d to populate the second array: ", size2);
    for (int i = 0; i < size2; i++) scanf("%d", &arr2[i]);
    printf("The inputted array: ");
    print_integer_array(arr2, size2);

    printf("Sorted first array with selection sort: ");
    selection_sort(arr, size);
    print_integer_array(arr, size);
    printf("Sorted second array with bubble sort: ");
    bubble_sort(arr2, size2);
    print_integer_array(arr2, size2);

    printf("Merged arrays: ");
    int* res = arrays_merge(arr, size, arr2, size2, &size3);
    print_integer_array(res, size3);

    printf("Reversed array: ");
    array_reverse(res, size3);
    print_integer_array(res, size3);

    printf("Max value: %d\n", array_max_value(res, size3));
    printf("Third max value in the array: %d\n", array_max_third_value(res, size3));

    printf("Min value: %d\n", array_min_value(res, size3));
    printf("Third min value in the array: %d\n", array_min_third_value(res, size3));

    int remove;
    printf("Please enter a value to remove from the array: ");
    scanf("%d", &remove);
    size3 = array_remove_value(res, size3, remove);

    printf("Array after removal: ");
    print_integer_array(res, size3);

    free(res);
}

void check_bits()
{
    int x, y;
    printf("Please enter two numbers: ");
    scanf("%d %d", &x, &y);
    printf("Summary of both numbers using bits: %d\n", add_numbers(x, y));
    printf("Substraction of both numbers using bits: %d\n", subtract_numbers(x, y));
    printf("Multiplication of both numbers using bits: %d\n", multiply_numbers(x, y));
}

void check_pointers()
{
    int x, y;
    int* pX = &x, *pY = &y;
    printf("Please enter two numbers: ");
    scanf("%d %d", &x, &y);
    printf("Before swap: %d %d\n", *pX, *pY);
    swap_pointers((void**) &pX, (void**) &pY);
    printf("After first swap: %d %d\n", *pX, *pY);
    swap_integer_values(pX, pY);
    printf("After second swap: %d %d\n", *pX, *pY);
}

void check_etc()
{
    float base;
    int exponent, limit, n;
    printf("Please enter a base and an exponent: ");
    scanf("%f %d", &base, &exponent);
    printf("%f^%d = %f\n", base, exponent, power(base, exponent));
    printf("Please enter up to where you want to print prime numbers: ");
    scanf("%d", &limit);
    char* temp = find_primes_up_to(limit);
    for (int i = 2; i <= limit; i++)
        if (temp[i] == '1') printf("%d ", i);
    printf("\n");
    printf("Please enter up to where you want to print fibonacci sequence: ");
    scanf("%d", &limit);
    int* res = find_fibonacci_up_to(limit, &n);
    print_integer_array(res, n);
    printf("Please enter the amount of elements of fibonacci sequence you want to display: ");
    scanf("%d", &n);
    res = number_of_elements_for_fibonacci(n);
    print_integer_array(res, n);
    free(res);
    free(temp);
}

void check_string()
{
    _flushall();
    char str[255];
    printf("Please enter a string: ");
    gets(str);
    printf("Inputted string: %s\n", str);
    string_reverse(str);
    printf("Reversed string: %s\n", str);
    printf("The amount of words in the string is: %d\n", string_count_words(str));
    string_to_uppercase(str);
    printf("String uppercase: %s\n", str);
    string_to_lowercase(str);
    printf("String lowercase: %s\n", str);
}