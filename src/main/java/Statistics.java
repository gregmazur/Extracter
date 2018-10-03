import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Statistics {

  List<Long> times = new ArrayList<>();

  public void update(long newTime) {
    times.add(newTime);
  }

  public long max() {
    long max = 0;
    for (Long i : times) {
      if (i > 0)
        max = i;
    }
    return max;
  }

  public long avrg() {
    long sum = 0;
    for (Long i : times) {
        sum = i + sum;
    }
    return sum/times.size();
  }
}
