#Spatial Gene Simulator

A basic biological simulator. It simulates dots that move randomly and have children.
It has a naive representation of DNA, food and disease.
Colors are used to show the presence of appearance controlling genes.

The primary purpose is to show how populations that are separated by distance separate into distinct groups.
It additionally shows how groups tend to form around food sources, and how disease is limited by the population density.

###Dependencies

 * Java 1.7
 * [Apache Commons Math 3.2](http://commons.apache.org/proper/commons-math/)

###Building

In order to build GeneSpread you must first download Apache Commons Math 3.2 and place it in `lib/commons-math3-3.2.jar`.
Then run `ant` to generate `GeneSpread.jar`. To run GeneSpread without any arguments you can use `ant run`.

To run GeneSpread outside of ant, use the following invocation:

```bash
java -cp "GeneSpread.jar;lib/commons-math3-3.2.jar" World
```

###Visual Key

 * **Green** a dot with two different color alleles
 * **Blue** a dot with two of the same color alleles
 * **Purple** a dot with two of the same color alleles
 * **White** a dot which is sick, contagious and will die
 * **Yellow** a dot which is sick, contagious and will not die (immunity)
 * **Black** a doc which has recently eaten another dot
 * **Red** Food
 * **Pink** Bare ground
 * Darker dots are male
 * Lighter dots are female
