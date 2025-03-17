/**
 * Program finds shortest distance and path of a given node-graph
 * @author Ahmet Emin Bilen, Student ID: 2022400213
 * @since Date: 04.04.2024
 */
import java.io.File; // Program reads a file.
import java.io.FileNotFoundException; // Throws FileNotFoundException
import java.util.ArrayList; // Arraylists are used.
import java.util.Scanner; // Imported in order to take a file as input.
import java.awt.Font; // Changed the font to set the size of city names so that they do not overlap.

public class AhmetEminBilen {
    public static void main(String[] args) throws FileNotFoundException {
        // First makes sure that relevant file exists in the directory.:
        File coordinatesFile = new File("city_coordinates.txt");
        if (!coordinatesFile.exists()) {
            System.out.printf("%s can not be found.", coordinatesFile);
            System.exit(1);
        }
        Scanner coordinatesInput = new Scanner(coordinatesFile); // Scanner object is created

        // Reading the file and constructing the cities:

        ArrayList<City> cities = new ArrayList<>(); // This arraylist will store all the cities as defined at City class and not as String names.
        while (coordinatesInput.hasNextLine()){ // Reads the city_coordinates.txt file
            String line = coordinatesInput.nextLine();
            String[] lineSplit = line.split(", "); // Split into an array of strings.
            City temp = new City(lineSplit[0], Integer.parseInt(lineSplit[1]), Integer.parseInt(lineSplit[2])); // City is constructed from the data read from each line.
            cities.add(temp); // Add the constructed city to the cities arraylist.
        }
        int numberOfCities = cities.size(); // This int variable is often used at the for loops as the number of iteration, true to its name.
        for (int i = 0; i < numberOfCities; i++) { // This for loop iterates through each city, determines their neighbors and adds them into an arraylist.
            ArrayList<String> neighborsNames = cities.get(i).findNeighbors(); // Names of neighbors
            ArrayList<City> neighbors = new ArrayList<>(); // For every city an arraylist to store neighbors is created.
            for (String neighborName: neighborsNames) { // findNeighbors method returns a string arraylist, so we have to convert them to city objects.
                for (int j = 0; j < numberOfCities; j++) {
                    if (neighborName.equals(cities.get(j).cityName)) { // Checks the name.
                        neighbors.add(cities.get(j));
                    }
                }
                cities.get(i).neighbors = neighbors; // The arraylist is defined as neighbors data field of the city
            }
        }

        // Arranging data to be used for solution algorithm:

        double infinity = 1_000_000_000; // If there is no direct path initially, the distance between two cities will be set to infinity.
        double[][] solutionMatrix = new double[numberOfCities][numberOfCities]; // This 2D array of doubles will hold the distances if cities are neighbors, and infinity otherwise.
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) { // Iterates through every pair of cities
                if (!(cities.get(i).neighbors == null)) { // Checks if city has any neighbor
                    if (cities.get(i).neighbors.contains(cities.get(j))) { // Checks if cities are neighbors.
                        solutionMatrix[i][j] = Math.sqrt((cities.get(i).x - cities.get(j).x) * (cities.get(i).x - cities.get(j).x) +
                                (cities.get(i).y - cities.get(j).y) * (cities.get(i).y - cities.get(j).y)); // Calculates the distance and assign its value
                    }
                }
            }
        }
        for (int i = 0; i < solutionMatrix.length; i++) {
            for (int j = 0; j < solutionMatrix[i].length; j++) { // Iterates again to assign infinity values
                if ( !(i == j) && solutionMatrix[i][j] == 0) { // If indexes are the same, cities are not different, and thus, distance should stay as zero
                    solutionMatrix[i][j] = infinity; // Non-neighboring cities' distance is set to infinity.
                }
            }
        }

        // Taking the input and evaluating it:

        // Next two loops below makes almost the same operations, first one is for source and second one is for destination.
        City sourceCity; // Starting city declared
        while (true) { // Loop is used to achieve the desired result of mistaken city name case.
            sourceCity = getSourceCity(cities);
            if (sourceCity.x == -1) { // If coordinates are that of a default city, user prompted again to enter a valid name.
                System.out.println("City named '" + sourceCity.cityName + "' not found. Please enter valid city name.");
            }
            else { // If it is a valid city gets out of the loop
                break;
            }
        }
        City destinationCity; // Ending city declared
        while (true) {
            destinationCity = getDestinationCity(cities);
            if (destinationCity.x == -1) {
                System.out.println("City named '" + destinationCity.cityName + "' not found. Please enter valid city name.");
            }
            else {
                break;
            }
        }
        // Indexes are declared and initialized.
        int indexOfSource = -1;
        int indexOfDestination = -1;
        for (int i = 0; i < numberOfCities; i++) {
            if (sourceCity.cityName.equals(cities.get(i).cityName)) { // Matches the input and data we have
                indexOfSource = i;
            }
        }
        for (int i = 0; i < numberOfCities; i++) {
            if (destinationCity.cityName.equals(cities.get(i).cityName)) {
                indexOfDestination = i;
            }
        }

        // Algorithm is implemented and outputs are printed or shown:

        int[][] pathsFound = findPathMatrix(solutionMatrix); // Declared an array and initialized it as pathfinder matrix
        ArrayList<City> myPath = getPath(indexOfSource, indexOfDestination, pathsFound, cities); // Path is found with the help of getPath method
        if (findDistances(solutionMatrix)[indexOfSource][indexOfDestination] != infinity){ // Checks if a path exists
            // The distance is calculated, formatted and printed as the console output
            System.out.printf("Total distance: %.2f. Path: ", findDistances(solutionMatrix)[indexOfSource][indexOfDestination]);
            for (int i = 0; i < myPath.size() - 1; i++) { // The path arraylist is printed out after the distance
                System.out.print(myPath.get(i).cityName + " -> ");
            }
            System.out.print(myPath.get(myPath.size() - 1).cityName); // Finally the destination city is printed out
            // StdDraw part begins
            StdDraw.setCanvasSize(2377/2, 1055/2); // Set the canvas size so that it fits into a regular screen
            StdDraw.setXscale(0, 2377); // According to the resolution of the map
            StdDraw.setYscale(0, 1055);
            StdDraw.picture(2377/2.0, 1055/2.0, "map.png", 2377, 1055); // Map is placed
            StdDraw.enableDoubleBuffering(); // Enabled to display the map faster
            StdDraw.setPenColor(StdDraw.GRAY); // Every city and road will be drawn in gray
            StdDraw.setFont(new Font("Helvetica", Font.PLAIN, 13)); // Set the font and size so that names of cities on the map do not overlap
            for (int i = 0; i < numberOfCities; i++) { // For each city the position dot, name and connection roads are drawn
                StdDraw.filledCircle(cities.get(i).x, cities.get(i).y, 5); // The dot
                StdDraw.text(cities.get(i).x, cities.get(i).y + 15, cities.get(i).cityName); // Name
                if (!(cities.get(i).neighbors == null)) { // If it has no neighbor, for loop will be passed so that we do not get an index error
                    for (City neighboringCity: cities.get(i).neighbors) {
                        StdDraw.line(cities.get(i).x, cities.get(i).y, neighboringCity.x, neighboringCity.y); // Connections are drawn
                    }
                }
            }
            StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE); // Cities and roads that are on the shortest path will be drawn in blue
            StdDraw.setPenRadius(0.007); // Increased the pen thickness
            for (int i = 0; i < myPath.size(); i++) {
                StdDraw.filledCircle(myPath.get(i).x, myPath.get(i).y, 5);
                StdDraw.text(myPath.get(i).x, myPath.get(i).y + 15, myPath.get(i).cityName); // Cities on the path are overdrawn
            }
            for (int i = 0; i < myPath.size() - 1; i++) {
                StdDraw.line(myPath.get(i).x, myPath.get(i).y, myPath.get(i+1).x, myPath.get(i+1).y); // The used roads are emphasized
            }
            StdDraw.show(); // The map is displayed
            coordinatesInput.close(); // File is closed, since it is not going to be used anymore
        }
        else // If no path is present, user is prompted so
            System.out.println("No path could be found.");
    }

    // Helper methods

    /**
     * Method takes source input, finds the relevant city in the cities arraylist and returns it.
     * @param cities Arraylist of cities that is searched through
     * @return The city that matches the input name
     */
    public static City getSourceCity(ArrayList<City> cities) {
        Scanner input = new Scanner(System.in); // Creates scanner object
        System.out.print("Enter starting city: ");
        String cityName = input.next(); // Takes the input
        City cityFounded = new City(cityName, -1, -1); // Creates a default city with input name and impossible coordinates
        for (int i = 0; i < cities.size(); i++) { // Iterates through every city
            if (cityName.equals(cities.get(i).cityName)) {
                cityFounded = cities.get(i); // If city is found, changes default to the actual data, then breaks
                break;
            }
        }
        return cityFounded; // Returns the city whether it is an actual city or a default city
    }

    /**
     * Method takes destination input, match the relevant city in the cities arraylist and returns it.
     * @param cities Arraylist of cities that will be searched through
     * @return The city that matches the input name
     */
    public static City getDestinationCity(ArrayList<City> cities) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter destination city: ");
        String cityName = input.next();
        City cityFounded = new City(cityName, -1, -1);
        for (int i = 0; i < cities.size(); i++) {
            if (cityName.equals(cities.get(i).cityName)) {
                cityFounded = cities.get(i);
                break;
            }
        }
        return cityFounded;
    }

    /**
     * Method finds the shortest distances between every pair of cities, using Floyd Warshall Algorithm
     * @param matrix 2D array that holds the distances between neighbors
     * @return 2D array that holds the total distances of the shortest paths between every pair of cities, if one exists, and infinity otherwise.
     */
    public static double[][] findDistances(double[][] matrix) {
        double infinity = 1_000_000_000; // Infinity is set to a very large number that cannot be exceeded
        int number = matrix.length; // Number of nodes
        double[][] distanceMatrix = matrix.clone(); // Nested array that is going to be filled and returned
        for (int i = 0; i < number; i++) { // Middle cities that are going to be tried and seen if it shortens the path
            for (int j = 0; j < number; j++) { // Starting city indexes
                for (int k = 0; k < number; k++) { // Ending city indexes
                    if (distanceMatrix[i][k] != infinity && distanceMatrix[k][j] != infinity &&
                            distanceMatrix[i][k] + distanceMatrix[k][j] < distanceMatrix[i][j]) { // Checks if city index of k shortens the path
                        distanceMatrix[i][j] = distanceMatrix[i][k] + distanceMatrix[k][j]; // If it does the shortest distance gets updated
                    }
                }
            }
        }
        return distanceMatrix; // Updated version is returned
    }

    /**
     * Method finds a matrix that can be used to find the shortest path
     * @param graph Graph is a 2D array that holds the distances between neighbors from each city to every other.
     * @return 2D, size of N*N array that contains -1 for cities that can not be connected,
     */
    public static int[][] findPathMatrix(double[][] graph) {
        double infinity = 1_000_000_000; // Very large number representing infinity (inability to connect two cities)
        int number = graph.length; // Number of vertices
        int[][] pathFinderMatrix = new int[number][number]; // 2D array to store intermediate vertices for constructing the shortest paths.
        double[][] distanceMatrix = graph.clone(); // Copy of the graph that is going to be updated through iterations
        // Through iterations, pathFinderMatrix is initialized
        for (int i = 0; i < number; i++) {
            for (int j = 0; j < number; j++) {
                if (i!= j && graph[i][j] != infinity) {
                    pathFinderMatrix[i][j] = i; // If indexes are different and has a connection value is set to index of starting city
                }
                else {
                    pathFinderMatrix[i][j] = -1; // Set to -1 otherwise
                }
            }
        }
        // As the same with findDistance method, matrices are updated through loops. In this method however, predecessors are also updated
        for (int k = 0; k < number; k++) {
            for (int i = 0; i < number; i++) {
                for (int j = 0; j < number; j++) {
                    if (distanceMatrix[i][k] != infinity && distanceMatrix[k][j] != infinity &&
                            distanceMatrix[i][k] + distanceMatrix[k][j] < distanceMatrix[i][j]) { // Means "If there is a shorter path"
                        distanceMatrix[i][j] = distanceMatrix[i][k] + distanceMatrix[k][j];
                        pathFinderMatrix[i][j] = pathFinderMatrix[k][j];
                    }
                }
            }
        }
        return pathFinderMatrix; // Return value that is going to be used to determine path
    }

    /**
     * Method derives the path as an arraylist of cities from the pathfinder 2D array, recursively. Then reverses it, so that the path can be written in the right direction.
     * @param start Index of the starting city
     * @param end Index of the destination city
     * @param pathFinderMatrix 2D array that is the result of findPathMatrix() method
     * @param cities Arraylist of all the cities (nodes)
     * @return Path between the two selected cities as an arraylist of cities.
     */
    public static ArrayList<City> getPath(int start, int end, int[][] pathFinderMatrix, ArrayList<City> cities) {
        ArrayList<City> path = new ArrayList<>(); // Path will be stored as an arraylist of cities
        if (pathFinderMatrix[start][end] == -1) { // The possibility of not being able to reach is eliminated in the main argument so this means we are at the same city
            path.add(cities.get(start)); // If starting and ending cities are the same, path is that city only
            return path; // The city that is both source and destination is returned
        }
        while (start != end) { // Until completing the path from end to start
            path.add(cities.get(end)); // Last city is added to the arraylist
            end = pathFinderMatrix[start][end]; // Last city is updated to previous stop in the actual path
        }
        path.add(cities.get(start)); // Finally starting city is added
        ArrayList<City> reversedPath = new ArrayList<>(); // The path is going to be reversed through the for loop below
        for (int i = path.size() - 1; i >= 0; i--) {
            reversedPath.add(path.get(i));
        }
        return reversedPath; // Reversed version of what is determined is returned
    }
}
