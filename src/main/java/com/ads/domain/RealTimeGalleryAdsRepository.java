package com.ads.domain;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class RealTimeGalleryAdsRepository implements GalleryAdsRepository {
  private final Configuration configuration;
  private final AdsRepository adsRepository;
  private static final String NO_LOCATION_ID = "0";

  public RealTimeGalleryAdsRepository(
      AdsRepository adsRepository,
      Configuration configuration) {
    this.adsRepository = adsRepository;
    this.configuration = configuration;
  }

  @Override
  public List<GalleryAd> getAll() {
    SearchResult searchResult = adsRepository.search(
        configuration.getCountryId(),
        createSearch(configuration.getPromotionPropertyType())
    );

    if (searchResult == null) {
      return emptyList();
    }

    return searchResult.getAds().stream()
        .filter(ad -> ad.hasPhoto())
        .map(GalleryAd::fromAd)
        .collect(Collectors.toList());
  }

  private Search createSearch(String projectPropertyTypeId) {
    return new Search(
        configuration.getCountryId(),
        OperationTypes.Sale,
        projectPropertyTypeId,
        NO_LOCATION_ID);
  }
}