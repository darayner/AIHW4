import java.util.ArrayList;
import java.util.Random;

public class SearchAlgorithm {
	
	int randRoom;
	int randTimeSlot;
	int newRandRoom;
	int newRandTimeSlot;
	
 // returns schedule based on simulated annealing search
  public Schedule simulatedAnnealing(SchedulingProblem problem, long deadline) {
	 double temperature = 10000;
	 double coolingRate = .05;
	 Random rand = new Random();
    
	 Schedule solution = genRandSchedule(problem);
	 double currentScheduleVal = problem.evaluateSchedule(solution);

	 while (temperature > 1) {
		 moveRandCourse(solution, problem); // move a random course to new room[timeslot]
		 double newScheduleVal = problem.evaluateSchedule(solution); // after course moved
		 double energy = newScheduleVal - currentScheduleVal; // compute new enery (schedule scores)
		 
		 if (energy > 0) { // newSchedule is better after moving course
			currentScheduleVal = newScheduleVal;
		 }
		 else if (Math.exp(energy / temperature) > Math.random()) { // compute probability of accepting bad move
			 currentScheduleVal = newScheduleVal;
		 }
		 else{
			 moveCourseBack(solution); // no bad move taken, move course back to currentSchedule
		 }
		 
		 temperature *= coolingRate; // reduce temperature by cooling rate
	 }

	 return solution;
  }
  
// moves course to random location
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
  // undos course location (swap back)
  public void moveCourseBack(Schedule solution){
	  solution.schedule[randRoom][randTimeSlot] = solution.schedule[newRandRoom][newRandTimeSlot]; // swap course back to location
	  solution.schedule[newRandRoom][newRandTimeSlot] = -1;
  }
    
  // generate random schedule to be used by simulated annealing
  public Schedule genRandSchedule(SchedulingProblem problem){
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
  
  
//ADD Back track here Andrew
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  

  
  

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
