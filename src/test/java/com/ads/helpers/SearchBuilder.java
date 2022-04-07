package com.ads.helpers;

import com.ads.domain.OperationTypes;
import com.ads.domain.Search;

public class SearchBuilder {
  private String countryId;
  private String propertyTypeId;
  private String locationId;
  private OperationTypes operationType;

  public static SearchBuilder aSearch() {
    return new SearchBuilder();
  }

  public Search build() {
    return new Search(countryId, operationType,
        propertyTypeId, locationId
    );
  }

  public SearchBuilder withCountryId(String countryId) {
    this.countryId = countryId;
    return this;
  }

  public SearchBuilder withPropertyTypeId(String propertyTypeId) {
    this.propertyTypeId = propertyTypeId;
    return this;
  }

  public SearchBuilder withLocationId(String locationId) {
    this.locationId = locationId;
    return this;
  }

  public SearchBuilder asSale() {
    this.operationType = OperationTypes.Sale;
    return this;
  }
}
