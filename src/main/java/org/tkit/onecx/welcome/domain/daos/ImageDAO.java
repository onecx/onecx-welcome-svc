package org.tkit.onecx.welcome.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.onecx.welcome.domain.models.Image;
import org.tkit.quarkus.jpa.daos.AbstractDAO;

@ApplicationScoped
public class ImageDAO extends AbstractDAO<Image> {
}
