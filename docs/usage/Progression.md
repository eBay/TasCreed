# Progression

## Job, Step Progression

**from: 0.2.4-SNAPSHOT**

In job and step level, Tumbler can show the progression in percentage format, in this way, users can easier to know their job execution progress.

Users can set an optional parameter `effort` in the steps, by default is `1`.

Job progression is calculated as `sum(success step effort) / sum(all step effort) * 100%`.

Step progression is calculated in different step modes:
- Simple: success `100%`, other `0%`
- Shard: `success shard num / total shard num * 100%`
- Pack: `success pack num / total pack num * 100%`
- Infinite Pack: `0%`

