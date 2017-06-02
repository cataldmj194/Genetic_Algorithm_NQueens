/** 
   Name : Michael Cataldo
   Class: CIS 421 
   Assignment: NQueens Genetic Algorithm
   Due: 9/21/2016
*/
import java.util.*;
import java.io.*;

public class GANQueens{

  public static final int N = 12;                     //Size of Board
  public static final int popSize = N*10;
  public static final double Epsilon = 0.0001;  //Used to ensure no 0 division
  public static int generation = 1;                   //Generation count
  public static boolean solutionFound = false;
  public static Creature[] creaturePop = new Creature[popSize]; //population []
  public static Creature[] creatureParents = new Creature[N];   //parent []
  public static Creature[] creatureChildren = new Creature[N];  //children [] \
  public static PrintStream log;                     //Used to print to log file
  public static File f;                              //Used to access log file

  public static void main(String[] args) throws FileNotFoundException{
  
    initializePopulation();
    testFitness();
    while(generation < 1000){
      parentSelection();
      crossOver();
      mutate();
      survivorSelection();
      generation++;
      System.out.println("Generation: " + generation);
      testFitness();
      if(solutionFound == true){
        System.exit(0);
      }
    }
    log = new PrintStream(new FileOutputStream("prog2_log.txt",true));
    log.append("Generation: " + generation + "\n");
    log.append("\n");  
    
  }

  //Postcondition: Copies entire current population plus all of the children
  //               into a temporary array. After placing them into the tmp
  //               array, they are then sorted according to their fitness.
  //               Reads the tmp array of creatures back into the current
  //               population skipping the lowest N fitness.
  public static void survivorSelection(){
    Creature[] tmp = new Creature[popSize+N];

    for(int i =0; i < popSize; i++)
      tmp[i] = creaturePop[i];
    for(int i = 0; i < N; i++)
      tmp[popSize+i] = creatureChildren[i];
    Arrays.sort(tmp);
    for(int i = 0; i < popSize; i++)
      creaturePop[i] = tmp[i+N];
  }

  //Postcondition: For every child created, they have a 10% chance to undergo
  //               mutation. If a child is selected for mutation, they are
  //               passed to the swap method.
  public static void mutate(){
    for(int i = 0; i < N; i++){
      Random rand = new Random();
      int r = rand.nextInt(100);
      if(r < 10)
        swap(creatureChildren[i]);
    }  
  }

  //Parameter: child - a Creature object to undergo a gene swap mutation
  //Postcondition: Two random position inside the child's array of genes
  //               will be swapped positions. Avoids swapping a position
  //               with itself.
  public static void swap(Creature child){
    int[] genes = child.getGenes();
    Random rand = new Random(); 
    int r1 = rand.nextInt(N);
    int r2 = rand.nextInt(N);

    while(r2 == r1)
      r2 = rand.nextInt(N);

    int tmp = genes[r1];
    genes[r1] = genes[r2];
    genes[r2] = tmp;
    child.setGenes(genes);
    child.setFit(getFitness(genes));
  }

  //Postcondition: Randomly selects three Creatures in our population
  //               to undergo tournament selection. The Creature with the
  //               highest fitness gets to be in the mating pool.
  //               If a parent is selected, it does not get selected again this
  //               generation. Does this until number of parents is 10% 
  //               of the population. Array of parents is sorted upon completion             
  public static void parentSelection(){
    Creature[] rejects = new Creature[N];
    int currentMember = 0;

    while(currentMember < (popSize * .1)){
      Creature[] tourney = new Creature[3];
      for(int i = 0; i < 3; i++){
        Random rand = new Random();
        int r = rand.nextInt(popSize);
        double f = creaturePop[r].getFit();
        if(!(contains(rejects,creaturePop[r]))){
          tourney[i] = creaturePop[r];
          rejects[currentMember] = creaturePop[r];
        }else
          i--;
      }
      creatureParents[currentMember] = tourney[2];
      currentMember++;
    }
    Arrays.sort(creatureParents);
  }

  //Postcondition: Parents are paired off by fitness values and from
  //               their pairing, a child is generated. For each set
  //               of parents, a crossover point is randomly generated.
  //               Parents are there cut into two segments after the xover point
  //               First part of parent 1 becomes 1st part of child 1.
  //               First part of parent 2 becomds 1st part of child 2.
  //               Then scans parent 2 left to right, filling in the second
  //               segment of child 1 with values from parent 2 skipping dupes.
  //               Does the same for child 2. Array containing children is
  //               sorted by fitness at the end.
  public static void crossOver() {

    for(int i = 0; i < N; i+=2){

      int[] parentOne = new int[N];
      int[] parentTwo = new int[N];

      int[] childOne = new int[N];
      int[] childTwo = new int[N];

      Random rand = new Random();
      int cut = rand.nextInt(N);
      while((cut == 0)||(cut == 11))
        cut = rand.nextInt(N);     

      parentOne = creatureParents[i].getGenes();
      parentTwo = creatureParents[i+1].getGenes();

      for(int j = 0; j <= cut; j++)
        childOne[j] = parentOne[j];
      for(int j = 0; j <= cut; j++)
        childTwo[j] = parentTwo[j];
     
      int count = cut;
      for(int j = 0; (j < N)&&(count<N); j++){
        if(!(contains(Arrays.copyOfRange(childOne,0,count),parentTwo[j]))){
          childOne[count] = parentTwo[j];
          count++;
        }
      }

      count = cut;
      for(int j = 0; (j < N)&&(count<N); j++){
        if(!(contains(Arrays.copyOfRange(childTwo,0,count),parentOne[j]))){
          childTwo[count] = parentOne[j];
          count++;
        }
      }
    
      creatureChildren[i] = new Creature(childOne,getFitness(childOne));
      creatureChildren[i+1] = new Creature(childTwo,getFitness(childTwo));
    }
    Arrays.sort(creatureChildren);
  }

  //Parameters: rejects - an Array of Creature objects
  //            candidate - a Creature object
  //Postcondition: return true of the rejects array contains the candidate
  //               returns false otherwise
  //Returns: cont - a boolean value, true if rejects contains candidate.
  public static boolean contains(Creature[] rejects, Creature candidate){
    boolean cont = false;
    for(Creature c : rejects){
      if(c == candidate)
        return cont = true;
    }
    return cont;
  }

  //Parameters: child - an array of integers
  //            element - an integer
  //Postcondition: return true if the element is contained in the genes of child
  //               return false otherwise. Used during crossover
  //Returns: cont - a boolean value, true if child contains element
  public static boolean contains(int[] child,int element){
    boolean cont = false;
    for(int i = 0; i < child.length; i++){
      if(child[i] == element)
        return cont = true;
    }
    return cont;
  }

  //Postcondition: Tests the fitness for every creature in our population
  //               If the creatures fitness is >=1, a solution is found,
  //               sets solutionFound boolean flag to true and calls logging
  //               Passing the creature determined to be the solution.
  public static void testFitness(){
    int count = 1;
    for(Creature c : creaturePop){
      Double f = c.getFit();
      if(f >= 1){
        solutionFound = true;
        logging(c);
      }
      count++;
    }
  }

  //Parameters: c - a Creature Object determined to be a solution
  //Postcondition: Prints the creature's solution to console along with
  //               the generation count. Then appends the generation + solution
  //               to the logging file prog2_log.txt. Catches FileNotFound and
  //               IOExceptions.
  public static void logging(Creature c){
    System.out.println("Solution Found!");
    System.out.println("Generation: " + generation);
    System.out.print("Solution: ");
    c.print();
    try{
      f = new File("prog2_log.txt");
      if(!f.exists())
        f.createNewFile();
      log = new PrintStream(new FileOutputStream(f,true));
      log.append("Generation: " + generation + "\n");
      log.append("Solution: " + Arrays.toString(c.getGenes()) + "\n");  
    }catch(FileNotFoundException e){
      System.out.println("File Not Found?");
      System.exit(0);
    }catch(IOException e){
      System.out.println("IOException");
      System.exit(0);
    }
  }

  //Parameters: indiv - an array of integers (an individual's genes)
  //Postcondition: calculates the fitness value using the slope formula for
  //               the passed creature's genes.
  //Returns: a double containing the creatures calculated fitness value
  public static double getFitness(int[] indiv){
    //1 / (q(p) + Epsilon) : q(p) = number of attacking queen pairs
    //(X2 - X1) / (Y2 - Y1)
    int aqp = 0;
    for(int i = 0; i < N; i++){
      for(int j = i+1; j < N; j++){
        double collision = ((double)indiv[j]-(double)indiv[i])/((double)j-(double)i);
        if((collision == 1.0) || (collision == -1.0))
          aqp++;
      }
    }
    return (1.0/((double)aqp + Epsilon));
  }

  //Postcondition: Initializes our population with randomly generated creatures
  public static void initializePopulation(){    
    int [] initPop = new int[N];
 
    for(int i = 0; i < N; i++)
      initPop[i] = i;

    for(int i = 0; i < N*10; i++) {
      initPop = shuffle(initPop);
      System.out.println("Testing fitness in init");
      System.out.println(Arrays.toString(initPop) + " = " + getFitness(initPop));
      creaturePop[i] = new Creature(initPop,getFitness(initPop));
    }
  }

  //Parameters: initpop - an int array to be shuffled to generate
  //            a random individual.
  //Postcondition: Uses a fisher-yates shuffle to randomly shuffle
  //               elements of the passed array. Generated array becomes
  //               genes of a creature in our population.
  public static int[] shuffle(int[] initPop){

    Random rand = new Random();
    for(int i = N-1; i > 0; i--){
      int randNum = rand.nextInt(i+1);
      int swap = initPop[randNum];
      initPop[randNum] = initPop[i];
      initPop[i] = swap;
    }
    return initPop;
  }

  //Postcondition: Prints entire population to console
  public static void printPop(){
    for(int i = 0; i < N*10; i++)
      creaturePop[i].print();
  }
}

