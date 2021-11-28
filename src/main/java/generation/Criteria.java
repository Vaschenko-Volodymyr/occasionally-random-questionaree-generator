package generation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Criteria {
  private final double expectedPercentage;
  private final Map<Integer, Integer> expectedResponses = new HashMap<>();
  private List<Map<Integer, Integer>> actualResponses = new ArrayList<>();
  private static final DecimalFormat df = new DecimalFormat("0.00");

  public Criteria(double expectedPercentage) {
    this.expectedPercentage = expectedPercentage;
  }

  public Criteria withPositiveAnswers(List<Integer> questionNumbers) {
    questionNumbers.forEach(item -> expectedResponses.put(item, 0));
    return this;
  }

  public Criteria withNegativeAnswers(List<Integer> questionNumbers) {
    questionNumbers.forEach(item -> expectedResponses.put(item, 1));
    return this;
  }

  public Criteria withActualResponses(List<Map<Integer, Integer>> actualResponses) {
    this.actualResponses = actualResponses;
    return this;
  }

  public boolean matches() {
    return expectedPercentage == getActualPercentage();
  }

  public List<Map<Integer, Integer>> improve() {
    double actualPercentage = getActualPercentage();
    if (actualPercentage > expectedPercentage) {
      return decreaseTotalPercentageRandomly();
    } else {
      return increaseTotalPercentageRandomly();
    }
  }

  private List<Map<Integer, Integer>> increaseTotalPercentageRandomly() {
    return this.actualResponses.stream().map(item -> item.entrySet()
                    .stream()
                    .peek(this::increase)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
            .collect(Collectors.toList());
  }

  private List<Map<Integer, Integer>> decreaseTotalPercentageRandomly() {
    return this.actualResponses.stream().map(item -> item.entrySet()
                    .stream()
                    .peek(this::decrease)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
            .collect(Collectors.toList());
  }

  private double getActualPercentage() {
    int question = expectedResponses.keySet().size();
    List<Float> actualMatchPercentage = new ArrayList<>();

    List<Map<Integer, Integer>> actualResponsesForCurrentCriteria = actualResponses.stream().map(responses -> responses.entrySet()
            .stream()
            .filter(response -> expectedResponses.containsKey(response.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))).collect(Collectors.toList());
    for (Map<Integer, Integer> actualResponsesPerPerson : actualResponsesForCurrentCriteria) {
      int matches = 0;
      for (Map.Entry<Integer, Integer> expectedResponse : expectedResponses.entrySet()) {
        if (actualResponsesPerPerson.get(expectedResponse.getKey()).equals(expectedResponse.getValue())) {
          matches++;
        }
      }
      actualMatchPercentage.add((float) matches / question);
    }

    return Double.parseDouble(
            df.format(
                    actualMatchPercentage.stream()
                            .mapToDouble(Float::doubleValue)
                            .sum() / actualMatchPercentage.size()));
  }

  private void increase(Map.Entry<Integer, Integer> response) {
    if (expectedResponses.containsKey(response.getKey()) && !expectedResponses.get(response.getKey()).equals(response.getValue())) {
      if (RandomBoolean.next()) {
        if (response.getValue() == 1) {
          response.setValue(0);
        } else {
          response.setValue(1);
        }
      }
    }
  }

  private void decrease(Map.Entry<Integer, Integer> response) {
    if (expectedResponses.containsKey(response.getKey()) && expectedResponses.get(response.getKey()).equals(response.getValue())) {
      if (RandomBoolean.next() && RandomBoolean.next() && RandomBoolean.next() && RandomBoolean.next()) {
        if (response.getValue() == 1) {
          response.setValue(0);
        } else {
          response.setValue(1);
        }
      }
    }
  }
}
