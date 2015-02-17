int g_sum;

void reduce(int a[100])
{
  int i;
#pragma acc parallel loop reduction(+:g_sum) present(a, g_sum)
  for(i =0;i<100;i++){
    g_sum += a[i];
  }
}

int main()
{
  int array[100];
  int i,j;
  
  for(i=0;i<100;i++){
    array[i] = i;
  }

  int sum = 0;

#pragma acc parallel loop reduction(+:sum)
  for(i=0;i<100;i++){
    sum += array[i];
  }
  //verify
  if(sum != 4950) return 1;


  sum = 0;
#pragma acc parallel loop reduction(+:sum) copy(sum)
  for(i=0;i<100;i++){
    sum += array[i];
  }
  //verify
  if(sum != 4950) return 2;


  sum = 0;
#pragma acc data copy(sum)
  {
#pragma acc parallel loop reduction(+:sum)
    for(i=0;i<50;i++){
      sum += array[i];
    }
  }
  //verify
  if(sum != 1225) return 3;


  sum = 100;
#pragma acc data copy(sum)
  {
#pragma acc parallel loop reduction(+:sum)
    for(i=50;i<100;i++){
      sum += array[i];
    }
  }
  //vefify
  if(sum != 3825) return 4;


  sum = 50;
#pragma acc parallel loop reduction(+:sum) vector_length(32)
  for(i=0;i<100;i++) sum += array[i];
  //vefify
  if(sum != 5000) return 5;

  sum = 50;
#pragma acc parallel loop reduction(+:sum) vector_length(512)
  for(i=0;i<100;i++) sum += array[i];
  //vefify
  if(sum != 5000) return 6;
  
  g_sum = 0;
#pragma acc data copy(array,g_sum)
  {
    reduce(array);
  }
  if(g_sum != 4950) return 7;

  return 0;
}
