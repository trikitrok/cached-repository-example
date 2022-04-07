package com.ads.helpers;

import com.ads.domain.GalleryAd;

public class GalleryAdBuilder {
  private String id;
  private String photoUrl;
  private String title;
  private String url;
  private long price;
  private String description;
  private int numberOfRooms;
  private int numberOfBathrooms;
  private float builtArea;

  public static GalleryAdBuilder aGalleryAd() {
    return new GalleryAdBuilder();
  }

  public GalleryAd build() {
    return new GalleryAd(id, photoUrl, title, url,
        price, description, numberOfRooms,
        numberOfBathrooms, builtArea);
  }

  public GalleryAdBuilder withId(String id) {
    this.id = id;
    return this;
  }
}
