package com.vinberts.gifvault.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 *
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GIF_VAULT")
public class GifVault {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne(targetEntity = GifFolder.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "gif_folder_id", foreignKey = @ForeignKey(name = "fk_gif_folder"))
    private GifFolder folder;

    @Column(nullable = false, length = 800)
    private String title;

    @Column(nullable = false, length = 800)
    private String storageLocation;

    @Column(length = 800)
    private String mp4Filename;

    @Column(length = 800)
    private String gifFilename;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at", nullable = true, columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @OneToOne(mappedBy = "gifVaultEntry")
    private GiphyGif giphyGif;

    @Override
    public String toString() {
        return "GifVault{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", storageLocation='" + storageLocation + '\'' +
                ", mp4Filename='" + mp4Filename + '\'' +
                ", gifFilename='" + gifFilename + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final GifVault gifVault = (GifVault) o;

        return new EqualsBuilder()
                .append(id, gifVault.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }
}
