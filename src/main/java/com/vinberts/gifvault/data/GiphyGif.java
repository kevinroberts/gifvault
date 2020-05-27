package com.vinberts.gifvault.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
@Table(name = "GIPHY_GIFS")
public class GiphyGif {

    @Id
    @Column(name = "id")
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "gifvault_giphy",
            joinColumns =
                    { @JoinColumn(name = "vault_id", referencedColumnName = "id") },
            inverseJoinColumns =
                    { @JoinColumn(name = "giphy_id", referencedColumnName = "id") })
    private GifVault gifVaultEntry;

    @Column(nullable = false, length = 1500)
    private String url;

    @Column(nullable = false, length = 300)
    private String bitlyUrl;

    @Column(nullable = false, length = 1500)
    private String embedUrl;

    @Column(name = "username", length = 300)
    private String username;

    @Column(name = "giphy_title", length = 800)
    private String giphyTitle;

    @Column(name="rating", length = 10)
    private String rating;

    @Column(name = "is_sticker")
    private boolean isSticker;

    @Column(name = "original_mp4", length = 1500)
    private String originalMp4;

    @Column(name = "original_url", length = 1500)
    private String originalUrl;

    @Column(name = "fixed_height_mp4", length = 1500)
    private String fixedHeightMp4;

    @Column(name = "fixed_height_url", length = 1500)
    private String fixedHeightUrl;

    @Column(name = "imported_at", nullable = false, columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date importedAt;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final GiphyGif giphyGif = (GiphyGif) o;

        return new EqualsBuilder()
                .append(id, giphyGif.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }
}
