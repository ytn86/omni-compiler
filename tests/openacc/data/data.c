#include <stdio.h>
#define N 100

int test_func(int a[N], char correct_val);
void test_func2(int dev_a[N]);

int main()
{
  int a[N];
  int i;

#pragma acc data copy(a)  
#pragma acc parallel loop
  for(i = 0; i < N; i++){
    a[i] = i;
  }

  //check
  for(i = 0; i < N; i++){
    if(a[i] != i) return 1;
  }


#pragma acc data copyin(a)
#pragma acc parallel loop
  for(i = 0; i < N; i++){
    a[i] = i + 1;
  }

  //check
  for(i = 0; i < N; i++){
    if(a[i] != i) return 2;
  }


#pragma acc data copyout(a)
#pragma acc parallel loop
  for(i = 0; i < N; i++){
    a[i] = i + 2;
  }

  //check
  for(i = 0; i < N; i++){
    if(a[i] != i + 2) return 3;
  }


#pragma acc data create(a)
#pragma acc parallel loop
  for(i = 0; i < N; i++){
    a[i] = i + 3;
  }

  //check
  for(i = 0; i < N; i++){
    if(a[i] != i + 2) return 4;
  }


  int ret;
  for(i = 0; i < N; i++) a[i] = 0;
  ret = test_func(a, 0);
  if(ret != 0){
    return 5;
  }


  for(i = 0; i < N; i++) a[i] = 0;
#pragma acc data copy(a)
  ret = test_func(a, 1);

  if(ret != 0){
    return 6;
  }


  for(i = 0; i < N; i++) a[i] = 0;
#pragma acc data copy(a)
#pragma acc host_data use_device(a)
  test_func2(a);
  //check
  for(i = 0; i < N; i++){
    if(a[i] != i * 3) return 7;
  }

  printf("PASS\n");
  return 0;
}

int test_func(int a[N], char correct_val)
{
  int i;
  char results[4] = {0};

#pragma acc data pcopy(a)
#pragma acc parallel loop
  for(i = 0; i < N; i++){
    a[i] = i;
  }

  //check
  for(i = 0; i < N; i++){
    if(a[i] != i) results[0] = 1;
  }


#pragma acc data pcopyin(a)
#pragma acc parallel loop
  for(i = 0; i < N; i++){
    a[i] = i + 1;
  }

  //check
  for(i = 0; i < N; i++){
    if(a[i] != i) results[1] = 1;
  }


#pragma acc data pcopyout(a)
#pragma acc parallel loop
  for(i = 0; i < N; i++){
    a[i] = i + 2;
  }

  //check
  for(i = 0; i < N; i++){
    if(a[i] != i + 2) results[2] = 1;
  }


#pragma acc data pcreate(a)
#pragma acc parallel loop
  for(i = 0; i < N; i++){
    a[i] = i + 3;
  }

  //check
  for(i = 0; i < N; i++){
    if(a[i] != i + 2) results[3] = 1;
  }

  
  for(i = 0; i < 4; i++){
    if(results[i] != correct_val) return 1;
  }

  return 0;

  /*
  printf("{");
  for(i = 0; i < 4; i++){
    printf("%d,", (int)results[i]);
  }
  printf("}\n");
  */
}

void test_func2(int dev_a[N])
{
  int i;
#pragma acc data deviceptr(dev_a)
#pragma acc parallel loop
  for(i = 0; i < N; i++){
    dev_a[i] = i * 3;
  }
}
