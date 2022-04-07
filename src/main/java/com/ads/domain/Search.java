package com.ads.domain;

import java.util.Objects;

public class Search {
  private final String countryId;
  private final OperationTypes operationTypes;
  private final String propertyTypeId;
  private final String locationId;

  public Search(String countryId, OperationTypes operationTypes,
                String propertyTypeId, String locationId) {

    this.countryId = countryId;
    this.operationTypes = operationTypes;
    this.propertyTypeId = propertyTypeId;
    this.locationId = locationId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Search search = (Search) o;
    return Objects.equals(countryId, search.countryId) && operationTypes == search.operationTypes && Objects.equals(propertyTypeId, search.propertyTypeId) && Objects.equals(locationId, search.locationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(countryId, operationTypes, propertyTypeId, locationId);
  }
}
