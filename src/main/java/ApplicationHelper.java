import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Derek on 9/21/2016.
 * Class for holding the data for the GUI
 */
public class ApplicationHelper
{
    private ObservableList<Individual> results;

    private ArrayList<Individual> population;

    private ArrayList<String> series;

    private String fileName;

    private Generation currentGeneration;

    public ApplicationHelper()
    {
        fileName = new String();
        currentGeneration = new Generation();
    }

    public ObservableList<Individual> getResults()
    {
        return results;
    }

    public void setResults(ObservableList<Individual> results)
    {
        this.results = results;
    }

    public ArrayList<Individual> getPopulation()
    {
        return population;
    }

    public void setPopulation(ArrayList<Individual> population)
    {
        this.population = population;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ArrayList<String> getStrings() {
        return series;
    }

    public void setStrings(ArrayList<String> series) {
        this.series = series;
    }

    public Generation getCurrentGeneration()
    {
        return currentGeneration;
    }

    public void setCurrentGeneration(Generation currentGeneration)
    {
        this.currentGeneration = currentGeneration;
    }

    public void saveGeneration()
    {
        String text = "Generation " + currentGeneration.getGenerationNumber() + " Min: " + currentGeneration.getMinimumDistance()
                + " Max: " + currentGeneration.getMaximumDistance() + " Avg: " + currentGeneration.getAverageDistance() + System.currentTimeMillis();
        FileWriter fileWriter;
        BufferedWriter fileStream;
        try
        {
            fileWriter = new FileWriter("generations.txt", true);
            fileStream = new BufferedWriter(fileWriter);
            fileStream.write(text);
            fileStream.newLine();
            fileStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void incrementGeneration()
    {
        this.currentGeneration.setGenerationNumber(this.currentGeneration.getGenerationNumber()+1);
    }

}
