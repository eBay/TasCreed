# Environment Config

**from: 0.4.0-RELEASE**

The environment of dependent storages like etcd and ES can be configured, which is more convenient for environment switch and debug at local.

- logger
	+ `tascreed.logger = std`: std logging, for local test
	+ `tascreed.logger = cal`: cal logging, for app deployment
- etcd
	+ `tascreed.etcd = mem`: in-memory impl of etcd, for local test
	+ `tascreed.etcd = magellan`: real etcd deployment
- es
	+ `tascreed.es = disk`: in-disk impl of es, for local test
	+ `tascreed.es = magellan`: real es deployment
- metrics
	+ `tascreed.metrics.server.enable = true`: prometheus metrics server enabled or not
	+ `tascreed.metrics.server.port = 9091`: prometheus metrics server port, e.g. metrics can be accessed via `http://localhost:9091/metrics`