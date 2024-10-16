# Phase

The step dependency describes the relationship between steps, and the results of previous step will affect the execution of the subsequent ones.

For example, if a step finishes with a `FAILED` state, the following steps will be simply skipped with a `SKIP_BY_FAILED` state. 
But sometimes, users might need a step that must be executed at last, just like the `finally` block does in Java application.

The feature of phase definition is introduced to solve the problem, it can do even more than a `finally` step.

## Phase definition

Each step can define its belonged phase, which is described as an integer number, by default `0`.

The rule is simple, a phase can start, only if all its previous phases are done. The smaller phase number the higher priority.

The step dependency describes a strong relationship, while the phase describes a weak dependency relationship. 

- The step dependency restricts the step to check the results of its dependent steps are success or not;
- The phase dependency only restricts the step to check its dependent phase steps are done or not.

Therefore, the results of steps in the dependent phase will not impact the execution of the steps in the next phase. 

For example, the `finally` step can be set with a larger phase number than other steps.

## Example

Let's define steps like this:

``` mermaid
flowchart TD
    subgraph P0["Phase 0"]
        A --> B
        A --> C
        B --> D
        C --> D
        C --> E
    end
    subgraph P1["Phase 1"]
        F --> H
        F --> I
        G --> I
    end
    E --> G
    P0 -.-> P1
```

The job define of the DAG as illustrated could be like this:

``` json
{
  "jobName": "dag",
  "steps": [
    {
      "stepName": "A"
    },
    {
      "stepName": "B",
      "dependency": {
        "steps": ["A"]
      }
    },
    {
      "stepName": "C",
      "dependency": {
        "steps": ["A"]
      }
    },
    {
      "stepName": "D",
      "dependency": {
        "steps": ["B", "C"]
      }
    },
    {
      "stepName": "E",
      "dependency": {
        "steps": ["C"]
      }
    },
    {
      "stepName": "F",
      "dependency": {
        "phase": 1
      }
    },
    {
      "stepName": "G",
      "dependency": {
        "phase": 1,
        "steps": ["E"]
      }
    },
    {
      "stepName": "H",
      "dependency": {
        "phase": 1,
        "steps": ["F"]
      }
    },
    {
      "stepName": "I",
      "dependency": {
        "phase": 1,
        "steps": ["F", "G"]
      }
    }
  ]
}
```

- phase 0 is the first phase, phase 1 depends on phase 0.
- if `phase` not set, it is in phase 0 by default.
- the explicit dependency can across different phases, like step `G` in phase 1 explicitly depends on step `E` in phase 0. Step `G` starts after `E` is done, and the result state of `E` will impact `G` as well. That is the main difference between the step dependency and phase dependency.
- the steps in phase 1 will start only after all the steps in phase 0 are done.
- the phase number is unnecessary to be continuous, but it should not be negative number. For example, in a job define, we can only have phase 3 and 5, without configuring phase 0, 1, 2, 4. The execution order is from small phase to large one, that's all.

???+ question "What if the phase dependency conflicts with the step dependency?"
    For example, a step in smaller phase depends on a step in larger phase, this is very tricky.  
    TasCreed only validates the cyclic dependency of the step dependencies, not the phase orders.  
    In case of any unexpected blocking issue, users need to make sure the phase order doesn't introduce cyclic dependency.

## States of dependent steps

When the tasks of a step is created, the state of the dependent steps are passed to them.

- `dependentStepStates`: the state of dependent steps
- `prevPhaseStepStates`: the state of steps in previous phase

With the states of dependent steps, a task executor can implement the conditional branch logic according to the dependent steps are success or failed, it brings more flexibility.