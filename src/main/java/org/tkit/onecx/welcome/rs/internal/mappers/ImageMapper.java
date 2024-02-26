package org.tkit.onecx.welcome.rs.internal.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.onecx.welcome.domain.models.Image;
import org.tkit.onecx.welcome.domain.models.ImageData;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.welcome.rs.internal.model.ImageDataResponseDTO;
import gen.org.tkit.onecx.welcome.rs.internal.model.ImageInfoDTO;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ImageMapper {
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "mimeType", source = "contentType")
    @Mapping(target = "imageContent", source = "data")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataLength", source = "contentLength")
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    ImageData create(String contentType, Integer contentLength, byte[] data);

    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "imageDataId", source = "image")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    Image map(ImageInfoDTO imageInfoDTO, ImageData image);

    @Mapping(target = "imageId", source = "imageDataId.id")
    ImageInfoDTO map(Image image);

    default ImageData update(ImageData imageToUpdate, Integer contentLength, byte[] body, String mimeType) {
        imageToUpdate.setMimeType(mimeType);
        imageToUpdate.setImageContent(body);
        imageToUpdate.setDataLength(contentLength);
        return imageToUpdate;
    }

    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "imageDataId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    void updateInfo(@MappingTarget Image infoToUpdate, ImageInfoDTO imageInfoDTO);

    @Mapping(target = "imageData", source = "imageContent")
    @Mapping(target = "imageId", source = "id")
    ImageDataResponseDTO mapResponse(ImageData image);

    List<ImageInfoDTO> mapInfoList(List<Image> list);
}
