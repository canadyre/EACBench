#include "matrixmultiplication.h"

void matrixmul(BaseType A0[N*M], BaseType B0[M*P], BaseType AB[N*P]) {
    BaseType A[N][M], B[M][P];
	#pragma HLS array_partition variable=A block factor=16 dim=1
	#pragma HLS array_partition variable=B block factor=16 dim=1

		for(int i=0; i<N; i++) {
			 for(int j=0; j<M; j++) {
	#pragma HLS PIPELINE
				  A[i][j] = A0[i * N + j];
			 }
		}

		for(int i=0; i<M; i++) {
			 for(int j=0; j<P; j++) {
	#pragma HLS PIPELINE
				  B[i][j] = B0[i * P + j];
			 }
		}

	//#pragma HLS ARRAY_RESHAPE variable=A complete dim=2
    //#pragma HLS ARRAY_RESHAPE variable=B complete dim=1
	  /* for each row and column of AB */
	  row: for(int i = 0; i < N; ++i) {
		col: for(int j = 0; j < P; ++j) {
#pragma HLS PIPELINE
		  /* compute (AB)i,j */
		  BaseType ABij = 0;
		product: for(int k = 0; k < M; ++k) {
			ABij += A[i][k] * B[k][j];
		  }

		  AB[i * P + j] = ABij;
		}
	  }
}
