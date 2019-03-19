
#ifndef H_SQRT_ACC_H
#define H_SQRT_ACC_H


#include <stdlib.h>
#include "ap_int.h"
#include "math.h"
#include "hls_math.h"

typedef ap_int<16> din_t;
typedef float dout_t;


#define N 10
#pragma SDS data access_pattern(a:SEQUENTIAL,b:SEQUENTIAL)
void normdist(din_t a[N], din_t b[N], dout_t *dout, int norm);

#endif //#ifndef H_SQRT_ACC_H


