import java.util.Arrays;

public class Main {

  public static void main(String[] args) {

    int nBuildings = 0;
    int nRooms = 0;
    int nCourses = 0;
    int TIME_LIMIT_SECONDS = 0;
    int algorithm = 1;
    long seed = 0;
/*
    if (args.length == 6) {
      try {
        nBuildings = Integer.parseInt(args[0]);
        nRooms = Integer.parseInt(args[1]);
        nCourses = Integer.parseInt(args[2]);
        TIME_LIMIT_SECONDS = Integer.parseInt(args[3]);
        algorithm = Integer.parseInt(args[4]);
        seed = Long.parseLong(args[5]);
      } catch (NumberFormatException e) {
        System.out.println("Number format exception reading arguments");
        System.exit(1);
      }
    } else {
      System.out.println("ERROR: Incorrect number of arguments (should have six).");
      System.exit(1);
    }
*/
    
    nBuildings = 10;
    nRooms = 100;
    nCourses = 300;
    TIME_LIMIT_SECONDS = 10;
    algorithm = 1;
    seed = 300;
    System.out.println("Number of Buildings: " + nBuildings);
    System.out.println("Number of Rooms: " + nRooms);
    System.out.println("Number of Courses: " + nCourses);
    System.out.println("Time limit (s): " + TIME_LIMIT_SECONDS);
    System.out.println("Algorithm number: " + algorithm);
    System.out.println("Random seed: " + seed);


    SchedulingProblem test1 = new SchedulingProblem(seed);
    test1.createRandomInstance(nBuildings, nRooms, nCourses);

    SearchAlgorithm search = new SearchAlgorithm();

    long deadline = System.currentTimeMillis() + (1000 * TIME_LIMIT_SECONDS);
    /*
    // Add your search algorithms here, each with a unique number
    Schedule solution = null;
    if (algorithm == 0) {
      solution = search.naiveBaseline(test1, deadline);
    }else if (algorithm == 1){
      solution = search.simulatedAnnealing(test1, deadline);
    }else if(algorithm == 2){
    	solution = search.backTrackCSP(test1, deadline, 0); //regular backtracking
    }else if(algorithm == 3){
    	solution = search.backTrackCSP(test1, deadline, 1); //backtracking with value optimization
    }else if(algorithm == 4){
    	solution = search.backTrackCSP(test1, deadline, 2); //backtracking with value and distance optimization
    }else {
      System.out.println("ERROR: Given algorithm number does not exist!");
      System.exit(1);
    }
    for (Course i : test1.courses) {
        System.out.println(Arrays.toString(i.timeSlotValues));
    }
    */
    System.out.println();
    
    Schedule solution1 = null;
    Schedule solution2 = null;
    Schedule solution3 = null;
    Schedule solution4 = null;
    Schedule solution5 = null;
    Schedule solution6 = null;
  
    
    long startTime = System.nanoTime();
    solution1 = search.naiveBaseline(test1, deadline);
    long endTime = System.nanoTime();
    long timeElapsed = endTime - startTime;
    System.out.println("BaseLine Time: " + timeElapsed);
    
    long startTime1 = System.nanoTime();
    solution2 = search.simulatedAnnealing(test1, deadline, 0);//regular simulatedAnnealing
    long endTime1 = System.nanoTime();
    long timeElapsed1 = endTime1 - startTime1;
    System.out.println("SimulatedAnn Time: " + timeElapsed1);
    
    long startTime2 = System.nanoTime();
    solution3 = search.simulatedAnnealing(test1, deadline, 1);//improved simulatedAnnealing
    long endTime2 = System.nanoTime();
    long timeElapsed2 = endTime2 - startTime2;
    System.out.println("Improved SimulatedAnn Time: " + timeElapsed2);
    
    long startTime3 = System.nanoTime();
    solution4 = search.backTrackCSP(test1, deadline, 0);//regular backtracking
    long endTime3 = System.nanoTime();
    long timeElapsed3 = endTime3 - startTime3;
    System.out.println("RegBacktrack Time: " + timeElapsed3);
    
    long startTime4 = System.nanoTime();
    solution5 = search.backTrackCSP(test1, deadline, 1);//backtracking with value optimization
    long endTime4 = System.nanoTime();
    long timeElapsed4 = endTime4 - startTime4;
    System.out.println("BacktrackVal Time: " + timeElapsed4);
    
    long startTime5 = System.nanoTime();
    solution6 = search.backTrackCSP(test1, deadline, 2);//backtracking with value and distance optimization
    long endTime5 = System.nanoTime();
    long timeElapsed5 = endTime5 - startTime5;
    System.out.println("BacktrackDist Time: " + timeElapsed4);
    
    
    double score1 = test1.evaluateSchedule(solution1);
    double score2 = test1.evaluateSchedule(solution2);
    double score3 = test1.evaluateSchedule(solution3);
    double score4 = test1.evaluateSchedule(solution4);
    double score5 = test1.evaluateSchedule(solution5);
    double score6 = test1.evaluateSchedule(solution6);
 
    
    System.out.println();
    System.out.println("BaseLine Score: " + score1);
    System.out.println("SimulatedAnn Score: " + score2);
    System.out.println("Imporved SimulatedAnn Score: " + score3);
    System.out.println("RegBacktrack Score: " + score4);
    System.out.println("BacktrackVal Score: " + score5);
    System.out.println("BacktrackDist Score: " + score6);
    System.out.println();
  }  
}