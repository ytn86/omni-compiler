#define NMAX 4
#include <stdio.h>
#include <stdlib.h>

extern int chk_int(int ierr);

int n=NMAX;
int a[n][n],b[n][n];
#pragma xmp nodes p(8)
#pragma xmp nodes p1(2,2)=p(1:4)
#pragma xmp nodes p2(2,2)=p(5:8)
#pragma xmp template tx(0:n-1,0:n-1,0:n-1)
#pragma xmp template ty(0:n-1,0:n-1,0:n-1)
#pragma xmp distribute tx(block,*,block) onto p1
#pragma xmp distribute ty(*,block,block) onto p2
#pragma xmp align a[i0][*] with tx(i0,*,*)
#pragma xmp align b[*][i2] with ty(*,*,i2)

int main(){

  int i0,i1,i2,ierr;

#pragma xmp loop (i0) on tx(i0,*,*)
  for(i0=0;i0<n;i0++){
    for(i2=0;i2<n;i2++){
      a[i0][i2]=i0+i2+1;
    }
  }

  for(i1=0;i1<n;i1++){
#pragma xmp loop (i2) on ty(*,*,i2)
    for(i2=0;i2<n;i2++){
      b[i1][i2]=0;
    }
  }

#pragma xmp gmove
  b[:][:]=a[:][:];

  ierr=0;
  for(i1=0;i1<n;i1++){
#pragma xmp loop (i2) on ty(*,*,i2)
    for(i2=0;i2<n;i2++){
      ierr=ierr+abs(b[i1][i2]-i1-i2-1);
    }
  }

#pragma xmp reduction (MAX:ierr)
  chk_int(ierr);
}