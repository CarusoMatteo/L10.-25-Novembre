package it.unibo.mvc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {
    private static final String SETTINGS_FILE_PATH = "src/main/resources/config.yml";
    private static final String OUTPUT_FILE_PATH = "src/main/resources/out.txt";

    private static final int DEFAULT_MIN = 0;
    private static final int DEFAULT_MAX = 100;
    private static final int DEFAULT_ATTEMPTS = 10;

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views the views to attach
     */
    public DrawNumberApp(final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view : views) {
            view.setObserver(this);
            view.start();
        }

        int min;
        int max;
        int attempts;
        try (final BufferedReader file = new BufferedReader(new FileReader(SETTINGS_FILE_PATH))) {
            min = Integer.parseInt(file.readLine().split(": ")[1]);
            max = Integer.parseInt(file.readLine().split(": ")[1]);
            attempts = Integer.parseInt(file.readLine().split(": ")[1]);
        } catch (final Exception e) {
            for (final DrawNumberView view : views) {
                view.displayError("Error while reading configuration file.");
            }

            min = DEFAULT_MIN;
            max = DEFAULT_MAX;
            attempts = DEFAULT_ATTEMPTS;
        }
        this.model = new DrawNumberImpl(min, max, attempts);
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view : views) {
                view.result(result);
            }
        } catch (final IllegalArgumentException e) {
            for (final DrawNumberView view : views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * @param args ignored
     * @throws FileNotFoundException
     */
    public static void main(final String... args) throws FileNotFoundException {
        new DrawNumberApp(
                new DrawNumberViewImpl(),
                new DrawNumberViewImpl(),
                new PrintStreamView(System.out),
                new PrintStreamView(OUTPUT_FILE_PATH));
    }
}
