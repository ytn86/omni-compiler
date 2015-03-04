program main
!$xmp nodes p(2,2)
!$xmp template t(4,4)
!$xmp distribute t(block,block) onto p 
integer(8):: a(4,4),x(4,4),idx1(4,4),idx2(4,4)
!$xmp align a(i,j) with t(i,j)
!$xmp align x(i,j) with t(i,j)
!$xmp align idx1(i,j) with t(i,j)
!$xmp align idx2(i,j) with t(i,j)
integer:: i,j,answer = 0

!$xmp loop (i,j) on t(i,j)
do i=1,4
 do j=1,4
a(i,j)=i*4+j
 end do
end do

!$xmp loop (i,j) on t(i,j)
do i=1,4
 do j=1,4
x(i,j)=0
 end do
end do

!$xmp loop (i,j) on t(i,j)
do i=1,4
 do j=1,4
idx1(i,j)=i
idx2(i,j)=j
 end do
end do

!print *,"initialized",xmp_node_num()," a=",a
!call xmp_barrier()
!call xmp_barrier()
!print *,"initialized",xmp_node_num()," x=",x
!call xmp_barrier()
!call xmp_barrier()
!print *,"initialized",xmp_node_num()," idx1=",idx1
!call xmp_barrier()
!call xmp_barrier()
!print *,"initialized",xmp_node_num()," idx1=",idx1
!call xmp_barrier()
call xmp_barrier()

call xmp_gather(xmp_desc_of(x),xmp_desc_of(a),xmp_desc_of(idx1),xmp_desc_of(idx2))
 
!print *,"after",xmp_node_num()," a=",a
!call xmp_barrier()
!call xmp_barrier()
!print *,"after",xmp_node_num()," x=",x
!call xmp_barrier()
!call xmp_barrier()
!print *,"after",xmp_node_num()," idx1=",idx1
!call xmp_barrier()
!call xmp_barrier()
!print *,"after",xmp_node_num()," idx2=",idx2
!call xmp_barrier()
call xmp_barrier()
!$xmp loop (i,j) on t(i,j)
do i=1,4
 do j=1,4
  if(x(i,j).ne.a(i,j)) then
    answer = -1
  endif
 end do
end do

!$xmp reduction(+:answer)

!$xmp task on p(1,1)
  if ( answer /= 0 ) then
     write(*,*) "ERROR"
     call  abort
  endif

  write(*,*) "PASS"
!$xmp end task


end program main