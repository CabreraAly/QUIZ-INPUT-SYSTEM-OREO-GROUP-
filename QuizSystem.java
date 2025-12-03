import java.util.*;

abstract class QuizItem {
    private String questionText;
    private int points;

    public QuizItem(String questionText, int points) {
        this.questionText = questionText;
        this.points = points;
    }

    public abstract void displayQuestion();
    public abstract boolean checkAnswer(String userAnswer);
    public abstract String getCorrectAnswer();

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}

class MultipleChoiceQuestion extends QuizItem {
    private String[] choices;
    private char correctAnswer;

    public MultipleChoiceQuestion(String questionText, String[] choices, char correctAnswer, int points) {
        super(questionText, points);
        this.choices = Arrays.copyOf(choices, choices.length);
        this.correctAnswer = Character.toLowerCase(correctAnswer);
    }

    @Override
    public void displayQuestion() {
        System.out.println(getQuestionText());
        char[] letters = { 'a', 'b', 'c', 'd' };
        for (int i = 0; i < Math.min(choices.length, 4); i++) {
            System.out.println(letters[i] + ": " + choices[i]);
        }
    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        if (userAnswer == null || userAnswer.isEmpty()) return false;
        char answer = Character.toLowerCase(userAnswer.charAt(0));
        return answer == correctAnswer;
    }

    @Override
    public String getCorrectAnswer() {
        return String.valueOf(correctAnswer);
    }
}

class TrueFalseQuestion extends QuizItem {
    private boolean correctAnswer;

    public TrueFalseQuestion(String questionText, boolean correctAnswer, int points) {
        super(questionText, points);
        this.correctAnswer = correctAnswer;
    }

    @Override
    public void displayQuestion() {
        System.out.println(getQuestionText());
        System.out.println("a: True");
        System.out.println("b: False");
    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        if (userAnswer == null || userAnswer.isEmpty()) return false;
        char answer = Character.toLowerCase(userAnswer.charAt(0));
        boolean userAnswerBool = (answer == 'a' || answer == 't');
        return userAnswerBool == correctAnswer;
    }

    @Override
    public String getCorrectAnswer() {
        return correctAnswer ? "True" : "False";
    }
}

class ShortAnswerQuestion extends QuizItem {
    private String correctAnswer;
    private String[] acceptableAnswers;

    public ShortAnswerQuestion(String questionText, String correctAnswer, int points) {
        super(questionText, points);
        this.correctAnswer = correctAnswer.toLowerCase();
        this.acceptableAnswers = new String[] { this.correctAnswer };
    }

    public ShortAnswerQuestion(String questionText, String correctAnswer, String[] acceptableAnswers, int points) {
        super(questionText, points);
        this.correctAnswer = correctAnswer.toLowerCase();
        this.acceptableAnswers = acceptableAnswers;

        for (int i = 0; i < this.acceptableAnswers.length; i++) {
            this.acceptableAnswers[i] = this.acceptableAnswers[i].toLowerCase();
        }
    }

    @Override
    public void displayQuestion() {
        System.out.println(getQuestionText());
        System.out.print("");
    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        if (userAnswer == null || userAnswer.isEmpty()) return false;
        String userAns = userAnswer.toLowerCase().trim();

        for (String acceptable : acceptableAnswers) {
            if (userAns.equals(acceptable)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getCorrectAnswer() {
        return correctAnswer;
    }
}

interface Scorable {
    int calculateScore();
    int getTotalPossiblePoints();
}

class Quiz implements Scorable {
    private String title;
    private QuizItem[] questions;
    private int questionCount;

    public Quiz(String title, int maxQuestions) {
        this.title = title;
        this.questions = new QuizItem[maxQuestions];
        this.questionCount = 0;
    }

    public boolean addQuestion(QuizItem question) {
        if (questionCount >= questions.length) return false;
        questions[questionCount++] = question;
        return true;
    }

    public boolean removeQuestion(int index) {
        if (index < 0 || index >= questionCount) return false;

        for (int i = index; i < questionCount - 1; i++) {
            questions[i] = questions[i + 1];
        }

        questions[--questionCount] = null;
        return true;
    }

    public QuizItem getQuestion(int index) {
        if (index < 0 || index >= questionCount) return null;
        return questions[index];
    }

    public QuizItem[] getAllQuestions() {
        return Arrays.copyOf(questions, questionCount);
    }

    public int getQuestionCount() { return questionCount; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @Override
    public int calculateScore() {
        int score = 0;
        for (int i = 0; i < questionCount; i++) {
            score += questions[i].getPoints();
        }
        return score;
    }

    @Override
    public int getTotalPossiblePoints() {
        int total = 0;
        for (int i = 0; i < questionCount; i++) {
            total += questions[i].getPoints();
        }
        return total;
    }
}

class QuizManager {
    private Scanner scanner;
    private Quiz currentQuiz;

    public QuizManager() {
        scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            clearScreen();
            System.out.println("=== QUIZ MANAGEMENT SYSTEM ===");
            System.out.println("1. Create New Quiz");
            System.out.println("2. Add Questions");
            System.out.println("3. Delete Question");
            System.out.println("4. View Quiz");
            System.out.println("5. Take Quiz");
            System.out.println("6. Clear Quiz");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");

            int choice = safeIntInput();

            switch (choice) {
                case 1 -> createQuiz();
                case 2 -> addQuestions();
                case 3 -> deleteQuestion();
                case 4 -> viewQuiz();
                case 5 -> takeQuiz();
                case 6 -> clearQuiz();
                case 0 -> { System.out.println("Goodbye!"); return; }
                default -> pause("Invalid choice! Press enter...");
            }
        }
    }

    private void createQuiz() {
        clearScreen();
        System.out.println("=== CREATE NEW QUIZ ===");
        System.out.print("Enter quiz title: ");
        String title = scanner.nextLine();

        int maxQuestions = 0;
        while (maxQuestions <= 0) {
            maxQuestions = safeIntInput("Enter max number of questions: ");
        }

        currentQuiz = new Quiz(title, maxQuestions);
        pause("Quiz created! Press enter...");
    }

    private void addQuestions() {
        if (currentQuiz == null) {
            pause("Create a quiz first! Press enter...");
            return;
        }

        while (true) {
            clearScreen();
            System.out.println("=== ADD QUESTIONS ===");
            System.out.println("1. Multiple Choice");
            System.out.println("2. True/False");
            System.out.println("3. Short Answer");
            System.out.println("0. Back");
            System.out.print("Choose: ");

            int choice = safeIntInput();

            if (choice == 0) return;

            System.out.print("Enter question text: ");
            String text = scanner.nextLine();

            int points = 0;
            while (points <= 0) {
                points = safeIntInput("Enter points: ");
            }

            QuizItem newQuestion = switch (choice) {
                case 1 -> createMultipleChoiceQuestion(text, points);
                case 2 -> createTrueFalseQuestion(text, points);
                case 3 -> createShortAnswerQuestion(text, points);
                default -> null;
            };

            if (newQuestion != null) {
                currentQuiz.addQuestion(newQuestion);
                System.out.println("Question added!");
            }

            int another = safeIntInput("Add another? (1 yes, 0 no): ");
            if (another == 0) break;
        }
    }

    private MultipleChoiceQuestion createMultipleChoiceQuestion(String text, int points) {
        String[] choices = new String[4];
        char[] letters = { 'a', 'b', 'c', 'd' };

        for (int i = 0; i < 4; i++) {
            System.out.print("Enter choice " + letters[i] + ": ");
            choices[i] = scanner.nextLine();
        }

        char correct = safeCharInput("Correct answer (a/b/c/d): ", Arrays.asList('a','b','c','d'));
        return new MultipleChoiceQuestion(text, choices, correct, points);
    }

    private TrueFalseQuestion createTrueFalseQuestion(String text, int points) {
        char ans = safeCharInput("Correct answer (t/f): ", Arrays.asList('t','f','T','F'));
        return new TrueFalseQuestion(text, ans == 't' || ans == 'T', points);
    }

    private ShortAnswerQuestion createShortAnswerQuestion(String text, int points) {
        System.out.print("Correct answer: ");
        String correct = scanner.nextLine();

        System.out.print("Alternative answers (comma separated, optional): ");
        String alt = scanner.nextLine();

        if (alt.trim().isEmpty()) {
            return new ShortAnswerQuestion(text, correct, points);
        }

        String[] acceptable = Arrays.stream(alt.split(","))
                .map(String::trim)
                .toArray(String[]::new);

        return new ShortAnswerQuestion(text, correct, acceptable, points);
    }

    private void deleteQuestion() {
        if (currentQuiz == null || currentQuiz.getQuestionCount() == 0) {
            pause("No questions to delete! Press enter...");
            return;
        }

        clearScreen();
        listQuestions();

        int num = safeIntInput("Delete question number (0 cancel): ");
        if (num == 0) return;

        if (currentQuiz.removeQuestion(num - 1)) {
            pause("Question deleted! Press enter...");
        } else {
            pause("Invalid question! Press enter...");
        }
    }

    private void viewQuiz() {
        if (currentQuiz == null) {
            pause("No quiz created yet! Press enter...");
            return;
        }

        clearScreen();
        System.out.println("=== QUIZ: " + currentQuiz.getTitle() + " ===");

        QuizItem[] questions = currentQuiz.getAllQuestions();

        for (int i = 0; i < questions.length; i++) {
            System.out.println("\nQuestion " + (i + 1) + ":");
            questions[i].displayQuestion();
            System.out.println("Correct answer: " + questions[i].getCorrectAnswer());
        }

        pause("\nPress enter to continue...");
    }

    private void takeQuiz() {
        if (currentQuiz == null || currentQuiz.getQuestionCount() == 0) {
            pause("No quiz available! Press enter...");
            return;
        }

        clearScreen();
        int score = 0;
        List<Integer> wrong = new ArrayList<>();

        QuizItem[] qs = currentQuiz.getAllQuestions();

        for (int i = 0; i < qs.length; i++) {
            System.out.println("\nQuestion " + (i + 1) + ":");
            qs[i].displayQuestion();

            String userAnswer;

            if (qs[i] instanceof MultipleChoiceQuestion) {
                userAnswer = String.valueOf(
                        safeCharInput("Your answer (a/b/c/d): ", Arrays.asList('a','b','c','d'))
                );
            } else if (qs[i] instanceof TrueFalseQuestion) {
                userAnswer = String.valueOf(
                        safeCharInput("Your answer (t/f): ", Arrays.asList('t','f','T','F'))
                );
            } else {
                System.out.print("Your answer: ");
                userAnswer = scanner.nextLine();
            }

            if (qs[i].checkAnswer(userAnswer)) {
                score += qs[i].getPoints();
                System.out.println("Correct! +" + qs[i].getPoints() + " points");
            } else {
                wrong.add(i + 1);
                System.out.println("Incorrect! Correct answer: " + qs[i].getCorrectAnswer());
            }
        }

        System.out.println("\n=== RESULTS ===");
        System.out.println("Score: " + score + "/" + currentQuiz.getTotalPossiblePoints());

        if (!wrong.isEmpty()) {
            System.out.println("Wrong questions: " + wrong);
        }

        pause("Press enter to continue...");
    }

    private void clearQuiz() {
        currentQuiz = null;
        pause("Quiz cleared! Press enter...");
    }

    private void listQuestions() {
        QuizItem[] questions = currentQuiz.getAllQuestions();
        for (int i = 0; i < questions.length; i++) {
            System.out.println((i + 1) + ". " + questions[i].getQuestionText() +
                    " (" + questions[i].getPoints() + ")");
        }
    }

    private int safeIntInput() { return safeIntInput(""); }

    private int safeIntInput(String prompt) {
        while (true) {
            if (!prompt.isEmpty()) System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid number.");
            }
        }
    }

    private char safeCharInput(String prompt, List<Character> allowed) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();

            if (line != null && !line.isEmpty()) {
                char c = line.toLowerCase().charAt(0);
                if (allowed.contains(c)) return c;
            }

            System.out.println("Invalid input. Allowed: " + allowed);
        }
    }

    private void pause(String msg) {
        System.out.println(msg);
        scanner.nextLine();
    }

    private void clearScreen() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("\n".repeat(50));
        }
    }
}

public class QuizSystem {
    public static void main(String[] args) {
        new QuizManager().run();
    }
}
