#include "fir.h"


void fir(int input, int *output, int taps[NUM_TAPS])
{
	int result = 0;
	static int delay_line[NUM_TAPS] = {};
	int i;
	for(i = NUM_TAPS - 1;i > 1;i = i - 2){
#pragma HLS unroll factor=2
		delay_line[i] = delay_line[i - 1];
		delay_line[i - 1] = delay_line[i - 2];
	}
	if(i == 1){
		delay_line[1] = delay_line[0];
	}
	delay_line[0] = input;

	for (i = 0; i < NUM_TAPS; i++) {
//#pragma HLS PIPELINE
#pragma HLS unroll factor=16
		result += delay_line[i] * taps[i];
	}

	*output = result;
}
