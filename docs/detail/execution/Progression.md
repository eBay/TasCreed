# Progression

TasCreed can calculate the progress of a job or step in percentage format, then users can easier to know where the execution achieves.

## Job progression

Job progression is determined by steps.

Each step has an optional parameter `effort`, by default is `1`, as the weight during calculation.

```
Job progression = sum(success step effort) / sum(all step effort) * 100%
```

## Step progression

Step progression is determined by tasks, but differentiated by step modes.

- Simple: success `100%`, other `0%`
- Shard: `count(success shard) / count(total shard) * 100%`
- Pack: `count(success pack) / count(total pack) * 100%`
    + Infinite Pack: `0%`, because the step never ends
