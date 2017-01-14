## Computation of t-level:

1. Construct a list of nodes in topological order. Call it TopList.

```
for each node ni in TopList do
  max = 0
  for each parent nx of ni do
    if t-level(nx) +w(nx) +c(nx, ni) > max then
      max= t-level(nx) + w(nx) + c(nx,ni)
    endif
  endfor
  t-level(ni ) = max
endfor
```
