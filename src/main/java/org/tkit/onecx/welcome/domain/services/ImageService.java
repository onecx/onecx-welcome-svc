package org.tkit.onecx.welcome.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.welcome.domain.daos.ImageDAO;
import org.tkit.onecx.welcome.domain.daos.ImageDataDAO;
import org.tkit.onecx.welcome.domain.models.ImageData;
import org.tkit.onecx.welcome.rs.exim.mappers.ExportImportMapperV1;

import gen.org.tkit.onecx.welcome.rs.exim.model.WelcomeSnapshotDTOV1;

@ApplicationScoped
public class ImageService {

    @Inject
    ImageDAO imageDAO;

    @Inject
    ImageDataDAO imageDataDAO;

    @Inject
    ExportImportMapperV1 eximMapper;

    public WelcomeSnapshotDTOV1 exportByWorkspaceName(String workspaceName) {
        var images = imageDAO.findAllByWorkspaceName(workspaceName).toList();
        return eximMapper.map(images);
    }

    @Transactional
    public void importSnapshot(WelcomeSnapshotDTOV1 welcomeSnapshotDTOV1, String workspace) {
        imageDAO.deleteAllByWorkspaceName(workspace);
        welcomeSnapshotDTOV1.getConfig().getImages().forEach(eximImageDTOV1 -> {
            ImageData createdData = null;
            if (eximImageDTOV1.getImageData() != null) {
                var imageDataToCreate = eximMapper.mapToData(eximImageDTOV1.getImageData());
                createdData = imageDataDAO.create(imageDataToCreate);
            }
            var imageInfoToCreate = eximMapper.mapToInfo(eximImageDTOV1.getImage(), createdData, workspace);
            imageDAO.create(imageInfoToCreate);
        });
    }
}
