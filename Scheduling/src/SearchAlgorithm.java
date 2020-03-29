import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import java.util.List;

public class SearchAlgorithm {
	
	int randRoom;
	int randTimeSlot;
	int newRandRoom;
	int newRandTimeSlot;
	
  // Your search algorithm should return a solution in the form of a valid
  // schedule before the deadline given (deadline is given by system time in ms)
  public Schedule simulatedAnnealing(SchedulingProblem problem, long deadline, int startingSchedule) {
	 double temperature = 10000;
	 double coolingRate = .05;
	 Random rand = new Random();
	 Schedule solution = null;
	
	 if (startingSchedule == 0){ 
		 solution = genRandSchedule(problem); // random starting point
	 }
	 if (startingSchedule == 1){
		solution = genHeurScedule(problem); // starting point based on distance heuristic
	 }
	 
	 double currentScheduleVal = problem.evaluateSchedule(solution);

	 while (temperature > 1) {
		 moveRandCourse(solution, problem); // move a random course to new room[timeslot]
		 double newScheduleVal = problem.evaluateSchedule(solution); // after course moved
		 double energy = newScheduleVal - currentScheduleVal; // compute new enery (schedule scores)
		 
		 if (energy > 0) { // newSchedule is better after moving course
			currentScheduleVal = newScheduleVal;
		 }
		 else if (Math.exp(energy / temperature) > Math.random()) { // compute probability of taking bad move
			 currentScheduleVal = newScheduleVal;
		 }
		 else{
			 moveCourseBack(solution); // no bad move taken, move course back to currentSchedule
		 }
		 
		 temperature *= coolingRate; // reduce temperature by cooling rate
	 }

	 return solution;
  }
  
	
  public void moveRandCourse(Schedule solution, SchedulingProblem problem){
	  Random rand = new Random();
	  boolean courseFound = false;
	
	  while (!courseFound) {
		  randRoom = rand.nextInt(problem.rooms.size()); // generate random room to iterate
		  int[] room = solution.schedule[randRoom];
		  ArrayList<Integer> timeSlotAssigned = new ArrayList<Integer>();
		  for(int i = 0; i < room.length; i++){
			  if(room[i] > -1){
				  timeSlotAssigned.add(i); // get all time slots assigned in this row
			  }
	  	   }
		  if (!timeSlotAssigned.isEmpty()) {
			  randTimeSlot = timeSlotAssigned.get(rand.nextInt(timeSlotAssigned.size())); // generate random time slot
			  courseFound = true;
		  }
	  }
	  boolean slotFound = false;
	  
	  while (!slotFound) {
		  ArrayList<Integer> validCourseTimeSlots = new ArrayList<Integer>();
		  Course c = problem.courses.get(solution.schedule[randRoom][randTimeSlot]); // get the course that was assigned room[timeslot]
		  for(int i = 0; i < problem.NUM_TIME_SLOTS; i++){
			  if(c.timeSlotValues[i] > 0){
				  validCourseTimeSlots.add(i); // get the valid course time slots
			  }
		  }
		  newRandTimeSlot = validCourseTimeSlots.get(rand.nextInt(validCourseTimeSlots.size())); 
		  newRandRoom = rand.nextInt(problem.rooms.size()); 
		  int temp = solution.schedule[newRandRoom][newRandTimeSlot];  // find a random new room[timeslot] for course
		  if (temp < 0) {
			  solution.schedule[newRandRoom][newRandTimeSlot] = solution.schedule[randRoom][randTimeSlot];  //assign new course to new location
			  solution.schedule[randRoom][randTimeSlot] = -1;
			  slotFound = true;
		  }
	  }
  }
  
  public void moveCourseBack(Schedule solution){
	  solution.schedule[randRoom][randTimeSlot] = solution.schedule[newRandRoom][newRandTimeSlot]; // swap course back to location
	  solution.schedule[newRandRoom][newRandTimeSlot] = -1;
  }
    
  public Schedule genRandSchedule(SchedulingProblem problem) { // used for sim annealing starting point
	 Schedule randSchedule = problem.getEmptySchedule();
	 Random rand = new Random();
	 
	 for (int i = 0; i < problem.courses.size(); i++) {
		 Course c = problem.courses.get(i);
		 boolean scheduled = false;
		 while (!scheduled) {
			int randTimeSlot = rand.nextInt(problem.NUM_TIME_SLOTS);
			if (c.timeSlotValues[randTimeSlot] > 0) {
				int randRoom = rand.nextInt(problem.rooms.size());
				if (randSchedule.schedule[randRoom][randTimeSlot] < 0) {
					randSchedule.schedule[randRoom][randTimeSlot] = i;
					scheduled = true;
				}
			}
		
		 }
	 }
	 return randSchedule; 
  }
  
  
  public Schedule genHeurScedule(SchedulingProblem problem) { // used for sim annealing starting point
	  Schedule randSchedule = problem.getEmptySchedule();
	  Random rand = new Random();
	  int tries = 0;
		 
		for (int i = 0; i < problem.courses.size(); i++) {
			Course c = problem.courses.get(i);
			boolean scheduled = false;
			while (!scheduled) {
				int randTimeSlot = rand.nextInt(problem.NUM_TIME_SLOTS);
				if (c.timeSlotValues[randTimeSlot] > 0) {
					int randRoom = rand.nextInt(problem.rooms.size());
					Room r = problem.rooms.get(randRoom);
					if (randSchedule.schedule[randRoom][randTimeSlot] < 0 && penaltyCost(r, c) < 3) { // heuristic added here if distance is decent assign
						randSchedule.schedule[randRoom][randTimeSlot] = i;
						scheduled = true;
					}
					if (tries >= problem.rooms.size() && randSchedule.schedule[randRoom][randTimeSlot] < 0) {
						randSchedule.schedule[randRoom][randTimeSlot] = i;
						tries = 0;
						scheduled = true;
					}
					tries++;
				}
			
			 }
		 }
		 return randSchedule; 
	}

 
  
  public Schedule backTrackCSP(SchedulingProblem problem, long deadline, int type){
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
		
		Course course = problem.courses.get(currCourse);

		for (int i = 0; i < solution.schedule.length; i++) {
			for (int j = 0; j < solution.schedule[i].length; j++) {
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
		for (int s = 0; s < solution.schedule[roomIndex].length; s++) // Get all values
			unordered.add(course.timeSlotValues[s]);
		// get sorted array of the index values
		List<IndexedEntry<Integer>> ordered = new ArrayList<>();
		for (int i = 0; i < unordered.size(); i++) {
			IndexedEntry<Integer> entry = new IndexedEntry<>(i, unordered.get(i));
			ordered.add(entry);
		}
		Collections.sort(ordered, Collections.reverseOrder());
		return ordered;
	}

	//returns ordered list of most optimal roomIndexes
	public List<IndexedEntry<Double>> findOptimalRoom(SchedulingProblem problem, Course course) {
		List<Double> unordered = new ArrayList<Double>();
		for (int r = 0; r < problem.rooms.size(); r++) // Get all distances
			unordered.add(penaltyCost(problem.rooms.get(r), course));
		// get sorted array of the index values
		List<IndexedEntry<Double>> ordered = new ArrayList<>();
		for (int i = 0; i < unordered.size(); i++) {
			IndexedEntry<Double> entry = new IndexedEntry<>(i, unordered.get(i));
			ordered.add(entry);
		}
		Collections.sort(ordered, Collections.reverseOrder());

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
