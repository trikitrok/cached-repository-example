package com.ads.domain;

import java.time.Duration;
import java.util.List;

public class CachedGalleryAdsRepository implements GalleryAdsRepository {
  private final GalleryAdsRepository galleryAdsRepository;
  private final Clock clock;
  private final Duration duration;
  private List<GalleryAd> cachedGalleryAds;
  private long lastRefreshTime;

  public CachedGalleryAdsRepository(
      GalleryAdsRepository galleryAdsRepository,
      Clock clock,
      Duration duration) {
    this.galleryAdsRepository = galleryAdsRepository;
    this.clock = clock;
    this.duration = duration;
  }

  @Override
  public List<GalleryAd> getAll() {
    long timeInMillis = clock.getTimeInMillis();
    if (needToRefreshCache(timeInMillis)) {
      refreshCache(timeInMillis);
    }
    return cachedGalleryAds;
  }

  private boolean needToRefreshCache(long timeInMillis) {
    return cachedGalleryAds == null || hasCacheExpired(timeInMillis);
  }

  private boolean hasCacheExpired(long timeInMillis) {
    return duration.toMillis() + lastRefreshTime < timeInMillis;
  }

  private void refreshCache(long timeInMillis) {
    lastRefreshTime = timeInMillis;
    cachedGalleryAds = galleryAdsRepository.getAll();
  }
}
