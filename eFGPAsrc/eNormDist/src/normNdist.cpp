//#include "hls_math.h"
#include "math.h"
#include "normNdist.h"

void normdist(din_t a[N], din_t b[N], dout_t *dout, int norm)
{

	int i;
	int acc= 0;
	int a_reg, b_reg, sub, sub2;

	switch (norm)
	{
	    case 0: // code to be executed if n = 1;
	    	for(i=0; i<N; i++)
	    	{
	    		#pragma HLS PIPELINE II=1

	    		a_reg  = a[i];
	    		b_reg  = b[i];
	    		sub = a_reg - b_reg;
	    		if(sub==0){
	    			acc += sub2;
	    		}
	    	}

	    	*dout = acc;
	    	break;
	    case 1:
	    	for(i=0; i<N; i++)
	    	{
	    		#pragma HLS PIPELINE II=1

	    		a_reg  = a[i];
	    		b_reg  = b[i];
	    		sub = a_reg - b_reg;
	    		sub2 = abs(sub);
	    		acc += sub2;
	    	}

	    	*dout = acc;
	        break;
	    case 2: // code to be executed if n = 2;
	    	for(i=0; i<N; i++)
	    	{
	    		#pragma HLS PIPELINE II=1

	    		a_reg  = a[i];
	    		b_reg  = b[i];
	    		sub = a_reg - b_reg;
	    		sub2 = sub*sub;
	    		acc += sub2;
	    	}

	    	*dout = sqrtf(acc);
	    	break;
	    default: // code to be executed if n doesn't match any cases
	    	for(i=0; i<N; i++)
	    	{
	    		#pragma HLS PIPELINE II=1

	    		a_reg  = a[i];
	    		b_reg  = b[i];
	    		sub = a_reg - b_reg;
	    		sub2 = sub*sub;
	    		acc += sub2;
	    	}

	    	*dout = sqrtf(acc);
	    	break;
	}


}

