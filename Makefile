all: bin/World.class

bin/World.class: bin src/*.java
	javac -d bin src/*.java

bin:
	mkdir -p bin

.PHONY: clean
clean:
	rm -rf bin
