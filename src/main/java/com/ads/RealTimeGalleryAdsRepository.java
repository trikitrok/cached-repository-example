package com.ads;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RealTimeGalleryAdsRepository implements GalleryAdsRepository {
  private final boolean useCache;
  private final Clock clock;
  private static SearchResult cachedSearchResult;
  private static long cachedTime;
  private final Configuration configuration;
  private final AdsRepository adsRepository;

  public RealTimeGalleryAdsRepository(
      AdsRepository adsRepository,
      Configuration configuration,
      boolean useCache, Clock clock) {

    this.adsRepository = adsRepository;
    this.configuration = configuration;
    this.useCache = useCache;
    this.clock = clock;
  }

  @Override
  public List<GalleryAd> getAll() {
    SearchResult searchResult = getCachedSearchResult();
    if (searchResult == null) {
      return Collections.emptyList();
    }
    return searchResult.getAds().stream()
        .filter(ad -> ad.hasPhoto())
        .map(GalleryAd::fromAd)
        .collect(Collectors.toList());
  }

  private SearchResult getCachedSearchResult() {
    if (!useCache) {
      return getSearchResult();
    }
    long timeInMillis = clock.getTimeInMillis();
    if (cacheIsExpired(timeInMillis)) {
      cachedSearchResult = null;
    }
    if (cachedSearchResult == null) {
      cachedSearchResult = getSearchResult();
      cachedTime = timeInMillis;
    }
    return cachedSearchResult;
  }

  private boolean cacheIsExpired(long timeInMillis) {
    return cachedTime + (30 * 60 * 1000L) < timeInMillis;
  }

  private SearchResult getSearchResult() {
    return adsRepository.search(configuration.getCountryId(), createSearch());
  }

  private Search createSearch() {
    String projectPropertyTypeId = configuration.getPromotionPropertyType();
    String noLocationId = "0";
    return new Search(
        configuration.getCountryId(),
        OperationTypes.Sale,
        projectPropertyTypeId,
        noLocationId);
  }

  public void resetCache() {
    cachedSearchResult = null;
  }
}