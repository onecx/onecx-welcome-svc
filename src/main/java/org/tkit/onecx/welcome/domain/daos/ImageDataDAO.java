package org.tkit.onecx.welcome.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.onecx.welcome.domain.models.ImageData;
import org.tkit.quarkus.jpa.daos.AbstractDAO;

@ApplicationScoped
public class ImageDataDAO extends AbstractDAO<ImageData> {
}
