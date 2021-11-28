package methodics;

import generation.Criteria;

import java.util.List;

public class UsageExample {

  public List<Criteria> getCriterias() {
    return List.of(criteria1(),
            criteria2(),
            criteria3());
  }

  private Criteria criteria3() {
    List<Integer> positiveAnswers = List.of(2, 18, 25, 28, 45, 61, 72, 85);
    List<Integer> negativeAnswers = List.of(16, 37, 55, 64, 81, 96);
    return new Criteria(0.66)
            .withPositiveAnswers(positiveAnswers)
            .withNegativeAnswers(negativeAnswers);
  }

  private Criteria criteria2() {
    List<Integer> positiveAnswers = List.of(11, 27, 73, 80);
    List<Integer> negativeAnswers = List.of(1, 17, 24, 36, 54, 63);
    return new Criteria(0.68)
            .withPositiveAnswers(positiveAnswers)
            .withNegativeAnswers(negativeAnswers);
  }

  private Criteria criteria1() {
    List<Integer> positiveAnswers = List.of(
            2, 7, 9, 10, 11, 13, 15, 18, 21, 25, 27, 28, 30, 32, 35, 39, 41, 42, 44, 45, 47, 48,
            52, 53, 58, 59, 61, 69, 72, 73, 75, 76, 80, 84, 85, 86, 88, 90, 91, 92, 93, 94, 95, 99);
    List<Integer> negativeAnswers = List.of(
            1, 3, 4, 5, 6, 8, 12, 14, 16, 17, 19, 20, 22, 23, 24, 26, 29, 31, 33, 34, 36, 37, 38, 40, 43, 46, 49,
            50, 51, 54, 55, 56, 57, 60, 62, 63, 64, 65, 66, 67, 68, 70, 71, 74, 77, 78, 79, 81, 82, 83, 87, 89, 96, 97, 98, 100);
    return new Criteria(0.64)
            .withPositiveAnswers(positiveAnswers)
            .withNegativeAnswers(negativeAnswers);
  }
}
