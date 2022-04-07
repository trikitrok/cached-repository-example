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
    public List<GalleryAd> getAll()  {
      long timeInMillis = clock.getTimeInMillis();
      if (noNeedForFreshAds(timeInMillis)) {
        return cachedGalleryAds;
      }
      refreshCacheOn(timeInMillis);
      return cachedGalleryAds;
    }

    private void refreshCacheOn(long timeInMillis)  {
      lastRefreshTime = timeInMillis;
      cachedGalleryAds = galleryAdsRepository.getAll();
    }

    private boolean noNeedForFreshAds(long timeInMillis) {
      return cachedGalleryAds != null && hasNotCachedValueExpired(timeInMillis);
    }

    private boolean hasNotCachedValueExpired(long timeInMillis) {
      return duration.toMillis() + lastRefreshTime >= timeInMillis;
    }
}
