package com.ads.factories;

import com.ads.domain.CachedGalleryAdsRepository;
import com.ads.domain.GalleryAdsRepository;
import com.ads.domain.RealTimeGalleryAdsRepository;
import com.ads.infrastructure.ConfigurationsByCountry;
import com.ads.infrastructure.RealTimeAdsRepository;
import com.ads.infrastructure.SystemClock;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class GalleryAdsRepositoryFactory {
  private static final Duration TIME_TO_EXPIRE_CACHE = Duration.ofMinutes(30L);
  private static Map<Integer, GalleryAdsRepository> instanceByCountry = new HashMap<>();

  public static GalleryAdsRepository createUniqueCached(int countryId) {
    if (!instanceByCountry.containsKey(countryId)) {
      instanceByCountry.put(countryId, createInstance(countryId));
    }
    return instanceByCountry.get(countryId);
  }

  private static GalleryAdsRepository createInstance(int countryId) {
    GalleryAdsRepository galleryAdsRepository = new RealTimeGalleryAdsRepository(
        new RealTimeAdsRepository(countryId),
        ConfigurationsByCountry.get(countryId)
    );
    return new CachedGalleryAdsRepository(
        galleryAdsRepository,
        new SystemClock(),
        TIME_TO_EXPIRE_CACHE);
  }
}
