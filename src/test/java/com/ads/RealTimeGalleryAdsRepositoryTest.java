package com.ads;

import com.ads.helpers.AdBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.ads.helpers.AdBuilder.aPromotionAd;
import static com.ads.helpers.SearchBuilder.aSearch;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

class RealTimeGalleryAdsRepositoryTest {
  private static final String PHOTO = "imagen";
  private static final String TITLE = "titulo";
  private static final String URL = "url";
  private static final String DESCRIPTION = "descripcion";
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
  private Clock clock;
  private Cache cache;

  @BeforeEach
  public void setUp() {
    cache = mock(Cache.class);
    when(cache.getPropertyTypesPromotion()).thenReturn(PROMOTION_TYPE_ID);

    clock = mock(Clock.class);
    when(cache.getIdCountry()).thenReturn(COUNTRY);

    search = aSearch()
        .withCountryId(COUNTRY)
        .withPropertyTypeId(PROMOTION_TYPE_ID)
        .withLocationId(NO_LOCATION_ID)
        .asSale()
        .build();

    adsRepository = mock(AdsRepository.class);

    boolean useCache = false;
    realTimeGalleryAdsRepository = createRealTimeGalleryAdsRepository(useCache);
  }

  @Test
  void maps_all_ads_with_photo_to_gallery_ads() {
    when(adsRepository.search(COUNTRY, search)).thenReturn(searchResult(
        ad("id1", "photo1").build(),
        ad("id2", "photo2").build(),
        ad("id3", "photo3").build()
    ));

    List<GalleryAd> galleryAds = realTimeGalleryAdsRepository.getAll();

    assertThat(galleryAds, contains(
        galleryAd("id1", "photo1"),
        galleryAd("id2", "photo2"),
        galleryAd("id3", "photo3")));
  }

  @Test
  public void ignore_ads_with_no_photos_in_gallery_ads() {
    when(adsRepository.search(COUNTRY, search)).thenReturn(
        searchResult(ad("id1").withNoPhoto().build())
    );

    List<GalleryAd> galleryAds = realTimeGalleryAdsRepository.getAll();

    assertThat(galleryAds, empty());
  }

  @Test
  void when_no_ads_are_found_there_are_no_gallery_ads() {
    when(adsRepository.search(COUNTRY, search)).thenReturn(noSearchResult());

    List<GalleryAd> galleryAds = realTimeGalleryAdsRepository.getAll();

    assertThat(galleryAds, empty());
  }

  @Test
  void when_cache_has_not_expired_the_cached_values_are_used() {
    realTimeGalleryAdsRepository = createRealTimeGalleryAdsRepository(true);
    realTimeGalleryAdsRepository.resetCache();
    when(adsRepository.search(COUNTRY, search)).thenReturn(searchResult(ad("id1").build()));

    List<GalleryAd> galleryAdsFirstResult = realTimeGalleryAdsRepository.getAll();
    List<GalleryAd> galleryAdsSecondResult = realTimeGalleryAdsRepository.getAll();

    assertThat(galleryAdsFirstResult, is(galleryAdsSecondResult));
    verify(adsRepository, times(1)).search(COUNTRY, search);
  }

  @Test
  void when_cache_expires_new_values_are_retrieved() {
    long maxCachedTime = 30 * 60 * 1000L;
    realTimeGalleryAdsRepository = createRealTimeGalleryAdsRepository(true);
    realTimeGalleryAdsRepository.resetCache();
    when(adsRepository.search(COUNTRY, search)).thenReturn(
        searchResult(ad("id1").build()));
    when(clock.getTimeInMillis()).thenReturn(0L, maxCachedTime + 1);

    realTimeGalleryAdsRepository.getAll();
    realTimeGalleryAdsRepository.getAll();

    verify(adsRepository, times(2)).search(COUNTRY, search);
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

  private AdBuilder ad(String id) {
    return ad(id, PHOTO);
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

  private RealTimeGalleryAdsRepository createRealTimeGalleryAdsRepository(boolean useCache) {
    return new RealTimeGalleryAdsRepository(
        adsRepository, cache,
        useCache, clock);
  }

  private SearchResult noSearchResult() {
    return null;
  }
}