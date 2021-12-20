# Demo Bi Direcional gRPC streams

This is a modified version from the output of a `lein new protojure` template run. The output is a set of sample
files demonstrating the use of the protojure lib + protoc plugin to expose a GRPC server/client with bi-direcional stream.

The server and the client runs in a control loop sending messages to each other without any order.

- See [protujure-template](https://github.com/protojure/lein-template) for more details.
- See [protojure quick-start](https://protojure.readthedocs.io/en/latest/quick-start/) for more details.

## Prerequisites

The [Protocol Buffer 'protoc' compiler](https://github.com/protocolbuffers/protobuf/releases)
and [Protojure protoc-plugin](https://github.com/protojure/protoc-plugin/releases) must be installed.

## Getting Started

Start the gRPC server on port 8080

1. `make all`
2. `cd server && lein run-server`

Start the gRPC client

1. `cd client && lein run-client`

## License

This project is licensed under the Apache License 2.0.
