all: bin/World.class

bin/World.class: src/*.java
	javac -d bin src/*.java

.PHONY: clean
clean:
	rm -f bin/*
