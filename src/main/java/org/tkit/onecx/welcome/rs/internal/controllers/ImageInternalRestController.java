package org.tkit.onecx.welcome.rs.internal.controllers;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.*;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.welcome.domain.daos.ImageDAO;
import org.tkit.onecx.welcome.domain.daos.ImageDataDAO;
import org.tkit.onecx.welcome.domain.models.Image;
import org.tkit.onecx.welcome.domain.models.ImageData;
import org.tkit.onecx.welcome.rs.internal.mappers.ExceptionMapper;
import org.tkit.onecx.welcome.rs.internal.mappers.ImageMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.welcome.rs.internal.ImagesInternalApi;
import gen.org.tkit.onecx.welcome.rs.internal.model.ImageInfoDTO;
import gen.org.tkit.onecx.welcome.rs.internal.model.ProblemDetailResponseDTO;

@LogService
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ImageInternalRestController implements ImagesInternalApi {

    @Context
    UriInfo uriInfo;

    @Context
    HttpHeaders httpHeaders;

    @Inject
    ImageMapper imageMapper;

    @Inject
    ImageDataDAO imageDataDAO;

    @Inject
    ImageDAO imageInfoDAO;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response createImage(Integer contentLength, byte[] body) {
        var contentType = httpHeaders.getMediaType();
        contentType = new MediaType(contentType.getType(), contentType.getSubtype());
        var image = imageMapper.create(contentType.toString(), contentLength, body);
        var createdImage = imageDataDAO.create(image);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(createdImage.getId()).build())
                .entity(imageMapper.mapResponse(createdImage))
                .build();
    }

    @Override
    @Transactional
    public Response createImageInfo(ImageInfoDTO imageInfoDTO) {
        ImageData image = null;
        if (imageInfoDTO.getImageId() != null) {
            image = imageDataDAO.findById(imageInfoDTO.getImageId());
        }
        Image imageInfo = imageMapper.map(imageInfoDTO, image);
        var createdImageInfo = imageInfoDAO.create(imageInfo);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(createdImageInfo.getId()).build())
                .entity(imageMapper.map(createdImageInfo)).build();
    }

    @Override
    @Transactional
    public Response deleteImageInfoById(String id) {
        Image info = imageInfoDAO.findById(id);
        if (info != null) {
            imageInfoDAO.delete(info);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public Response getAllImageInfosByWorkspaceName(String workspaceName) {
        List<ImageInfoDTO> imageInfos;
        imageInfos = imageMapper.mapInfoList(imageInfoDAO.findAllByWorkspaceName(workspaceName).toList());
        return Response.ok().entity(imageInfos).build();
    }

    @Override
    public Response getImageById(String id) {
        var image = imageDataDAO.findById(id);
        if (image != null) {
            var imageResponse = imageMapper.mapResponse(image);
            return Response.status(Response.Status.OK).entity(imageResponse).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response getImageInfoById(String id) {
        var imageInfo = imageInfoDAO.findById(id);
        return Response.status(Response.Status.OK).entity(imageMapper.map(imageInfo)).build();
    }

    @Override
    public Response updateImageById(String id, Integer contentLength, byte[] body) {
        var imageToUpdate = imageDataDAO.findById(id);
        if (imageToUpdate == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var contentType = httpHeaders.getMediaType();
        contentType = new MediaType(contentType.getType(), contentType.getSubtype());
        var updatedImage = imageDataDAO.update(imageMapper.update(imageToUpdate, contentLength, body, contentType.toString()));
        return Response.status(Response.Status.OK).entity(imageMapper.mapResponse(updatedImage)).build();
    }

    @Override
    public Response updateImageInfo(String id, ImageInfoDTO imageInfoDTO) {
        var infoToUpdate = imageInfoDAO.findById(id);
        if (infoToUpdate == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (imageInfoDTO.getImageId() != null) {
            var imageToAssign = imageDataDAO.findById(imageInfoDTO.getImageId());
            if (imageToAssign != null) {
                infoToUpdate.setImageDataId(imageToAssign);
            }
        }
        imageMapper.updateInfo(infoToUpdate, imageInfoDTO);
        var updatedInfo = imageInfoDAO.update(infoToUpdate);
        return Response.status(Response.Status.OK).entity(imageMapper.map(updatedInfo)).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> daoException(OptimisticLockException ex) {
        return exceptionMapper.optimisticLock(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
