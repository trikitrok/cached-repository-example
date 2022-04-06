package com.ads;

public interface AdsRepository {
  SearchResult search(String countryId, Search search);
}
