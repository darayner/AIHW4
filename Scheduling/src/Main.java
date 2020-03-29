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
    
    nBuildings = 5;
    nRooms = 100;
    nCourses = 500;
    TIME_LIMIT_SECONDS = 10;
    algorithm = 1;
    seed = 0;
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
    }
    else if (algorithm == 1){
      solution = search.simulatedAnnealing(test1, deadline);
    }
  	else {
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
    
    long startTime = System.nanoTime();
    solution1 = search.naiveBaseline(test1, deadline);
    long endTime = System.nanoTime();
    long timeElapsed = endTime - startTime;
    System.out.println("Time: " + timeElapsed);
    
    long startTime1 = System.nanoTime();
    solution2 = search.backTrackCSP(test1, deadline);
    long endTime1 = System.nanoTime();
    long timeElapsed1 = endTime1 - startTime1;
    System.out.println("Time: " + timeElapsed1);
    
    
    double score1 = test1.evaluateSchedule(solution1);
    double score2 = test1.evaluateSchedule(solution2);
    System.out.println();
    System.out.println("Score: " + score1);
    System.out.println("Score: " + score2);
    System.out.println();
  }  
}