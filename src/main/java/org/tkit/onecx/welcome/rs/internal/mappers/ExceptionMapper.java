package org.tkit.onecx.welcome.rs.internal.mappers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.welcome.rs.internal.model.ProblemDetailInvalidParamDTO;
import gen.org.tkit.onecx.welcome.rs.internal.model.ProblemDetailParamDTO;
import gen.org.tkit.onecx.welcome.rs.internal.model.ProblemDetailResponseDTO;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ExceptionMapper {

    default RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        var dto = exception(ErrorKeys.CONSTRAINT_VIOLATIONS.name(), ex.getMessage());
        dto.setInvalidParams(createErrorValidationResponse(ex.getConstraintViolations()));
        return RestResponse.status(Response.Status.BAD_REQUEST, dto);
    }

    default RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        var dto = exception(ex.getMessageKey().name(), ex.getConstraints());
        dto.setParams(map(ex.namedParameters));
        return RestResponse.status(Response.Status.BAD_REQUEST, dto);
    }

    @LogService(log = false)
    default RestResponse<ProblemDetailResponseDTO> optimisticLock(OptimisticLockException ex) {
        var dto = exception(ErrorKeys.OPTIMISTIC_LOCK.name(), ex.getMessage());
        return RestResponse.status(Response.Status.BAD_REQUEST, dto);
    }

    @Mapping(target = "removeParamsItem", ignore = true)
    @Mapping(target = "params", ignore = true)
    @Mapping(target = "invalidParams", ignore = true)
    @Mapping(target = "removeInvalidParamsItem", ignore = true)
    ProblemDetailResponseDTO exception(String errorCode, String detail);

    default List<ProblemDetailParamDTO> map(Map<String, Object> params) {
        if (params == null) {
            return List.of();
        }
        return params.entrySet().stream().map(e -> {
            var item = new ProblemDetailParamDTO();
            item.setKey(e.getKey());
            if (e.getValue() != null) {
                item.setValue(e.getValue().toString());
            }
            return item;
        }).toList();
    }

    List<ProblemDetailInvalidParamDTO> createErrorValidationResponse(
            Set<ConstraintViolation<?>> constraintViolation);

    @Mapping(target = "name", source = "propertyPath")
    @Mapping(target = "message", source = "message")
    ProblemDetailInvalidParamDTO createError(ConstraintViolation<?> constraintViolation);

    default String mapPath(Path path) {
        return path.toString();
    }

    enum ErrorKeys {
        OPTIMISTIC_LOCK,
        CONSTRAINT_VIOLATIONS;
    }
}
