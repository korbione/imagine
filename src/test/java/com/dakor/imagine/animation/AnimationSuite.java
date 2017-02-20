package com.dakor.imagine.animation;

import com.dakor.imagine.ExpectsException;
import com.dakor.imagine.ExpectsExceptionRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * The tests suite to verify {@link Animation}.
 *
 * @author dkor
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AnimationSuite.AnimationTest.class,
        AnimationSuite.ParametrizedAnimationTest.class
})
public class AnimationSuite {
    private static final Animation ANIMATION = new Animation();

    /**
     * The JUnit class to test the input parameters are correct.
     */
    @RunWith(ExpectsExceptionRunner.class)
    public static class AnimationTest {

        @Test
        @ExpectsException(type = IllegalArgumentException.class, message = "The init parameter <null> should contain")
        public void testNull() {
            ANIMATION.animate(1, null);
        }

        @Test
        @ExpectsException(type = IllegalArgumentException.class, message = "The init parameter <> should contain")
        public void testEmptyInit() {
            ANIMATION.animate(1, "");
        }

        @Test
        @ExpectsException(type = IllegalArgumentException.class, message = "The init parameter <..R.Q.> should contain")
        public void testWrongInit() {
            ANIMATION.animate(1, "..R.Q.");
        }

        @Test
        @ExpectsException(type = IllegalArgumentException.class, message = "The init parameter <.......")
        public void testTooLargeInitLength() {
            ANIMATION.animate(1, createDefaultInit(51));
        }

        @Test
        public void testMaxInitLength() {
            ANIMATION.animate(1, createDefaultInit(50));
        }

        @Test
        public void testMinInitLength() {
            ANIMATION.animate(1, createDefaultInit(1));
        }

        @Test
        @ExpectsException(type = IllegalArgumentException.class,
                message = "The speed <11> should be in the range 1 - 10 inclusive")
        public void testTooLargeSpeed() {
            ANIMATION.animate(11, createDefaultInit(3));
        }

        @Test
        public void testMaxSpeed() {
            ANIMATION.animate(10, createDefaultInit(3));
        }

        @Test
        @ExpectsException(type = IllegalArgumentException.class,
                message = "The speed <-1> should be in the range 1 - 10 inclusive")
        public void testTooSmallSpeed() {
            ANIMATION.animate(-1, createDefaultInit(3));
        }

        @Test
        public void testMinSpeed() {
            ANIMATION.animate(1, createDefaultInit(3));
        }

        private String createDefaultInit(int length) {
            char[] arr = new char[length];
            int i = 0;
            do {
                arr[i] = '.';
            } while (++i < length);

            return new String(arr);
        }
    }

    /**
     * The JUnit class to test output result according input data.
     */
    @RunWith(Parameterized.class)
    public static class ParametrizedAnimationTest {
        private int speed;
        private String init;
        private String[] expected;

        public ParametrizedAnimationTest(String init, int speed, String[] expected) {
            this.speed = speed;
            this.init = init;
            this.expected = expected;
        }

        @Parameterized.Parameters
        public static Collection<Object[]> config() {
            return Arrays.asList(new Object[][]{
                    {"..R....", 2, new String[]{"..X....", "....X..", "......X", "......."}},
                    {"RR..LRL", 3, new String[]{"XX..XXX", ".X.XX..", "X.....X", "......."}},
                    {"LRLR.LRLR", 2, new String[]{"XXXX.XXXX", "X..X.X..X", ".X.X.X.X.", ".X.....X.", "........."}},
                    {"RLRLRLRLRL", 10, new String[]{"XXXXXXXXXX", ".........."}},
                    {"...", 1, new String[]{"..."}},
                    {"LRRL.LR.LRR.R.LRRL.", 1, new String[]{
                            "XXXX.XX.XXX.X.XXXX.",
                            "..XXX..X..XX.X..XX.",
                            ".X.XX.X.X..XX.XX.XX",
                            "X.X.XX...X.XXXXX..X",
                            ".X..XXX...X..XX.X..",
                            "X..X..XX.X.XX.XX.X.",
                            "..X....XX..XX..XX.X",
                            ".X.....XXXX..X..XX.",
                            "X.....X..XX...X..XX",
                            ".....X..X.XX...X..X",
                            "....X..X...XX...X..",
                            "...X..X.....XX...X.",
                            "..X..X.......XX...X",
                            ".X..X.........XX...",
                            "X..X...........XX..",
                            "..X.............XX.",
                            ".X...............XX",
                            "X.................X",
                            "..................."}}
            });
        }

        @Test
        public void test() {
            System.out.println("INPUT:\n" + init + ", " + speed);

            String[] result = ANIMATION.animate(speed, init);

            System.out.println("RESULTS:");
            Stream.of(result).forEach(System.out::println);

            Assert.assertNotNull(result);
            Assert.assertEquals(expected.length, result.length);
            for (int i = 0; i < expected.length; i++) {
                Assert.assertTrue(expected[i].equals(result[i]));
            }
        }
    }
}
