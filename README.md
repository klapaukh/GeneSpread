#Spatial Gene Simulator

A basic biological simulator. It simulates dots that move randomly and have children.
It has a naive representation of DNA, food and disease.
Colors are used to show the presence of appearance controlling genes.

The primary purpose is to show how populations that are separated by distance separate into distinct groups.
It additionally shows how groups tend to form around food sources, and how disease is limited by the population density.

##Dependencies

 * Java 1.7
 * [Apache Commons Math 3.2](http://commons.apache.org/proper/commons-math/)

##Building

In order to build GeneSpread you must first download Apache Commons Math 3.2 and place it in `lib/commons-math3-3.2.jar`, and then run `ant` to generate `GeneSpread.jar`.

##Running

GeneSpread can be run without any arguments using `ant run`, and can be run outside of ant using the following invocation:

```bash
java -cp "GeneSpread.jar:lib/commons-math3-3.2.jar" World
```

Or for Windows (note the change from `:` to `;`):

```bash
java -cp "GeneSpread.jar;lib/commons-math3-3.2.jar" World
```

GeneSpread takes the following optional arguments:

* `-w [number]` The width of the world. The default is 300.
* `-h [number]` The height of the world. The default is 300.
* `-f [number]` The number of food sources in the world. The default is based on the size of the world.
* `-n [number]` The number of walls in the world. The default is based on the size of the world.
* `-s [string]` The seed for the simulation. The default is based on the current time.

##Visual Key

 * **Green** - an organism with two different color alleles.
 * **Blue** - an organism with two of the same color alleles.
 * **Purple** - an organism with two of the same color alleles.
 * **White** - an organism which is sick, contagious, and will die.
 * **Yellow** - an organism which is sick, contagious, and will not die (immune).
 * **Black** - an organism which has recently eaten another organism.
 * **Grey** - a wall which organisms cannot pass through.
 * **Red** - a food source from which organisms can eat.
 * **Pink** - empty ground.
 * Darker colored organisms are male.
 * Lighter colored organisms are female.
