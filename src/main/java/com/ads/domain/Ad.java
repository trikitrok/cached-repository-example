package com.ads.domain;

import java.util.Objects;

public class Ad {
  private final String id;
  private final String photo;
  private final String url;
  private final float builtArea;
  private final int numberOfBathrooms;
  private final int numberOfRooms;
  private final long price;
  private final String title;
  private final String description;

  public Ad(String id, String photo, String url, float builtArea,
            int numberOfBathrooms, int numberOfRooms, long price,
            String title, String description) {
    this.id = id;
    this.photo = photo;
    this.url = url;
    this.builtArea = builtArea;
    this.numberOfBathrooms = numberOfBathrooms;
    this.numberOfRooms = numberOfRooms;
    this.price = price;
    this.title = title;
    this.description = description;
  }

  public boolean hasPhoto() {
    return !photo.isEmpty();
  }

  public String getId() {
    return id;
  }

  public String getPhotoUrl() {
    return photo;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public long getPrice() {
    return price;
  }

  public String getDescription() {
    return description;
  }

  public int getNumberOfRooms() {
    return numberOfRooms;
  }

  public float getBuiltArea() {
    return builtArea;
  }

  public int getNumberOfBathrooms() {
    return numberOfBathrooms;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Ad ad = (Ad) o;
    return Float.compare(ad.builtArea, builtArea) == 0 && numberOfBathrooms == ad.numberOfBathrooms && numberOfRooms == ad.numberOfRooms && price == ad.price && Objects.equals(id, ad.id) && Objects.equals(photo, ad.photo) && Objects.equals(url, ad.url) && Objects.equals(title, ad.title) && Objects.equals(description, ad.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, photo, url, builtArea, numberOfBathrooms, numberOfRooms, price, title, description);
  }
}
