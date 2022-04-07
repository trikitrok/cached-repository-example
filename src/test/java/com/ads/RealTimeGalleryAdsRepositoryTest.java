package com.ads;

import com.ads.helpers.AdBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.ads.helpers.AdBuilder.aPromotionAd;
import static com.ads.helpers.SearchBuilder.aSearch;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
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
  void when_get_all_ads_then_search_only_projects_from_network() {
    when(adsRepository.search(COUNTRY, search)).thenReturn(searchResult(
        ad("id1").build(),
        ad("id2").build(),
        ad("id3").build()
    ));

    List<GalleryAd> galleryAds = realTimeGalleryAdsRepository.getAll();

    assertThat(galleryAds, is(getExpectedAdsGallery()));
  }

  @Test
  public void when_ad_has_not_photo_ignore_it_for_ad_gallery() {
    when(adsRepository.search(COUNTRY, search)).thenReturn(
        searchResult(ad("id1").withNoPhoto().build())
    );

    List<GalleryAd> galleryAds = realTimeGalleryAdsRepository.getAll();

    assertThat(galleryAds, empty());
  }

  @Test
  void when_get_all_twice_then_return_cache_values() {
    realTimeGalleryAdsRepository = createRealTimeGalleryAdsRepository(true);
    realTimeGalleryAdsRepository.resetCache();
    when(adsRepository.search(COUNTRY, search)).thenReturn(searchResult(ad("id1").build()));

    List<GalleryAd> galleryAdsFirstResult = realTimeGalleryAdsRepository.getAll();
    List<GalleryAd> galleryAdsSecondResult = realTimeGalleryAdsRepository.getAll();

    assertThat(galleryAdsFirstResult, is(galleryAdsSecondResult));
    verify(adsRepository, times(1)).search(COUNTRY, search);
  }

  @Test
  void no_return_cached_results_when_cached_is_expired() {
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

  private List<GalleryAd> getExpectedAdsGallery() {
    return Arrays.asList(
        galleryAd("id1"),
        galleryAd("id2"),
        galleryAd("id3")
    );
  }

  private GalleryAd galleryAd(String id) {
    return new GalleryAd(
        id, PHOTO, TITLE, URL, PRICE, DESCRIPTION, ROOMS,
        BATHROOMS, BUILT_AREA
    );
  }

  private SearchResult searchResult(Ad... ads) {
    return new SearchResult(Arrays.asList(ads));
  }

  private AdBuilder ad(String id) {
    return aPromotionAd().
        withId(id).
        withDescription(DESCRIPTION).
        withTitle(TITLE)
        .withPrice(PRICE)
        .withNumberOfBathrooms(BATHROOMS)
        .withNumberOfRooms(ROOMS)
        .withBuiltArea(BUILT_AREA)
        .withUrl(URL)
        .withPhoto(PHOTO);
  }

  private RealTimeGalleryAdsRepository createRealTimeGalleryAdsRepository(boolean useCache) {
    return new RealTimeGalleryAdsRepository(
        adsRepository, cache,
        useCache, clock);
  }
}