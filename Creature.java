/** 
   Name : Michael Cataldo
   Class: CIS 421 
   Assignment: Genetic Algorithm - NQueens
   Due: Oct/
*/
import java.util.*;

public class Creature implements Comparable<Creature> {

  private int[] genes = new int[12]; //Genes for our creature
  private double fitness;            //Creatures fitness value

  //Creature constructor. A creature is an object containing both
  //the array containing the queen configuration on the board (genes)
  //and the calculated fitness value for the queen configuration.
  public Creature(int[] genes, double fitness){
    this.fitness = fitness;
    for(int i = 0; i < genes.length; i++)
      this.genes[i] = genes[i];

  }
 
  //Empty creature constructor
  public Creature(){
  
  }

  //Creature constructor
  //Parameter: genes - an int array
  //Postcondition: Creates creature and sets the creatures genes to 
  //               the passed in int array
  public Creature(int[] genes){
    this.genes = genes;
  }

  //Creature constructor
  //Parameter: fitness - a double
  //Postcondition: Creates a creature and sets it's fitness to the passed in val
  public Creature(double fitness){
    this.fitness = fitness;
  }

  //Returns: This creature's calculated fitness value
  public double getFit(){
    return fitness;
  }

  //Returns: This creature's genes
  public int[] getGenes(){
    return genes;
  }

  //Parameter: genes - an int array
  //Postconditon: sets this Creature's genes to the int[] that was passed in
  public void setGenes(int[] genes){
    this.genes = genes;
  }

  //Parameter: fitness - a double
  //Postcondition: sets this creature's fitness to the value passed in
  public void setFit(double fitness){
    this.fitness = fitness;
  }

  //Parameter: other - a Creature object to compare to the current creature
  //Postcondition: implemented comparable for easy sorting of creature arrays
  //               compares creature's fitness to the other's fitness
  //Returns: the int value comparison of the other creature to this creature
  public int compareTo(Creature other){
    return Double.compare(this.fitness, other.fitness);
  }

  //Postcondition: Prints this creature's genes and fitness value to console.
  public void print(){
    System.out.println(Arrays.toString(genes) + " = " + fitness);
  }

}

