# Environment Config

**from: 0.4.0-RELEASE**

The environment of dependent storages like etcd and ES can be configured, which is more convenient for environment switch and debug at local.

- logger
	+ `tumbler.logger = std`: std logging, for local test
	+ `tumbler.logger = cal`: cal logging, for app deployment
- etcd
	+ `tumbler.etcd = mem`: in-memory impl of etcd, for local test
	+ `tumbler.etcd = magellan`: real etcd deployment
- es
	+ `tumbler.es = disk`: in-disk impl of es, for local test
	+ `tumbler.es = magellan`: real es deployment
- metrics
	+ `tumbler.metrics.server.enable = true`: prometheus metrics server enabled or not
	+ `tumbler.metrics.server.port = 9091`: prometheus metrics server port, e.g. metrics can be accessed via `http://localhost:9091/metrics`