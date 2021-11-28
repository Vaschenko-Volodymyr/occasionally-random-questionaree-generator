import generation.Generator;
import methodics.UsageExample;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Application {

  public static void main(String[] args) throws InterruptedException {
    List<Map<Integer, Integer>> generate = new Generator().generate(60, 100, new UsageExample().getCriterias());
    AtomicInteger personCount = new AtomicInteger(0);
    generate.forEach(item -> System.out.println("Person #" + personCount.incrementAndGet() + " answered: " + item));
  }
}
