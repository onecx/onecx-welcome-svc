package org.tkit.onecx.welcome.rs.exim.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.welcome.domain.services.ImageService;
import org.tkit.onecx.welcome.rs.exim.mappers.ExportImportExceptionMapperV1;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.welcome.rs.exim.ImagesExportImportApi;
import gen.org.tkit.onecx.welcome.rs.exim.model.EximProblemDetailResponseDTOV1;
import gen.org.tkit.onecx.welcome.rs.exim.model.ExportWelcomeRequestDTOV1;
import gen.org.tkit.onecx.welcome.rs.exim.model.WelcomeSnapshotDTOV1;

@LogService
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ImageEximRestControllerV1 implements ImagesExportImportApi {

    @Inject
    ImageService imageService;

    @Inject
    ExportImportExceptionMapperV1 exceptionMapper;

    @Override
    public Response exportImages(ExportWelcomeRequestDTOV1 exportWelcomeRequestDTOV1) {
        var snapshot = imageService.exportByWorkspaceName(exportWelcomeRequestDTOV1.getWorkspaceName());
        return Response.status(Response.Status.OK).entity(snapshot).build();
    }

    @Override
    public Response importImages(String workspaceName, WelcomeSnapshotDTOV1 welcomeSnapshotDTOV1) {
        imageService.importSnapshot(welcomeSnapshotDTOV1, workspaceName);
        return Response.status(Response.Status.OK).build();
    }

    @ServerExceptionMapper
    public RestResponse<EximProblemDetailResponseDTOV1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
