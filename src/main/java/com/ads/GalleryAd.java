package com.ads;

import java.util.Objects;

public class GalleryAd {
  private final String id;
  private final String photoUrl;
  private final String title;
  private final String url;
  private final long price;
  private final String description;
  private final int numberOfRooms;
  private final int numberOfBathrooms;
  private final float builtArea;

  public GalleryAd(String id, String photoUrl, String title, String url, long price, String description, int numberOfRooms, int numberOfBathrooms, float builtArea) {

    this.id = id;
    this.photoUrl = photoUrl;
    this.title = title;
    this.url = url;
    this.price = price;
    this.description = description;
    this.numberOfRooms = numberOfRooms;
    this.numberOfBathrooms = numberOfBathrooms;
    this.builtArea = builtArea;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GalleryAd galleryAd = (GalleryAd) o;
    return price == galleryAd.price && numberOfRooms == galleryAd.numberOfRooms && numberOfBathrooms == galleryAd.numberOfBathrooms && Float.compare(galleryAd.builtArea, builtArea) == 0 && Objects.equals(id, galleryAd.id) && Objects.equals(photoUrl, galleryAd.photoUrl) && Objects.equals(title, galleryAd.title) && Objects.equals(url, galleryAd.url) && Objects.equals(description, galleryAd.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, photoUrl, title, url, price, description, numberOfRooms, numberOfBathrooms, builtArea);
  }
}
