import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Derek on 10/15/2016.
 */
public class Individual implements Comparable
{
    private String string;
    private HammingDistance furthestHammingDistance;
    private HammingDistance closestHammingDistance;
    //Possibly change to float or double?
    private int averageHammingDistance;

    Individual(String string)
    {
        this.string = string;
    }

    Individual(String string, HammingDistance closest, HammingDistance furthest, int average)
    {
        this.string = string;
        this.closestHammingDistance = closest;
        this.furthestHammingDistance = furthest;
        this.averageHammingDistance = average;
    }

    public String getString()
    {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public HammingDistance getFurthestHammingDistance() {
        return furthestHammingDistance;
    }

    public void setFurthestHammingDistance(HammingDistance furthestHammingDistance) {
        this.furthestHammingDistance = furthestHammingDistance;
    }

    public HammingDistance getClosestHammingDistance() {
        return closestHammingDistance;
    }

    public void setClosestHammingDistance(HammingDistance closestHammingDistance) {
        this.closestHammingDistance = closestHammingDistance;
    }

    public int getAverageHammingDistance() {
        return averageHammingDistance;
    }

    public void setAverageHammingDistance(int averageHammingDistance) {
        this.averageHammingDistance = averageHammingDistance;
    }

    @Override
    public int compareTo(Object o)
    {
        Individual individual = (Individual)o;
        if(this.averageHammingDistance < individual.getAverageHammingDistance())
        {
            return -1;
        }
        else if (this.getAverageHammingDistance() > individual.getAverageHammingDistance())
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor)
    {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
