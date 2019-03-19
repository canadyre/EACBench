/*
*/

#define N 32
#define M 32
#define P 32

typedef int BaseType;

#pragma SDS data access_pattern(A0:SEQUENTIAL, B0:SEQUENTIAL, AB:SEQUENTIAL)
void matrixmul(BaseType A0[N*M], BaseType B0[M*P], BaseType AB[N*P]);

