package com.ads.infrastructure;

import com.ads.domain.AdsRepository;
import com.ads.domain.Search;
import com.ads.domain.SearchResult;

public class RealTimeAdsRepository implements AdsRepository {
  public RealTimeAdsRepository(int countryId) {

  }

  @Override
  public SearchResult search(String countryId, Search search) {
    return null;
  }
}
