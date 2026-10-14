package com.fulfilment.application.monolith.location;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class LocationGatewayTest {
  private final LocationGateway gateway = new LocationGateway();

  @Test void resolvesKnownLocation() {
    var location = gateway.resolveByIdentifier("ZWOLLE-001");
    assertNotNull(location);
    assertEquals("ZWOLLE-001", location.identification);
    assertEquals(1, location.maxNumberOfWarehouses);
    assertEquals(40, location.maxCapacity);
  }

  @Test void unknownOrBlankLocationReturnsNull() {
    assertNull(gateway.resolveByIdentifier("UNKNOWN"));
    assertNull(gateway.resolveByIdentifier(null));
    assertNull(gateway.resolveByIdentifier(" "));
  }
}
