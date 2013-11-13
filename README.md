#Spacial Gene Simulator


A basic biological simulator. It simulates dots that move randomly and have children.
It has a naive representation of DNA, food and disease.
Colors are used to show the presence of appearance controlling genes.

The primary purpose is to show how populations that are separated by distance separate into distinct groups.
It additionally shows how groups tend to form around food sources, and how disease is limited by the population density.

###Dependencies

 * Java 1.7
 * Apache commons-math 3.2

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
