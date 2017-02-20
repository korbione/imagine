package com.dakor.imagine.minibook;

import com.dakor.imagine.ExpectsException;
import com.dakor.imagine.ExpectsExceptionRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Stream;


/**
 * The JUnit tests to verify {@link MiniBookService}.
 *
 * @author dkor
 */
@RunWith(ExpectsExceptionRunner.class)
public class MiniBookTest {
    private IMiniBookService miniBook = new MiniBookService();

    @Test
    public void test() {
        Stream.of("Q1/O/N/1.31/1000000",
                "Q2/B/N/1.21/1000000",
                "Q3/B/N/1.22/1000000",
                "Q4/B/N/1.20/1000000",
                "Q5/B/N/1.20/1000000",
                "Q6/O/N/1.32/1000000",
                "Q7/O/N/1.33/200000",
                "Q5/B/U/1.20/500000",
                "Q7/O/U/1.33/100000",
                "Q7/O/D/0/0").forEach(quote -> miniBook.handle(quote));

        miniBook.print();

        List<Quote> offers = miniBook.getSortedOffers();
        Assert.assertNotNull(offers);
        Assert.assertEquals("The amount of the offers is wrong", 2, offers.size());
        Assert.assertEquals("Q1/1.31/1000000", offers.get(0).toString());
        Assert.assertEquals("Q6/1.32/1000000", offers.get(1).toString());

        List<Quote> bids = miniBook.getSortedBids();
        Assert.assertNotNull(bids);
        Assert.assertEquals("The amount of the bids is wrong", 4, bids.size());
        Assert.assertEquals("Q3/1.22/1000000", bids.get(0).toString());
        Assert.assertEquals("Q2/1.21/1000000", bids.get(1).toString());
        Assert.assertEquals("Q4/1.20/1000000", bids.get(2).toString());
        Assert.assertEquals("Q5/1.20/500000", bids.get(3).toString());
    }

    @Test
    @ExpectsException(type = IllegalArgumentException.class)
    public void testNull() {
        miniBook.handle(null);
    }

    @Test
    @ExpectsException(type = RuntimeException.class, message = "The quote is not in the format")
    public void testIncorrectQuote() {
        miniBook.handle("incorrect");
    }

    @Test
    @ExpectsException(type = RuntimeException.class, message = "The quote is not in the format")
    public void testUnknownType() {
        miniBook.handle("Q7/S/U/1.33/100000");
    }

    @Test
    @ExpectsException(type = RuntimeException.class, message = "The quote is not in the format")
    public void testUnknownAction() {
        miniBook.handle("Q7/O/X/1.33/100000");
    }

    @Test
    @ExpectsException(type = RuntimeException.class, message = "The quote is not in the format")
    public void testIncorrectPrice() {
        miniBook.handle("Q7/O/U/1.2.3/100000");
    }

    @Test
    @ExpectsException(type = RuntimeException.class, message = "The quote is not in the format")
    public void testIncorrectVolume() {
        miniBook.handle("Q7/O/U/1.33/100.001");
    }
}
