package org.tkit.onecx.welcome.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "IMAGE_DATA")
@Entity
@SuppressWarnings("java:S2160")
public class ImageData extends TraceableEntity {

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "IMAGE_DATA")
    private byte[] imageContent;

    @Column(name = "DATA_LENGTH")
    private Integer dataLength;

    @Column(name = "MIME_TYPE")
    private String mimeType;
}
