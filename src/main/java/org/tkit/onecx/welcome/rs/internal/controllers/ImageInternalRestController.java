package org.tkit.onecx.welcome.rs.internal.controllers;

import java.io.File;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.welcome.rs.internal.ImagesInternalApi;
import gen.org.tkit.onecx.welcome.rs.internal.model.ImageInfoDTO;

@LogService
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ImageInternalRestController implements ImagesInternalApi {
    @Override
    public Response createImage(Integer contentLength, File body) {
        return null;
    }

    @Override
    public Response createImageInfo(ImageInfoDTO imageInfoDTO) {
        return null;
    }

    @Override
    public Response deleteImageById(String id) {
        return null;
    }

    @Override
    public Response deleteImageInfoById(String id) {
        return null;
    }

    @Override
    public Response getAllImages() {
        return null;
    }

    @Override
    public Response getImageById(String id) {
        return null;
    }

    @Override
    public Response getImageInfoById(String id) {
        return null;
    }

    @Override
    public Response updateImageById(String id, Integer contentLength, File body) {
        return null;
    }

    @Override
    public Response updateImageInfo(String id, ImageInfoDTO imageInfoDTO) {
        return null;
    }
}
