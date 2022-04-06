package com.ads;

import com.ads.helpers.AdBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ads.helpers.AdBuilder.aPromotionAd;
import static com.ads.helpers.SearchBuilder.aSearch;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RealTimeAdsGalleryRepositoryTest {
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
  private RealTimeAdsGalleryRepository realTimeAdsGalleryRepository;
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
    when(adsRepository.search(COUNTRY, search)).thenReturn(createSearchResult());

    boolean useCache = false;
    realTimeAdsGalleryRepository = createRealTimeAdsGalleryRepository(useCache);
  }

  private RealTimeAdsGalleryRepository createRealTimeAdsGalleryRepository(boolean useCache) {
    return new RealTimeAdsGalleryRepository(
        adsRepository, cache,
        useCache, clock);
  }

  @Test
  void when_get_all_ads_then_search_only_projects_from_network()  {
    List<GalleryAd> adsGallery = realTimeAdsGalleryRepository.getAll();

    verify(adsRepository).search(COUNTRY, search);
    assertEquals(getExpectedAdsGallery(), adsGallery);
  }

  @Test
  public void when_ad_has_not_photo_ignore_it_for_ad_gallery() {
    SearchResult searchResult = createSearchResult(
        Collections.singletonList(
            aPromotionAd().withId("id1").withDescription(DESCRIPTION).
                withPrice(PRICE).withTitle(TITLE).withUrl(URL)
                .withNumberOfBathrooms(BATHROOMS).withNumberOfRooms(ROOMS)
                .withBuiltArea(BUILT_AREA).build()));
    when(adsRepository.search(COUNTRY, search)).thenReturn(searchResult);

    List<GalleryAd> adsGallery = realTimeAdsGalleryRepository.getAll();

    assertEquals(Collections.emptyList(), adsGallery);
  }

  @Test
  void when_get_all_twice_then_return_cache_values()  {
    RealTimeAdsGalleryRepository realTimeAdsGalleryRepository =
        createRealTimeAdsGalleryRepository(true);
    realTimeAdsGalleryRepository.resetCache();
    SearchResult searchResult = createSearchResult(
        Collections.singletonList(aPromotionAd().withId("id1")
            .withDescription(DESCRIPTION).withPrice(PRICE).withTitle(TITLE)
            .withUrl(URL).withNumberOfBathrooms(BATHROOMS).withNumberOfBathrooms(ROOMS)
            .withBuiltArea(BUILT_AREA).build()));
    when(adsRepository.search(COUNTRY, search)).thenReturn(searchResult);

    List<GalleryAd> adsGalleryFirstResult = realTimeAdsGalleryRepository.getAll();
    List<GalleryAd> adsGallerySecondResult = realTimeAdsGalleryRepository.getAll();

    assertEquals(adsGalleryFirstResult, adsGallerySecondResult);
    verify(adsRepository, times(1)).search(COUNTRY, search);
  }

  @Test
  void no_return_cached_results_when_cached_is_expired()  {
    long maxCachedTime = 30 * 60 * 1000L;
    RealTimeAdsGalleryRepository realTimeNetworkAdsGalleryRepository =
        createRealTimeAdsGalleryRepository(true);
    realTimeNetworkAdsGalleryRepository.resetCache();
    SearchResult searchResult = createSearchResult(Collections.singletonList(
        aPromotionAd().withId("id1").withDescription(DESCRIPTION).withPrice(PRICE)
            .withTitle(TITLE).withUrl(URL).withNumberOfBathrooms(BATHROOMS)
            .withNumberOfRooms(ROOMS).withBuiltArea(BUILT_AREA).build()));
    when(adsRepository.search(COUNTRY, search)).thenReturn(searchResult);
    when(clock.getTimeInMillis()).thenReturn(0L, maxCachedTime + 1);

    realTimeNetworkAdsGalleryRepository.getAll();
    realTimeNetworkAdsGalleryRepository.getAll();

    verify(adsRepository, times(2)).search(COUNTRY, search);
  }

  private List<GalleryAd> getExpectedAdsGallery() {
    return Arrays.asList(
        createAdGallery("id1"),
        createAdGallery("id2"),
        createAdGallery("id3")
    );
  }

  private GalleryAd createAdGallery(String id) {
    return new GalleryAd(
        id, PHOTO, TITLE, URL, PRICE, DESCRIPTION, ROOMS,
        BATHROOMS, BUILT_AREA
    );
  }

  private SearchResult createSearchResult() {
    List<Ad> ads = Arrays.asList(
        ad("id1").build(),
        ad("id2").build(),
        ad("id3").build()
    );
    return createSearchResult(ads);
  }

  private SearchResult createSearchResult(List<Ad> ads) {
    return new SearchResult(ads);
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
        .withPhotos(PHOTO);
  }

}