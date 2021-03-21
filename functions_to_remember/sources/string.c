#include "../headers/string.h"

void string_reverse(char* str)
{
    int size = 0;
    char* temp = str;
    while (*temp != '\0')
    {
        size++;
        temp++;
    }
    for (int i = 0; i < size / 2; i++)
    {
        char t = str[i];
        str[i] = str[size - 1 - i];
        str[size - 1 - i] = t;
    }
}

int string_count_words(char* str)
{
    int count = 0, flag = 0; // flag for if the word started with space
    char space = ' '; // can also be 32
    // If the string starts with spaces
    while (*str != '\0')
    {
        if (*str == space)
        {
            str++;
            flag = 1;
        }
        else break;
    }
    if (*str == '\0') return 0;
    if (flag == 0) count++;
    while (*str != '\0')
    {
        if (*str >= 'A' && *str <= 'Z')
        {
            if(*(str - 1) == space) count++;
        }
        else if (*str >= 'a' && *str <= 'z')
        {
            if(*(str - 1) == space) count++;
        }
        str++;
    }
    return count;
}

void string_to_uppercase(char* str)
{
    while (*str != '\0')
    {
        if (*str >= 'a' && *str <='z') *str -= 32;
        str++;
    }
}

void string_to_lowercase(char* str)
{
    while (*str != '\0')
    {
        if (*str >= 'A' && *str <='Z') *str += 32;
        str++;
    }
}