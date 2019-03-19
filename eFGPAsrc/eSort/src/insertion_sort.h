const static int SIZE = 16;
typedef int DTYPE;

#pragma SDS data access_pattern(A:SEQUENTIAL, Bout:SEQUENTIAL)
void insertion_sort_parallel(DTYPE A[SIZE], DTYPE Bout[SIZE]);
