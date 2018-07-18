import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Starter {

  public static void main(String... args) throws IOException {

    if (args == null || args.length <=0){
      System.out.println("Path to csv is absent");
      System.exit(1);
    }


    String path = args[0];

    try (
        BufferedReader br = new BufferedReader(new FileReader(path))
    ) {
      String line;

        try (
            BufferedWriter writer = Files.newBufferedWriter(Paths.get("extracted.csv"));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
        ) {
          while ((line = br.readLine()) != null) {
            System.out.println("Reading line");
            String[] csvRecord = line.split(";");
          List<String> first9 = getFirst9(csvRecord);

          TestCaseRecord[] records = getParsed12thColumn(csvRecord[11]);

          for (TestCaseRecord record : records) {
            List<String> words = new ArrayList<>(15);
            words.addAll(first9);
            words.addAll(Arrays.asList(record.getArray()));
            csvPrinter.printRecord(words);
          }
          csvPrinter.flush();
        }
      }
    }
  }

  private static List<String> getFirst9(String[] csvRecord) {
    List<String> result = new ArrayList<>();
    for (int i = 0; i < 9; i++) {
      result.add(csvRecord[i]);
    }
    return result;
  }

  private static TestCaseRecord[] getParsed12thColumn(String column) {
    column = "{\"records\":" + column + "}";
    Wrapper wrapper = new Gson().fromJson(column, Wrapper.class);
    return wrapper.records;
  }


}
