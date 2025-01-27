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
@SuppressWarnings("java:S2160")
public class Image extends TraceableEntity {

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "WORKSPACE_NAME", nullable = false, columnDefinition = "VARCHAR(255) default 'undefined'")
    private String workspaceName;

    @Column(name = "POSITION")
    private Integer position;

    @Column(name = "OBJECT_FIT")
    @Enumerated(EnumType.STRING)
    private ObjectFit objectFit;

    @Column(name = "OBJECT_POSITION")
    private String objectPosition;

    @Column(name = "BACKGROUND_COLOR")
    private String backgroundColor;

    @Column(name = "VISIBLE")
    private boolean visible;

    @Column(name = "URL")
    private String url;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "IMAGE_DATA")
    private ImageData imageDataId;

    public enum ObjectFit {
        NONE,
        CONTAIN,
        COVER,
        FILL,
        SCALE_DOWN
    }
}
