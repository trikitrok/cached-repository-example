package com.ads;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RealTimeAdsGalleryRepository implements AdsGalleryRepository {
  private final boolean useCache;
  private final Clock clock;
  private static SearchResult cachedSearchResult;
  private static long cachedTime;
  private final Cache cache;
  private final AdsRepository adsRepository;

  public RealTimeAdsGalleryRepository(AdsRepository adsRepository, Cache cache,
                                      boolean useCache, Clock clock) {

    this.adsRepository = adsRepository;
    this.cache = cache;
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
        .filter(ad -> ad.hasPhotos())
        .map(this::mapAdToAdGallery)
        .collect(Collectors.toList());
  }

  private SearchResult getCachedSearchResult() {
    if (!useCache) {
      return getSearchResult();
    }
    long timeInMillis = clock.getTimeInMillis();
    if (cacheIsExpired(timeInMillis)){
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
    return adsRepository.search(cache.getIdCountry(), createSearch());
  }

  private GalleryAd mapAdToAdGallery(Ad ad) {
    return new GalleryAd(
        ad.getId(),
        ad.getPhotoUrl(),
        ad.getTitle(),
        ad.getUrl(),
        ad.getPrice(),
        ad.getDescription(),
        ad.getNumberOfRooms(),
        ad.getNumberOfBathrooms(),
        ad.getBuiltArea()
    );
  }

  private Search createSearch() {
    String projectPropertyTypeId = cache.getPropertyTypesPromotion();
    String noLocationId = "0";
    return new Search(
        cache.getIdCountry(),
        OperationTypes.Sale,
        projectPropertyTypeId,
        noLocationId);
  }

  public void resetCache(){
    cachedSearchResult = null;
  }
}