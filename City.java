import java.io.File;
import java.io.FileNotFoundException; // Program reads a file
import java.util.ArrayList; // Arraylists are used
import java.util.Scanner; // Imported in order to take a file as input
public class City {
    // Declaring data fields
    public String cityName;
    public int x;
    public int y;
    public ArrayList<City> neighbors; // This stores the neighbors of the city
    City(String cityName, int x, int y){ // Constructor
        this.cityName = cityName;
        this.x = x;
        this.y = y;
    }

    /**
     * Finds neighbors of the given city object
     * @return An arraylist of names as strings
     * @throws FileNotFoundException In case of not finding the necessary file
     */
    public ArrayList<String> findNeighbors() throws FileNotFoundException {
        File connectionsFile = new File("city_connections.txt");
        if (!connectionsFile.exists()) { // First makes sure that txt file is present in the directory.
            System.out.printf("%s can not be found.", connectionsFile);
            System.exit(1);
        }
        Scanner connectionsInput = new Scanner(connectionsFile);
        ArrayList<String> stringArrayList = new ArrayList<>(); // This is the arraylist that will hold the neighboring cities' names.
        while (connectionsInput.hasNextLine()) { // Reads the file
            String line = connectionsInput.nextLine();
            String[] lineSplit = line.split(","); // Split and take each of the cities and put into an array
            if (lineSplit[0].equals(cityName)){ // If the first element is the argument city, then add the second element to stringArrayList
                stringArrayList.add(lineSplit[1]);
            }
            if (lineSplit[1].equals(cityName)){ // If the second element is the argument city, then add the first element to stringArrayList
                stringArrayList.add(lineSplit[0]);
            }
        }
        return stringArrayList; // After all the lines have been read returns the arraylist.
    }
}
