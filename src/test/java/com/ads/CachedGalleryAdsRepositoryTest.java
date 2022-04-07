package com.ads;

import com.ads.domain.CachedGalleryAdsRepository;
import com.ads.domain.Clock;
import com.ads.domain.GalleryAd;
import com.ads.domain.GalleryAdsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static com.ads.helpers.GalleryAdBuilder.aGalleryAd;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class CachedGalleryAdsRepositoryTest {
  private static final Duration CACHE_DURATION = Duration.ofMinutes(30);
  private Clock clock;
  private GalleryAdsRepository galleryAdsRepository;
  private CachedGalleryAdsRepository cachedGalleryAdsRepository;

  @BeforeEach
  void setUp() {
    clock = mock(Clock.class);
    galleryAdsRepository = mock(GalleryAdsRepository.class);

    cachedGalleryAdsRepository = new CachedGalleryAdsRepository(
        galleryAdsRepository,
        clock,
        CACHE_DURATION
    );
  }

  @Test
  void when_cache_has_not_expired_the_cached_values_are_used() {
    when(clock.getTimeInMillis()).thenReturn(0L, CACHE_DURATION.toMillis());
    when(galleryAdsRepository.getAll()).thenReturn(
        singletonList(aGalleryAd().withId("id1").build())
    );

    List<GalleryAd> adsGalleryFirstResult = cachedGalleryAdsRepository.getAll();
    List<GalleryAd> adsGallerySecondResult = cachedGalleryAdsRepository.getAll();

    verify(galleryAdsRepository, times(1)).getAll();
    assertThat(adsGalleryFirstResult, is(adsGallerySecondResult));
  }

  @Test
  void when_cache_expires_new_values_are_retrieved() {
    when(clock.getTimeInMillis()).thenReturn(
        CACHE_DURATION.toMillis(),
        CACHE_DURATION.toMillis() + 1,
        2 * CACHE_DURATION.toMillis() + 1);
    List<GalleryAd> someGalleryAds = singletonList(aGalleryAd().withId("id1").build());
    List<GalleryAd> anotherGalleryAds = singletonList(aGalleryAd().withId("id3").build());
    when(galleryAdsRepository.getAll()).thenReturn(someGalleryAds, anotherGalleryAds);

    List<GalleryAd> galleryAdsFirstResult = cachedGalleryAdsRepository.getAll();
    List<GalleryAd> galleryAdsSecondResult = cachedGalleryAdsRepository.getAll();
    List<GalleryAd> galleryAdsThirdResult = cachedGalleryAdsRepository.getAll();

    verify(galleryAdsRepository, times(2)).getAll();
    assertThat(galleryAdsFirstResult, is(someGalleryAds));
    assertThat(galleryAdsSecondResult, is(galleryAdsFirstResult));
    assertThat(galleryAdsThirdResult, is(anotherGalleryAds));
  }
}