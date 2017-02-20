package com.dakor.imagine.animation;

import java.util.ArrayList;
import java.util.List;

/**
 * The class to animate the init string with set speed.
 *
 * @author dkor
 */
public class Animation {
    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 10;

    private static final String INIT_PATTERN = "^[\\.RL]{1,50}$";
    private static final String EMPTY_ELEMENT_PATTERN = "^\\.*$";

    private static final char EMPTY_CHAR = '.';
    private static final char RIGHT_CHAR = 'R';
    private static final char LEFT_CHAR = 'L';
    private static final char DECORATION_CHAR = 'X';

    /**
     * Animates the given string via moving significant characters to right/left according set speed.
     *
     * @param speed
     *            the amount of positions in the string which significant characters should move for 1 iteration
     * @param init
     *            the animating string
     * @return the arrays of animation snapshots
     */
    public String[] animate(int speed, String init) {
        if (speed < MIN_SPEED || speed > MAX_SPEED) {
            throw new IllegalArgumentException("The speed <" + speed + "> should be in the range 1 - 10 inclusive");
        }

        if (init == null || !init.matches(INIT_PATTERN)) {
            throw new IllegalArgumentException(String.format("The init parameter <%s> should contain 1-50 characters " +
                    "inclusive from the set { '%s', '%s', '%s' }", init, EMPTY_CHAR, RIGHT_CHAR, LEFT_CHAR));
        }

        List<String> elements = new ArrayList<>();

        String element;
        int offset = 0;
        do {
            element = transform(init, offset);
            elements.add(element);
            offset += speed;
        } while (!element.matches(EMPTY_ELEMENT_PATTERN));

        return elements.toArray(new String[elements.size()]);
    }

    private String transform(String element, int offset) {
        // copy the string with emptying all significant characters
        char[] chArray = element.replace(RIGHT_CHAR, EMPTY_CHAR).replace(LEFT_CHAR, EMPTY_CHAR).toCharArray();

        for (int i = 0; i < element.length(); i++) {
            char ch = element.charAt(i);
            int idx = i;
            switch (ch) {
                case RIGHT_CHAR:
                    if ((idx += offset) < chArray.length) {
                        chArray[idx] = DECORATION_CHAR;
                    }
                    break;
                case LEFT_CHAR:
                    if ((idx -= offset) >= 0) {
                        chArray[idx] = DECORATION_CHAR;
                    }
                    break;
                case EMPTY_CHAR:
                    break;
                default:
                    throw new UnsupportedOperationException("The character <" + ch + "> is not supported yet");
            }
        }

        return new String(chArray);
    }
}
