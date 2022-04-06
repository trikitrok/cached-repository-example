package com.ads.helpers;

import com.ads.Ad;

public class AdBuilder {
  private String photo;
  private String url;
  private float builtArea;
  private int numberOfRooms;
  private int numberOfBathrooms;
  private long price;
  private String title;
  private String description;
  private String id;

  public static AdBuilder aPromotionAd() {
    AdBuilder adBuilder = new AdBuilder();
    adBuilder.withPhotos("");
    return adBuilder;
  }

  public Ad build() {
    return new Ad(
        id, photo, url, builtArea, numberOfBathrooms, numberOfRooms,
        price, title, description
    );
  }

  public AdBuilder withId(String id) {
    this.id = id;
    return this;
  }

  public AdBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public AdBuilder withTitle(String title) {
    this.title = title;
    return this;
  }

  public AdBuilder withPrice(long price) {
    this.price = price;
    return this;
  }

  public AdBuilder withNumberOfBathrooms(int numberOfBathrooms) {
    this.numberOfBathrooms = numberOfBathrooms;
    return this;
  }

  public AdBuilder withNumberOfRooms(int numberOfRooms) {
    this.numberOfRooms = numberOfRooms;
    return this;
  }

  public AdBuilder withBuiltArea(float builtArea) {
    this.builtArea = builtArea;
    return this;
  }

  public AdBuilder withUrl(String url) {
    this.url = url;
    return this;
  }

  public AdBuilder withPhotos(String photo) {
    this.photo = photo;
    return this;
  }


}
