module data
!! include 'xmp_coarray.h'
parameter(lx=100,ly=50)
real*8, allocatable :: a(:,:)[:]
real*8 :: b(1,2)[*]
end module

use data
allocate ( a(lx,ly)[*] )

end
