# sirens

`sirens` is my senior thesis at [Brandeis University](brandeis.edu). It was completed under the supervision of [Ryan Marcus](rmarcus.info) and [Olga Papaemmanouil](http://www.cs.brandeis.edu/~olga/). The project implements several task scheduling heuristics and measures their performance accross several synethetic workloads. For context, finding an optimal schedule for a set of tasks is an NP-hard problem. Therefore, at larger graph sizes, the problem is intractable. Heuritics make the problem feasible, albeit at the cost of optimality. `sirens` tries to quantify these tradeoffs.

## Etmyology

`sirens` is named after a [Nicholas Jaar album](https://en.wikipedia.org/wiki/Sirens_(Nicolas_Jaar_album)).

## Installation

First, ensure that [`ggen`](https://github.com/perarnau/ggen) is installed and is in your `PATH`. It is responsible for generating random graphs.

1. `git clone github.com/davidbarsky/sirens`
2. `mvn test`