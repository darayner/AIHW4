import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SearchAlgorithm {

  // Your search algorithm should return a solution in the form of a valid
  // schedule before the deadline given (deadline is given by system time in ms)
  public Schedule backtracking(SchedulingProblem problem, long deadline) {

    // get an empty solution to start from
    Schedule solution = problem.getEmptySchedule();

    while(true) {
    	if(Double.NEGATIVE_INFINITY < problem.evaluateSchedule(solution)) {
    		return solution;
    	}
    	
    }
    
  }
  
  public Schedule backTrackCSP(SchedulingProblem problem, long deadline, int type){ //type 0 is normal backtrack, 1 adds value optimization, 2 adds distance optimization
	 Schedule solution = problem.getEmptySchedule();
	 boolean csp = false;
	 if (type == 0)
		 csp = backtrackReg(solution, problem, deadline, 0);
	 else if (type == 1)
		 csp = backtrackValue(solution, problem, deadline, 0);
	 else if (type == 2)
		 csp = backtrackDist(solution, problem, deadline, 0);
	 if (csp) {
		 return solution;
	 }
	 
	 return null;
  }
  
	public boolean backtrackReg(Schedule solution, SchedulingProblem problem, long deadline, int currCourse) {

		if (currCourse >= problem.courses.size()) { // chose unassigned course
			return true;
		}

		for (int i = 0; i < solution.schedule.length; i++) {
			for (int j = 0; j < solution.schedule[i].length; j++) {
				Course course = problem.courses.get(currCourse);
				if (course.timeSlotValues[j] > 0 && solution.schedule[i][j] == -1) { // constraints
					solution.schedule[i][j] = currCourse;
					boolean nextAssign = backtrackReg(solution, problem, deadline, currCourse + 1); // assign next course to room[timeslot]
					if (nextAssign) { // assignment is good to move on to next
						return true;
					}
					solution.schedule[i][j] = -1; // unassign room[timeslot]
				}
			}
		}

		return false;
	}
  
	public boolean backtrackValue(Schedule solution, SchedulingProblem problem, long deadline, int courseIndex) {
		if (courseIndex >= problem.courses.size())	// Course traversal completed
			return true;

		int assignedR = 0;
		int assignedS = 0;
		Course course = problem.courses.get(courseIndex);
		
		for (int r = 0; r < solution.schedule.length; r++) {

			List<IndexedEntry<Integer>> valueIndex = findOptimalSlot(solution, course, r); // get sorted list of best value indexes

			for (int s = 0; s < valueIndex.size(); s++) {
				if (course.timeSlotValues[valueIndex.get(s).getIndex()] > 0 && solution.schedule[r][valueIndex.get(s).getIndex()] == -1) { // if the timeslot is feasible and available, assign to course
					
					assignedS = valueIndex.get(s).getIndex();
					assignedR = r;
					solution.schedule[assignedR][assignedS] = courseIndex; // Assign course to timeslot

					boolean correctPath = backtrackValue(solution, problem, deadline, courseIndex + 1); // checks if correct path

					if (correctPath) { //ready to continue
						return true;
					}
					solution.schedule[assignedR][assignedS] = -1; // undo room assignment
				}
			}
		}
		return false;
	}
	
	public boolean backtrackDist(Schedule solution, SchedulingProblem problem, long deadline, int courseIndex) {
		if (courseIndex >= problem.courses.size())	 // Course traversal completed
			return true;

		int assignedR = 0;
		int assignedS = 0;
		Course course = problem.courses.get(courseIndex);
		
		List<IndexedEntry<Double>> distIndex = findOptimalRoom(problem, course);//get sorted list of best room indexes
		for (int r = 0; r < distIndex.size(); r++) {

			List<IndexedEntry<Integer>> valueIndex = findOptimalSlot(solution, course, distIndex.get(r).getIndex()); // get sorted list of best value indexes

			for (int s = 0; s < valueIndex.size(); s++) {
				if (course.timeSlotValues[valueIndex.get(s).getIndex()] > 0 && solution.schedule[distIndex.get(r).getIndex()][valueIndex.get(s).getIndex()] == -1) { // if the timeslot is feasible and available, assign to course
					
					assignedS = valueIndex.get(s).getIndex();
					assignedR = distIndex.get(r).getIndex();
					solution.schedule[assignedR][assignedS] = courseIndex; // Assign course to timeslot

					boolean correctPath = backtrackDist(solution, problem, deadline, courseIndex + 1); // checks if correct path

					if (correctPath) { //ready to continue
						return true;
					}
					solution.schedule[assignedR][assignedS] = -1; // undo room assignment
				}
			}
		}
		return false;
	}
  
  //returns ordered list of most optimal slotIndexes
  public List<IndexedEntry<Integer>> findOptimalSlot(Schedule solution, Course course, int roomIndex) {
	  List<Integer> unordered = new ArrayList<Integer>();
	    for (int s = 0; s < solution.schedule[roomIndex].length; s++) //Get all values
	    	unordered.add(course.timeSlotValues[s]);
	  //get sorted array of the index values
	    List<IndexedEntry<Integer>> ordered = new ArrayList<>();
	    for (int i = 0; i < unordered.size(); i++) {
	        IndexedEntry<Integer> entry = new IndexedEntry<>(i, unordered.get(i));
	        ordered.add(entry);
	    }
	    Collections.sort(ordered,Collections.reverseOrder());
	    return ordered;
  }
  
  //returns ordered list of most optimal roomIndexes
  public List<IndexedEntry<Double>> findOptimalRoom(SchedulingProblem problem, Course course) {
	  List<Double> unordered = new ArrayList<Double>();
	    for (int r = 0; r < problem.rooms.size(); r++) //Get all distances
	    	unordered.add(penaltyCost(problem.rooms.get(r), course));
	  //get sorted array of the index values
	    List<IndexedEntry<Double>> ordered = new ArrayList<>();
	    for (int i = 0; i < unordered.size(); i++) {
	        IndexedEntry<Double> entry = new IndexedEntry<>(i, unordered.get(i));
	        ordered.add(entry);
	    }
	    Collections.sort(ordered,Collections.reverseOrder());

	    return ordered;	  
  }
  
  //calculate penalty/distance cost
  public double penaltyCost(Room r, Course c) {
	  Building b1 = r.b;
      Building b2 = c.preferredLocation;
      double xDist = (b1.xCoord - b2.xCoord) * (b1.xCoord - b2.xCoord);
      double yDist = (b1.yCoord - b2.yCoord) * (b1.yCoord - b2.yCoord);
      double dist = Math.sqrt(xDist + yDist);
      return dist;
  }

  // This is a very naive baseline scheduling strategy
  // It should be easily beaten by any reasonable strategy
  public Schedule naiveBaseline(SchedulingProblem problem, long deadline) {

    // get an empty solution to start from
    Schedule solution = problem.getEmptySchedule();

    for (int i = 0; i < problem.courses.size(); i++) {
      Course c = problem.courses.get(i);
      boolean scheduled = false;
      for (int j = 0; j < c.timeSlotValues.length; j++) {
        if (scheduled) break;
        if (c.timeSlotValues[j] > 0) {
          for (int k = 0; k < problem.rooms.size(); k++) {
            if (solution.schedule[k][j] < 0) {
              solution.schedule[k][j] = i;
              scheduled = true;
              break;
            }
          }
        }
      }
    }

    return solution;
  }
}
