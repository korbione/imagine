package com.dakor.imagine.minibook;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * .
 *
 * @author dkor
 */
public class MiniBookService implements IMiniBookService {
    private static final String QUOTE_PATTERN = "^(.*)/([BO])/([NUD])/(\\d*\\.?\\d+)/(\\d*)$";

    private Store offers;
    private Store bids;

    private IQuoteAction insertQuoteAction;
    private IQuoteAction updateQuoteAction;
    private IQuoteAction deleteQuoteAction;

    public MiniBookService() {
        offers = new Store(() -> true);
        bids = new Store(() -> false);
    }

    @Override
    public void handle(String quoteStr) {
        if (quoteStr == null) {
            throw new IllegalArgumentException("The quote can't be NULL");
        }

        Matcher matcher = Pattern.compile(QUOTE_PATTERN).matcher(quoteStr);

        try {
            if (matcher.find()) {
                Quote quote = new Quote();
                quote.setId(matcher.group(1));
                quote.setPrice(new BigDecimal(matcher.group(4)));
                quote.setVolume(Integer.parseInt(matcher.group(5)));
                quote.setDate(new Date());

                String type = matcher.group(2);
                Store store = "O".equals(type) ? offers : bids;

                String actionType = matcher.group(3);
                store.proceed(getAction(actionType), quote);
            } else {
                throw new IllegalArgumentException(
                        "The quote is not in the format: QuoteID/{B|O}/{N|U|D}/Price/Volume");
            }
        } catch (Exception e) {
            throw new RuntimeException("The error appears while parsing the quote: " + quoteStr, e);
        }
    }

    private IQuoteAction getAction(String type) {
        switch (type) {
            case "N":
                if (insertQuoteAction == null) {
                    insertQuoteAction = (quote, store) -> store.add(quote);
                }
                return insertQuoteAction;
            case "U":
                if (updateQuoteAction == null) {
                    updateQuoteAction = (quote, store) -> {
                        if (store.remove(quote)) {
                            store.add(quote);
                        }
                    };
                }
                return updateQuoteAction;
            case "D":
                if (deleteQuoteAction == null) {
                    deleteQuoteAction = (quote, store) -> store.remove(quote);
                }
                return deleteQuoteAction;
            default:
                throw new UnsupportedOperationException("The type is not supported: " + type);
        }
    }

    @Override
    public List<Quote> getSortedOffers() {
        return offers.getSortedList();
    }

    @Override
    public List<Quote> getSortedBids() {
        return bids.getSortedList();
    }

    @Override
    public void print() {
        System.out.println("OFFER");
        getSortedOffers().forEach(System.out::println);

        System.out.println("\nBID");
        getSortedBids().forEach(System.out::println);
    }

    private static class Store {
        private Set<Quote> quotes;
        private IQuotesCompareStrategy sortStrategy;

        private Lock lock = new ReentrantLock();

        public Store(IQuotesCompareStrategy sortStrategy) {
            this.quotes = new HashSet<>();
            this.sortStrategy = sortStrategy;
        }

        public void proceed(IQuoteAction action, Quote quote) {
            try {
                lock.lock();
                action.proceed(quote, quotes);
            } finally {
                lock.unlock();
            }
        }

        public List<Quote> getSortedList() {
            ArrayList<Quote> sortedQuotes;

            try {
                lock.lock();
                sortedQuotes = new ArrayList<>(quotes);
            } finally {
                lock.unlock();
            }

            sortedQuotes.sort(sortStrategy::compare);

            return sortedQuotes;
        }
    }

    private interface IQuoteAction {
        void proceed(Quote quote, Set<Quote> store);
    }

    @FunctionalInterface
    private interface IQuotesCompareStrategy {
        default int compare(Quote quote1, Quote quote2) {
            if (quote1.equals(quote2)) {
                return 0;
            }

            // compare prices
            int compareTo = quote1.getPrice().compareTo(quote2.getPrice());
            if (compareTo == 0) {
                // for the same prices compare volumes
                if (quote1.getVolume() < quote2.getVolume()) {
                    compareTo = 1;
                } else if (quote1.getVolume() > quote2.getVolume()) {
                    compareTo = -1;
                } else {
                    compareTo = 0;
                }

            } else if (!isLowestPriceFirst()) {
                compareTo *= -1;
            }

            if (compareTo == 0) {
                // if prices and volumes are the same compare dates
                compareTo = quote1.getDate().compareTo(quote2.getDate());
            }

            return compareTo;
        }

        boolean isLowestPriceFirst();
    }
}
