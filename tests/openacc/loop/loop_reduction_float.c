#include <stdio.h>

int main()
{
  int i;
  float float_red;

  //sum
  float_red = 3.0;
#pragma acc parallel
#pragma acc loop reduction(+:float_red)
  for(i=0;i<10000;i++){
    float_red += 1.0;
  }
  //verify
  if(float_red != 10003){
    return 1;
  }

  //mul
  float_red = 2.0;
#pragma acc parallel
#pragma acc loop reduction(*:float_red)
  for(i=0;i<128;i++){
    if(i%8 == 0){
      float_red *= 2.0;
    }else{
      float_red *= 1.0;
    }
  }
  if(float_red != 131072.0){
    return 2;
  }

  //max
  float_red = 123.0;
#pragma acc parallel
#pragma acc loop reduction(max:float_red)
  for(i=0;i<10000;i++){
    if(float_red < i){
      float_red = i;
    }
  }
  if(float_red != 9999){
    return 3;
  }

  //min
  float_red = 33333;
#pragma acc parallel
#pragma acc loop reduction(min:float_red)
  for(i=0;i<10000;i++){
    if(float_red > i-2){
      float_red = i-2.0;
    }
  }
  if(float_red != -2.0){
    return 4;
  }

  //max (result is negative value)
  float_red = -10;
#pragma acc parallel
#pragma acc loop reduction(max:float_red)
  for(i=-1; i > -1000;i--){
    if(float_red < i){
      float_red = i;
    }
  }
  if(float_red != -1){
    return 5;
  }

  printf("PASS\n");

  return 0;
}
