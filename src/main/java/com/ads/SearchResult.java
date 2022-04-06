package com.ads;

import java.util.List;
import java.util.Objects;

public class SearchResult {
  private List<Ad> ads;

  public SearchResult(List<Ad> ads) {
    this.ads = ads;
  }

  public List<Ad> getAds() {
    return ads;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SearchResult that = (SearchResult) o;
    return Objects.equals(ads, that.ads);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ads);
  }
}
