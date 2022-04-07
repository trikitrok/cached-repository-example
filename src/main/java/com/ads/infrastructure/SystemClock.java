package com.ads.infrastructure;

import com.ads.domain.Clock;

public class SystemClock implements Clock {
  @Override
  public long getTimeInMillis() {
    return 0;
  }
}
