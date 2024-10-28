# Param

In TasCreed, params can be passed to task executors at runtime.

## Data Type

Params are configured as a map of key-value pairs, in string format. For example:

``` json
"params": {
  "k1": "v1",
  "k2": "v2"
}
```

## Overwrite rule

Params are

- configured in `JobDefine`, `StepDefine`
- overwritten in `JobRequest`
- inherited in `Job`, `Step`, `Task`

Only the params in `Task` are actually used at runtime.

### Job param

```
Job params = JobDefine params + JobRequest params
```

`Job` params are union of `JobDefine` params and `JobRequest` params, the latter overwrites the former.

### Step param

```
Step params = StepDefine params + StepRequest params
```

`Step` params are union of `StepDefine` params and `StepRequest` params, the latter overwrites the former. 

*`StepRequest` is the step part in `JobRequest`.*

### Task Param Generation

```
Task params = Job params + Step params + Job updated params
```

`Task` params are union of `Job` params, `Step` params and `Job` [updated params](../detail/execution/UpdateParamConfig.md), the latter overwrites the former.
