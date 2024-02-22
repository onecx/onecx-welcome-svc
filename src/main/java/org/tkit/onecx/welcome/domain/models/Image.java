package org.tkit.onecx.welcome.domain.models;

import jakarta.persistence.*;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "IMAGE")
@Entity
//@NamedEntityGraph(name = Image.IMAGE_FULL, includeAllAttributes = true)
public class Image extends TraceableEntity {

    // public static final String IMAGE_FULL = "Image.full";

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "POSITION")
    private Integer position;

    @Column(name = "VISIBLE")
    private boolean visible;

    @Column(name = "URL")
    private String url;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_DATA")
    private ImageData imageDataId;
}
