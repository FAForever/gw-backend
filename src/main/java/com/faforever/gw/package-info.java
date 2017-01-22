@TypeDef(
        name = "pg-uuid",
        defaultForType = UUID.class,
        typeClass = PostgresUUIDType.class
)
package com.faforever.gw;

import org.hibernate.annotations.TypeDef;
import org.hibernate.type.PostgresUUIDType;

import java.util.UUID;