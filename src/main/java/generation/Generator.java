package generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Generator {

  private final AtomicInteger counter = new AtomicInteger(0);
  private final AtomicBoolean resultFound = new AtomicBoolean(false);
  private volatile List<Map<Integer, Integer>> result = new ArrayList<>();
  private static final int threadsCount = 100;

  public List<Map<Integer, Integer>> generate(int people, int questions, List<Criteria> criterias) throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);
    ArrayList<Callable<Void>> tasks = new ArrayList<>();
    for (int i = 0; i < threadsCount; i++) {
      tasks.add(getTask(criterias, people, questions));
    }
    executorService.invokeAll(tasks);
    executorService.shutdown();
    return result;
  }

  private Callable<Void> getTask(final List<Criteria> criterias, final int people, final int questions) {
    return () -> {
      int currentThreadNumber = counter.incrementAndGet();
      System.out.println("Thread " + currentThreadNumber + " started!");
      List<Map<Integer, Integer>> generatedResults = generateData(people, questions);
      while (true) {
        try {
          checkIfExecutionIsStillNeeded();
          for (Criteria criteria : criterias) {
            checkIfExecutionIsStillNeeded();
            while (!resultFound.get() && !criteria.withActualResponses(generatedResults).matches()) {
              generatedResults = criteria.improve();
            }
          }
          if (allCriteriaPassed(criterias, generatedResults) && setResult(generatedResults)) {
            System.out.println("Result found!");
            break;
          }
        } catch (RuntimeException e) {
          return null;
        }
      }
      System.out.println("Thread " + currentThreadNumber + " finished!");
      return null;
    };
  }

  private static List<Map<Integer, Integer>> generateData(int people, int questions) {
    List<Map<Integer, Integer>> result = new ArrayList<>();
    for (int i = 1; i <= people; i++) {
      Map<Integer, Integer> responses = new HashMap<>();
      for (int j = 1; j <= questions; j++) {
        responses.put(j, RandomBoolean.next() ? 1 : 0);
      }
      result.add(responses);
    }
    return result;
  }

  private synchronized boolean setResult(List<Map<Integer, Integer>> generatedResult) {
    System.out.println("Try to set result");
    if (resultFound.compareAndSet(false, true)) {
      result = generatedResult;
      System.out.println("Success!");
      return true;
    }
    return false;
  }

  private void checkIfExecutionIsStillNeeded() {
    if (resultFound.get()) {
      System.out.println("Result is already found by another thread");
      throw new RuntimeException("Result is already found by another thread");
    }
  }

  private boolean allCriteriaPassed(List<Criteria> criterias, List<Map<Integer, Integer>> generatedResults) {
    Set<Boolean> checker = new HashSet<>();
    for (Criteria criteria : criterias) {
      checkIfExecutionIsStillNeeded();
      if (!criteria.withActualResponses(generatedResults).matches()) {
        checker.add(false);
      }
    }
    return checker.isEmpty();
  }
}
