import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public class Statistics {

  List<Long> times = new ArrayList<>();

  public void update(long newTime) {
    times.add(newTime);
  }

  public long max() {
    return times.stream().mapToLong(Long::longValue).max().orElseThrow(NoSuchElementException::new);
  }

  public long avrg() {
    long sum = 0;
    for (Long i : times) {
        sum = i + sum;
    }
    return sum/times.size();
  }
}
