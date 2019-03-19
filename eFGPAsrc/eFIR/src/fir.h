#ifndef _FIR_H_
#define _FIR_H_

#define NUM_TAPS 16

#pragma SDS data access_pattern(input:RANDOM,taps:SEQUENTIAL)
void fir(int input, int *output, int taps[NUM_TAPS]);


#endif /* _FIR_H_ */
