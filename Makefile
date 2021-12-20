all:
	protoc --clojure_out=grpc-server:server/src --proto_path=config config/addressbook.proto
	protoc --clojure_out=grpc-client:client/src --proto_path=config config/addressbook.proto
