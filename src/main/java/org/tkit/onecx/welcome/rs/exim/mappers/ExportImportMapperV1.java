package org.tkit.onecx.welcome.rs.exim.mappers;

import java.time.OffsetDateTime;
import java.util.*;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.onecx.welcome.domain.models.Image;
import org.tkit.onecx.welcome.domain.models.ImageData;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.welcome.rs.exim.model.EximImageDTOV1;
import gen.org.tkit.onecx.welcome.rs.exim.model.ImageDataDTOV1;
import gen.org.tkit.onecx.welcome.rs.exim.model.ImageInfoDTOV1;
import gen.org.tkit.onecx.welcome.rs.exim.model.WelcomeSnapshotDTOV1;

@Mapper(uses = OffsetDateTimeMapper.class)
public interface ExportImportMapperV1 {

    default WelcomeSnapshotDTOV1 map(List<Image> images) {
        WelcomeSnapshotDTOV1 snapshot = new WelcomeSnapshotDTOV1();
        List<EximImageDTOV1> imageList = new ArrayList<>();
        images.forEach(image -> {
            imageList.add(mapExim(image));
        });
        snapshot.setImages(imageList);
        snapshot.setId(UUID.randomUUID().toString());
        snapshot.setCreated(OffsetDateTime.now());
        return snapshot;
    }

    default EximImageDTOV1 mapExim(Image image) {
        EximImageDTOV1 eximImageDTOV1 = new EximImageDTOV1();
        eximImageDTOV1.setImage(map(image));
        eximImageDTOV1.setImageData(map(image.getImageDataId()));
        return eximImageDTOV1;
    }

    @Mapping(target = "imageId", ignore = true)
    ImageInfoDTOV1 map(Image image);

    @Mapping(target = "imageId", ignore = true)
    @Mapping(target = "imageData", source = "imageContent")
    ImageDataDTOV1 map(ImageData imageData);

    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "imageContent", source = "imageData")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    ImageData mapToData(ImageDataDTOV1 imageData);

    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "imageDataId", source = "createdData")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    Image mapToInfo(ImageInfoDTOV1 imageInfo, ImageData createdData, String workspaceName);
}
