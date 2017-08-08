import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Derek on 8/24/2016.
 */
public class FileHandler
{
    private File StringFile;

    public FileHandler(String filename)
    {
        StringFile = new File(filename);
    }

    public ArrayList<String> getStringsData()
    {
        ArrayList<String> StringArrayList = new ArrayList<>();
        try
        {

            Scanner e = new Scanner(this.StringFile);

            //Parse the strings from the file
            while(e.hasNext())
            {
                StringArrayList.add(e.next());
            }
        } catch (FileNotFoundException var)
        {
            var.printStackTrace();
        }

        return StringArrayList;
    }

    public File getStringFile()
    {
        return StringFile;
    }
}
