package com.dakor.imagine.minibook;

import java.util.List;

/**
 * .
 *
 * @author dkor
 */
public interface IMiniBookService {

    void handle(String quote);

    List<Quote> getSortedOffers();

    List<Quote> getSortedBids();

    void print();
}
