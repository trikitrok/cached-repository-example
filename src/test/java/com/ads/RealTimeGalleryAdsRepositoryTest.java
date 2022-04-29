package com.ads;

import com.ads.domain.*;
import com.ads.helpers.AdBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.ads.helpers.AdBuilder.aPromotionAd;
import static com.ads.helpers.SearchBuilder.aSearch;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RealTimeGalleryAdsRepositoryTest {
  private static final String TITLE = "title";
  private static final String URL = "url";
  private static final String DESCRIPTION = "description";
  private static final long PRICE = 1L;
  private static final int BATHROOMS = 1;
  private static final int ROOMS = 1;
  private static final float BUILT_AREA = 1f;
  private static final String PROMOTION_TYPE_ID = "5";
  private static final String NO_LOCATION_ID = "0";
  private static final String COUNTRY = "co";
  private AdsRepository adsRepository;
  private RealTimeGalleryAdsRepository realTimeGalleryAdsRepository;
  private Search search;
  private Configuration configuration;

  @BeforeEach
  public void setUp() {
    configuration = mock(Configuration.class);
    when(configuration.getPromotionPropertyType()).thenReturn(PROMOTION_TYPE_ID);
    when(configuration.getCountryId()).thenReturn(COUNTRY);

    search = aSearch()
        .withCountryId(COUNTRY)
        .withPropertyTypeId(PROMOTION_TYPE_ID)
        .withLocationId(NO_LOCATION_ID)
        .asSale()
        .build();

    adsRepository = mock(AdsRepository.class);

    realTimeGalleryAdsRepository = new RealTimeGalleryAdsRepository(
        adsRepository, configuration
    );
  }

  @Test
  void maps_all_ads_with_photo_to_gallery_ads() {
    when(adsRepository.search(COUNTRY, search)).thenReturn(
        searchResult(
            ad("id1", "photo1.jpg").build(),
            ad("id2", "photo2.jpg").build(),
            ad("id3", "photo3.jpg").build()));

    List<GalleryAd> galleryAds = realTimeGalleryAdsRepository.getAll();

    assertThat(galleryAds, contains(
        galleryAd("id1", "photo1.jpg"),
        galleryAd("id2", "photo2.jpg"),
        galleryAd("id3", "photo3.jpg"))
    );
  }

  @Test
  void when_no_ads_are_found_there_are_no_gallery_ads() {
    when(adsRepository.search(COUNTRY, search)).thenReturn(noSearchResult());

    List<GalleryAd> galleryAds = realTimeGalleryAdsRepository.getAll();

    assertThat(galleryAds, empty());
  }

  @Test
  public void ignore_ads_with_no_photos_in_gallery_ads() {
    when(adsRepository.search(COUNTRY, search)).thenReturn(
        searchResult(aPromotionAd().withId("id1").withNoPhoto().build()));

    List<GalleryAd> galleryAds = realTimeGalleryAdsRepository.getAll();

    assertThat(galleryAds, empty());
  }

  private GalleryAd galleryAd(String id, String photo) {
    return new GalleryAd(
        id, photo, TITLE, URL, PRICE, DESCRIPTION, ROOMS,
        BATHROOMS, BUILT_AREA
    );
  }

  private SearchResult searchResult(Ad... ads) {
    return new SearchResult(Arrays.asList(ads));
  }

  private AdBuilder ad(String id, String photo) {
    return aPromotionAd().
        withId(id).
        withDescription(DESCRIPTION).
        withTitle(TITLE)
        .withPrice(PRICE)
        .withNumberOfBathrooms(BATHROOMS)
        .withNumberOfRooms(ROOMS)
        .withBuiltArea(BUILT_AREA)
        .withUrl(URL)
        .withPhoto(photo);
  }

  private SearchResult noSearchResult() {
    return null;
  }
}