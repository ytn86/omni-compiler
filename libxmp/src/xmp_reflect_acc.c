#include <stdio.h>
#include <stdlib.h>
#include "xmp_internal.h"
extern void _XMP_reflect_init_gpu(void *acc_addr, _XMP_array_t *array_desc);
extern void _XMP_reflect_do_gpu(_XMP_array_t *array_desc);

void _XMP_reflect_init_acc(void *acc_addr, _XMP_array_t *array_desc)
{
  if(_XMP_world_size == 1) return;

#ifdef _XMP_TCA
  char *hybrid_str = getenv("XACC_ENABLE_HYBRID");
  if (hybrid_str == NULL || atoi(hybrid_str) != 1)
    _XMP_reflect_init_tca(acc_addr, array_desc);
  else
    _XMP_reflect_init_hybrid(acc_addr, array_desc);
#else
  _XMP_reflect_init_gpu(acc_addr, array_desc);
#endif
}

void _XMP_reflect_do_acc(_XMP_array_t *array_desc)
{
  if(_XMP_world_size == 1) return;

#ifdef _XMP_TCA
  char *hybrid_str = getenv("XACC_ENABLE_HYBRID");
  if (hybrid_str == NULL || atoi(hybrid_str) != 1)
    _XMP_reflect_do_tca(array_desc);
  else
    _XMP_reflect_do_hybrid(array_desc);
#else
  _XMP_reflect_do_gpu(array_desc);
#endif
}

void _XMP_reflect_acc(void *addr)
{
  if(_XMP_world_size == 1) return;

#ifdef _XMP_TCA
#endif
}
