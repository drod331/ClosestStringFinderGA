/**
 * Created by Derek on 9/18/2016.
 */

@SuppressWarnings("DefaultFileTemplate")
public class HammingDistance
{
    private int hammingDistance;

    private int stringIndex;

    HammingDistance(int distance)
    {
        this.hammingDistance = distance;
        this.stringIndex = 0;
    }

    HammingDistance()
    {
        this.hammingDistance = 0;
        this.stringIndex = 0;
    }

    public int getHammingDistance() {
        return hammingDistance;
    }

    public void setHammingDistance(int hammingDistance) {
        this.hammingDistance = hammingDistance;
    }

    public int getStringIndex() {
        return stringIndex;
    }

    public void setStringIndex(int stringIndex) {
        this.stringIndex = stringIndex;
    }

    //Gets the Hamming Distance between two strings, which is simply the number of characters that are not the same in the two strings.
    public static int getHammingDistance(String string1, String string2)
    {
        if(string1 == null || string2 == null)
        {
            throw new IllegalArgumentException("Strings cannot be null");
        }
        //If the strings are not the same length, do not attempt to calculate Hamming Distance
        if(string1.length() != string2.length())
        {
            throw new IllegalArgumentException("Strings must be the same length");
        }

        int distance = 0;

        for(int i = 0; i < string1.length(); i++)
        {
            if(string1.charAt(i) != string2.charAt(i))
            {
                distance++;
            }
        }

        return distance;
    }

    @Override
    public String toString()
    {
        return Integer.toString(hammingDistance);
    }
}
