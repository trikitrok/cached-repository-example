package com.ads.domain;

public interface AdsRepository {
  SearchResult search(String countryId, Search search);
}
