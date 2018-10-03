import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Starter {

  public static void main(String... args) throws IOException {

    System.out.println(new File("").getAbsolutePath());
    String str;
    File currentFolder = new File(new File("").getAbsolutePath());
    File[] files = currentFolder.listFiles();

    Map<String, Statistics> statisticMap = new HashMap<>();

    for (File file : files) {

      if (!file.getName().contains(".log"))
        continue;

      System.out.println("Reading " + file.getName());

      Map<Key, Long> requsts = new HashMap<>();

      try (
          BufferedReader br = new BufferedReader(new FileReader(file))
      ) {
        String line;

        while ((line = br.readLine()) != null) {
          if (line.contains("DefaultHttpClientConnectionOperator : Connecting to")) {
            String ip = line.substring(line.indexOf("/")+1, line.lastIndexOf(":"));
            String[] words = line.split(" ");
            long time = getTime(words);
            String threadName = getThreadName(words);

            requsts.put(new Key(threadName, ip), time);
          } else if (line
              .contains("DefaultHttpClientConnectionOperator : Connection established")) {
            String ip = line.substring(line.indexOf(">")+1, line.lastIndexOf(":"));
            String[] words = line.split(" ");
            long time = getTime(words);
            String threadName = getThreadName(words);

            Long previousTime = requsts.remove(new Key(threadName, ip));
            if (previousTime != null) {
              final long finalTime = time - previousTime;
              Statistics statistics = statisticMap.computeIfAbsent(ip, s -> new Statistics());
              statistics.update(finalTime);
            }
          }
        }
      }
    }

    try (
        BufferedWriter writer = Files.newBufferedWriter(Paths.get("result.csv"));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
    ) {
      System.out.println("Printing statistics");
      csvPrinter.printRecord("ip", "max Connection time(msec)", "average Connection time(msec)",
          "max Connection time(sec)", "average Connection time(sec)");

      for (Map.Entry<String, Statistics> entry : statisticMap.entrySet()){
        csvPrinter.printRecord(entry.getKey(), entry.getValue().max()/1000000, entry.getValue().avrg()/1000000
            , entry.getValue().max()/1000000000, entry.getValue().avrg()/1000000000);
      }
      csvPrinter.flush();
    }


  }


  private static long getTime(String[] words) {
    for (String word : words) {
      if (word.matches("\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}")) {
        return LocalTime.parse(word, DateTimeFormatter.ofPattern("HH:mm:ss.nnn")).toNanoOfDay();
      }
    }
    throw new IllegalStateException();
  }

  private static String getThreadName(String[] words) {
    for (String word : words) {
      if (word.matches("\\[.{1,}\\]")) {
        return word;
      }
    }
    throw new IllegalStateException();
  }


  private static class Key {

    String threadName;
    String ip;

    public Key(String threadName, String ip) {
      this.threadName = threadName;
      this.ip = ip;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Key key = (Key) o;
      return Objects.equals(threadName, key.threadName) &&
          Objects.equals(ip, key.ip);
    }

    @Override
    public int hashCode() {
      return Objects.hash(threadName, ip);
    }
  }


}
