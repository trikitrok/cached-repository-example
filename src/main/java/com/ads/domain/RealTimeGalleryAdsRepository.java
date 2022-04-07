package com.ads.domain;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class RealTimeGalleryAdsRepository implements GalleryAdsRepository {
  private final Cache cache;
  private final AdsRepository adsRepository;
  private static final String NO_LOCATION_ID = "0";

  public RealTimeGalleryAdsRepository(AdsRepository adsRepository, Cache cache) {
    this.adsRepository = adsRepository;
    this.cache = cache;
  }

  @Override
  public List<GalleryAd> getAll() {
    SearchResult searchResult = adsRepository.search(
        cache.getIdCountry(),
        createSearch(cache.getPropertyTypesPromotion())
    );

    if (searchResult == null) {
      return emptyList();
    }

    return searchResult.getAds().stream()
        .filter(ad -> ad.hasPhoto())
        .map(this::mapAdToAdGallery)
        .collect(Collectors.toList());
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

  private Search createSearch(String projectPropertyTypeId) {
    return new Search(
        cache.getIdCountry(),
        OperationTypes.Sale,
        projectPropertyTypeId,
        NO_LOCATION_ID);
  }
}