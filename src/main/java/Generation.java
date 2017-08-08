/**
 * Created by Derek on 11/3/2016.
 */
public class Generation
{
    public Generation()
    {
        this.generationNumber = 0;
        this.maximumDistance = 0;
        this.minimumDistance = 0;
        this.averageDistance = 0;
    }

    public Generation(int max, int min, double avg, int gen)
    {
        maximumDistance = max;
        minimumDistance = min;
        averageDistance = avg;
        generationNumber = gen;
    }

    private int minimumDistance;
    private int maximumDistance;
    private double averageDistance;
    private int generationNumber;

    public double getMinimumDistance() {
        return minimumDistance;
    }

    public void setMinimumDistance(int minimumDistance) {
        this.minimumDistance = minimumDistance;
    }

    public double getMaximumDistance() {
        return maximumDistance;
    }

    public void setMaximumDistance(int maximumDistance) {
        this.maximumDistance = maximumDistance;
    }

    public double getAverageDistance() {
        return averageDistance;
    }

    public void setAverageDistance(float averageDistance) {
        this.averageDistance = averageDistance;
    }

    public int getGenerationNumber() {
        return generationNumber;
    }

    public void setGenerationNumber(int generationNumber) {
        this.generationNumber = generationNumber;
    }
}
