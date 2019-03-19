/*
*/

#include<stdio.h>
#include <stdlib.h>
//#include<iostream>
#include <math.h>
#include "matrixmultiplication.h"

#include <stdint.h>
#include "sds_lib.h"

BaseType A[N][M];
BaseType B[M][P];
BaseType AB[N][P];

int main()
{
	BaseType *A, *B, *AB;

	A = (BaseType *)sds_alloc(N * M * sizeof(BaseType));
	B = (BaseType *)sds_alloc(M * P * sizeof(BaseType));
	AB= (BaseType *)sds_alloc(N * P * sizeof(BaseType));

	if (!A || !B || !AB) {
		  if (A) sds_free(A);
		  if (B) sds_free(B);
		  if (AB) sds_free(AB);
	}

	FILE *fp;
	printf("INPUTS\n");
	for(int i=0; i<N; i++) {
		for(int j=0;j<M; j++) {
			A[i*M+j] = i+j;
		}
	}
	for(int i=0; i<M; i++) {
		for(int j=0;j<P; j++) {
			B[i*P+j] = i+j;
		}
	}
	matrixmul(A,B,AB);

	//Print output
	fp=fopen("matrixmultiplication.out.dat", "w");
	printf("Output:\n");
	for(int i=0; i<N; i++) {
		printf("%4d\t ", i);
		for(int j=0;j<P; j++) {
			printf("%d ",AB[i*P+j]);
			fprintf(fp, "%4d\t%4d\t%d\n",i,j,AB[i*P+j]);
		}
		printf("\n");
	}
	fclose(fp);

    sds_free(A);
    sds_free(B);
    sds_free(AB);



}
