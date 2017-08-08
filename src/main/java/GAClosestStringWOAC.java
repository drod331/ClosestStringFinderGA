import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.*;
import java.util.function.Predicate;


/**
 * Created by Derek on 9/14/2016.
 */
public class GAClosestStringWOAC extends Application
{
    private static ApplicationHelper applicationHelper;

    public static void main(String args[])
    {
        //Used to pass data to the GUI
        applicationHelper = new ApplicationHelper();

        FileHandler fileHandler = new FileHandler(args[1]);
        ArrayList<String> strings = fileHandler.getStringsData();
        applicationHelper.setFileName(fileHandler.getStringFile().getName());

        //Determine the alphabet of the input strings
        ArrayList<Character> alphabet = new ArrayList<>();
        for (String s : strings)
        {
            for (int i = 0; i < s.length(); i++)
            {
                if(!alphabet.contains(s.charAt(i)))
                {
                    alphabet.add(s.charAt(i));
                }
            }
        }

        //Randomly generate individuals, using the alphabet determined by the input strings, to create a total population size of 100.
        int m = strings.get(0).length();
        ArrayList<Individual> population = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        for(int i = 0; i < 100; i++)
        {

            StringBuilder stringBuilder = new StringBuilder();
            String temp;
            for(int j = 0; j < m; j++)
            {
                /*Randomly choose symbols from the discovered alphabet,
                add them to the string until it meets the desired string length*/
                stringBuilder.append(alphabet.get(random.nextInt(alphabet.size())));
            }
            temp = stringBuilder.toString();
            //Calculate its Hamming Distance from input strings
            HammingDistance furthestHammingDistance = new HammingDistance(temp.length()), closestHammingDistance = new HammingDistance();

            int tempDistance, averageHammingDistance = 0;
            Generation generation = new Generation();
            //Iterate through input strings, find distances
            for(int j = 0; j < strings.size(); j++)
            {
                tempDistance = HammingDistance.getHammingDistance(strings.get(j), temp);
                if(tempDistance > closestHammingDistance.getHammingDistance())
                {
                    closestHammingDistance.setHammingDistance(tempDistance);
                    closestHammingDistance.setStringIndex(j);
                    generation.setMinimumDistance(tempDistance);
                }
                if(tempDistance < furthestHammingDistance.getHammingDistance())
                {
                    furthestHammingDistance.setHammingDistance(tempDistance);
                    furthestHammingDistance.setStringIndex(j);
                    generation.setMaximumDistance(tempDistance);
                }
                averageHammingDistance += tempDistance;
            }
            averageHammingDistance /= strings.size();
            generation.setAverageDistance(averageHammingDistance);
            population.add(new Individual(temp, furthestHammingDistance, closestHammingDistance, averageHammingDistance));
            applicationHelper.setCurrentGeneration(generation);
        }

        applicationHelper.setPopulation(population);
        applicationHelper.setStrings(strings);
        launch(args);
    }

    /**
     *
     * @param mutationRate Decimal value to determine the probability of a mutation occurring
     */
    private static void swapMutation(double mutationRate)
    {
        ArrayList<Individual> population = applicationHelper.getPopulation();
        for(Individual i : population)
        {
            if(Math.random() < mutationRate)
            {
                Random random = new Random(System.currentTimeMillis());
                int firstIndex = random.nextInt(i.getString().length());
                int secondIndex = random.nextInt(i.getString().length());
                char temp = i.getString().charAt(firstIndex);
                StringBuilder mutatedString = new StringBuilder(i.getString());
                mutatedString.setCharAt(firstIndex, i.getString().charAt(secondIndex));
                mutatedString.setCharAt(secondIndex, temp);
                i.setString(mutatedString.toString());
            }
        }
        applicationHelper.setPopulation(population);
    }

    /**
     * Same crossover function, just with different selection parameters
     */
    private void crossover(SelectionMethod selectionMethod)
    {
        ArrayList<Individual> population = applicationHelper.getPopulation();
        ArrayList<Individual> newGeneration = new ArrayList<>();
        if(selectionMethod == SelectionMethod.FITNESS_PROPORTIONATE)
        {
            ArrayList<Double> probabilities = new ArrayList<>();
            for (Individual i : population)
            {
                probabilities.add((double)i.getAverageHammingDistance());
            }
            int max = population.get(0).getString().length(), sum = 0;
            for (int i = 0; i < probabilities.size(); i++)
            {
                probabilities.set(i, (max - probabilities.get(i)));
                sum += (max - probabilities.get(i));
            }
            for (int i = 0; i < probabilities.size(); i++)
            {
                probabilities.set(i, (max - probabilities.get(i))/sum);
            }
            int firstIndex = 0;
            int secondIndex = 0;
            for (int i = 0; i < population.size(); i++)
            {
                double probability = Math.random(), probabilitySum = 0;
                for (int j = 0; j < probabilities.size(); j++)
                {
                    probabilitySum += probabilities.get(j);
                    if(probability <= probabilitySum)
                    {
                        if(i % 2 == 1)
                        {
                            firstIndex = j;
                        }
                        else
                        {
                            secondIndex = j;
                        }
                        break;
                    }
                }
                newGeneration.add(createOffspring(population.get(firstIndex), population.get(secondIndex)));
            }
        }
        else if(selectionMethod == SelectionMethod.LOCAL)
        {
            //Pick top 50%
            population.sort((i1, i2) -> i1.compareTo(i2));
            for(int i = 0; i < 12; i++)
            {
                if(newGeneration.size() >= 100)
                {
                    break;
                }
                for(int j = i+1; j < 13; j++)
                {
                    if (newGeneration.size() >= 100)
                    {
                        break;
                    }
                    newGeneration.add(createOffspring(population.get(i), population.get(j)));
                }
            }
        }

        applicationHelper.setPopulation(newGeneration);
    }

    private void nextGeneration()
    {
        //Perform crossover operation
        crossover(SelectionMethod.FITNESS_PROPORTIONATE);
        //Mutate some individuals
        swapMutation(0.001);
    }

    private void applyWisdom(ArrayList<String> inputStrings, int generation)
    {
        for(Individual i : applicationHelper.getPopulation())
        {
            int bestDistance = i.getString().length();
            HammingDistance individualWorst = i.getFurthestHammingDistance();
            for(Individual individual : applicationHelper.getPopulation())
            {
                if(individualWorst.getStringIndex() == individual.getClosestHammingDistance().getStringIndex() && individual.getClosestHammingDistance().getHammingDistance() < bestDistance)
                {
                    //Find how far apart the less fit individual is from the fit individual
                    int difference = HammingDistance.getHammingDistance(i.getString(), individual.getString());

                    ArrayList<Integer> visitedIndices = new ArrayList<>();
                    StringBuilder stringBuilder = new StringBuilder(individual.getString().length());
                    for (int j = 0; j < individual.getString().length(); j++)
                    {
                        stringBuilder.append(" ");
                    }
                    //Add all characters that match to the child
                    for (int j = 0; j < individual.getString().length(); j++)
                    {

                        if(i.getString().charAt(j) == individual.getString().charAt(j))
                        {
                            stringBuilder.setCharAt(j, i.getString().charAt(j));
                            visitedIndices.add(j);
                        }
                    }

                    //Add half of the better fit letters to the individual
                    for (int j = 0; j < individual.getString().length()/2; j++)
                    {
                        if(!visitedIndices.contains(j))
                        {
                            stringBuilder.setCharAt(j, individual.getString().charAt(j));
                        }
                    }
                    //Keep the other parent symbols the same
                    for(int j = individual.getString().length()/2; j < individual.getString().length(); j++)
                    {
                        if(!visitedIndices.contains(j))
                        {
                            stringBuilder.setCharAt(j, i.getString().charAt(j));
                        }
                    }
                    i.setString(stringBuilder.toString());
                    bestDistance = individual.getClosestHammingDistance().getHammingDistance();
                }
            }
            int count = 0;
            for (int j = 0; j < inputStrings.size(); j++)
            {
                if(j+1 != inputStrings.size())
                {
                    if(inputStrings.get(j).charAt(generation%inputStrings.size()) == inputStrings.get(j+1).charAt(generation%inputStrings.size()))
                    {
                        count++;
                    }
                }
            }
            StringBuilder stringBuilder = new StringBuilder(i.getString());
            if(count == (inputStrings.size()-1))
            {
                stringBuilder.setCharAt(generation%inputStrings.get(0).length(), inputStrings.get(0).charAt(generation%inputStrings.get(0).length()));
            }
            i.setString(stringBuilder.toString());
        }
    }

    private Individual createOffspring(Individual parentA, Individual parentB)
    {
        ArrayList<Integer> visitedIndices = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < parentA.getString().length(); i++)
        {
            stringBuilder.append(" ");
        }
        //Add all characters that match to the child
        for (int i = 0; i < parentA.getString().length(); i++)
        {
            if(parentA.getString().charAt(i) == parentB.getString().charAt(i))
            {
                stringBuilder.setCharAt(i, parentA.getString().charAt(i));
                visitedIndices.add(i);
            }
        }

        boolean firstParent = true;
        //Alternate between parents to fill out the rest of the child
        for (int i = 0; i < parentA.getString().length(); i++)
        {
            if(!visitedIndices.contains(i))
            {
                //stringBuilder.setCharAt(i, parentA.getString().charAt(i));
                if(firstParent)
                {
                    stringBuilder.setCharAt(i, parentA.getString().charAt(i));
                }
                else
                {
                    stringBuilder.setCharAt(i, parentB.getString().charAt(i));
                }
                firstParent = !firstParent;
            }
        }

        return new Individual(stringBuilder.toString());
    }

    //GUI for displaying output.
    @Override
    public void start(Stage stage)
    {
        //Add the current file accessed to the application title
        ArrayList<Individual> population = applicationHelper.getPopulation();
        stage.setTitle("Closest String Finder - " + applicationHelper.getFileName());

        int lowest = population.get(0).getString().length();
        //Find the lowest, furthest hamming distance of any individual
        for (Individual i : population)
        {
            if(i.getFurthestHammingDistance().getHammingDistance() < lowest)
            {
                lowest = i.getFurthestHammingDistance().getHammingDistance();
            }
        }
        final int low = lowest;


        final ObservableList<Individual> data = FXCollections.observableArrayList();

        Button button = new Button("Find Closest String");

        TextField textField = new TextField();

        Label programInformation = new Label();
        programInformation.setPadding(new Insets(0, 0, 10, 0));
        Label kValue = new Label("Enter K Value");

        ProgressIndicator progressIndicator = new ProgressIndicator(-1);
        progressIndicator.setVisible(false);

        TableColumn hammingDistanceCol = new TableColumn("Hamming Distance");
        hammingDistanceCol.setMinWidth(150);
        hammingDistanceCol.setCellValueFactory(new PropertyValueFactory<Individual, Integer>("furthestHammingDistance"));

        TableColumn stringCol = new TableColumn("String");
        stringCol.setMinWidth(630);
        stringCol.setCellValueFactory(new PropertyValueFactory<Individual, String>("string"));

        TableView tableView = new TableView();
        tableView.getColumns().addAll(hammingDistanceCol, stringCol);

        VBox vBox = new VBox(2);
        vBox.getChildren().addAll(kValue, textField, programInformation);

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setTop(vBox);
        borderPane.setCenter(tableView);
        borderPane.setBottom(button);
        Scene scene  = new Scene(borderPane, 800, 600);

        //Display output window
        stage.setScene(scene);
        stage.show();


        button.setOnAction(event -> {
            if (textField.getText().equals(""))
            {
                programInformation.setText("Invalid K Value.  Please enter an integer.");
                textField.setStyle("-fx-border-color: red");
                programInformation.setTextFill(Paint.valueOf("red"));
                programInformation.setVisible(true);
            }
            else
            {
                textField.setStyle("-fx-border-color: transparent");
                programInformation.setTextFill(Paint.valueOf("black"));
                programInformation.setText("Searching for solution...");
                programInformation.setVisible(true);
                progressIndicator.setVisible(true);
                boolean foundResult = findSolution(low, Integer.parseInt(textField.getText()));
                progressIndicator.setVisible(false);
                if(foundResult)
                {
                    programInformation.setVisible(false);
                    tableView.setItems(applicationHelper.getResults());
                }
                else
                {
                    programInformation.setText("No solution found for k = " + textField.getText());
                    programInformation.setVisible(true);
                    tableView.setItems(FXCollections.emptyObservableList());
                }
            }
        });
    }

    private boolean findSolution(int lowest, int k)
    {
        int i = 0;
        int best = lowest;
        //Loop until it either finds a solution or hits 1000 generations
        while(best > k && i < 1000)
        {
            applicationHelper.saveGeneration();
            nextGeneration();
            applicationHelper.incrementGeneration();
            calculateDistances();
            applyWisdom(applicationHelper.getStrings(), applicationHelper.getCurrentGeneration().getGenerationNumber());
            //recalculate distances
            best = calculateDistances();
            System.out.println(best);
            i++;
        }
        if(best <= k)
        {
            ArrayList<Individual> results = applicationHelper.getPopulation();
            final int temp = best;
            //Removes all values that aren't within the specified k distance, as well as any that were present in the
            //original input strings, as closest string is defined as a "new" string
            results.removeIf(indiv -> indiv.getFurthestHammingDistance().getHammingDistance() > temp || applicationHelper.getStrings().contains(indiv.getString()));
            results.stream().filter(Individual.distinctByKey(individual -> individual.getString()));
            if(results.size() > 0)
            {
                System.out.println("Solution exists");
                applicationHelper.setResults(FXCollections.observableArrayList(results));
                return true;
                //results.forEach(indiv -> data.add(indiv));// System.out.println(indiv.getString() + " " + indiv.getFurthestHammingDistance().getHammingDistance() + " " + indiv.getClosestHammingDistance().getHammingDistance() + " " + indiv.getAverageHammingDistance()));
            }
            else
            {
                System.out.println("Solution does not exist");
                return false;
            }
        }
        else
        {
            System.out.println("Solution might not exist");
            return false;
        }
    }

    private int calculateDistances()
    {
        ArrayList<String> strings = applicationHelper.getStrings();
        ArrayList<Individual> population = applicationHelper.getPopulation();
        int best = strings.get(0).length();
        for (int i = 0; i < population.size(); i++)
        {
            HammingDistance closestHammingDistance = new HammingDistance(population.get(i).getString().length()), furthestHammingDistance = new HammingDistance();
            int averageHammingDistance = 0;
            for(int j = 0; j < strings.size(); j++)
            {
                int tempDistance = HammingDistance.getHammingDistance(strings.get(j), population.get(i).getString());
                if(tempDistance < closestHammingDistance.getHammingDistance())
                {
                    closestHammingDistance.setHammingDistance(tempDistance);
                    closestHammingDistance.setStringIndex(j);
                }
                if(tempDistance > furthestHammingDistance.getHammingDistance())
                {
                    furthestHammingDistance.setHammingDistance(tempDistance);
                    furthestHammingDistance.setStringIndex(j);
                }
                averageHammingDistance += tempDistance;
            }
            averageHammingDistance /= strings.size();
            population.get(i).setAverageHammingDistance(averageHammingDistance);
            population.get(i).setClosestHammingDistance(closestHammingDistance);
            population.get(i).setFurthestHammingDistance(furthestHammingDistance);
            if(furthestHammingDistance.getHammingDistance() < best)
            {
                best = furthestHammingDistance.getHammingDistance();
            }
        }
        return best;
    }
}
