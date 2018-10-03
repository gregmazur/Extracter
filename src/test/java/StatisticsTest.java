import static org.junit.Assert.*;

import java.util.Arrays;

public class StatisticsTest {

  @org.junit.Test
  public void max() {
    Statistics statistics = new Statistics();
    statistics.times.addAll(Arrays.asList(1L, 2L, 3L, 4L, 56L, 7L, 89L, 10L));

    assertEquals("Should be 89", 89, statistics.max());
  }

  @org.junit.Test
  public void avrg() {
    Statistics statistics = new Statistics();
    statistics.times.addAll(Arrays.asList(1L, 2L, 3L, 4L, 56L, 7L, 89L, 10L));

    assertEquals("Should be 21", 21, statistics.avrg());
  }
}