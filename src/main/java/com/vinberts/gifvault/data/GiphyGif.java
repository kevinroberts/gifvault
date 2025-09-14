package com.vinberts.gifvault.data;

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
@Table(name = "GIPHY_GIFS")
public class GiphyGif {

    public GiphyGif() {
    }

    public GiphyGif(String id, GifVault gifVaultEntry, String url, String bitlyUrl, String embedUrl, String username, String giphyTitle, String rating, boolean isSticker, String originalMp4, String originalUrl, String fixedHeightMp4, String fixedHeightUrl, Date importedAt) {
        this.id = id;
        this.gifVaultEntry = gifVaultEntry;
        this.url = url;
        this.bitlyUrl = bitlyUrl;
        this.embedUrl = embedUrl;
        this.username = username;
        this.giphyTitle = giphyTitle;
        this.rating = rating;
        this.isSticker = isSticker;
        this.originalMp4 = originalMp4;
        this.originalUrl = originalUrl;
        this.fixedHeightMp4 = fixedHeightMp4;
        this.fixedHeightUrl = fixedHeightUrl;
        this.importedAt = importedAt;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GifVault getGifVaultEntry() {
        return gifVaultEntry;
    }

    public void setGifVaultEntry(GifVault gifVaultEntry) {
        this.gifVaultEntry = gifVaultEntry;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBitlyUrl() {
        return bitlyUrl;
    }

    public void setBitlyUrl(String bitlyUrl) {
        this.bitlyUrl = bitlyUrl;
    }

    public String getEmbedUrl() {
        return embedUrl;
    }

    public void setEmbedUrl(String embedUrl) {
        this.embedUrl = embedUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGiphyTitle() {
        return giphyTitle;
    }

    public void setGiphyTitle(String giphyTitle) {
        this.giphyTitle = giphyTitle;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public boolean isSticker() {
        return isSticker;
    }

    public void setSticker(boolean sticker) {
        isSticker = sticker;
    }

    public String getOriginalMp4() {
        return originalMp4;
    }

    public void setOriginalMp4(String originalMp4) {
        this.originalMp4 = originalMp4;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getFixedHeightMp4() {
        return fixedHeightMp4;
    }

    public void setFixedHeightMp4(String fixedHeightMp4) {
        this.fixedHeightMp4 = fixedHeightMp4;
    }

    public String getFixedHeightUrl() {
        return fixedHeightUrl;
    }

    public void setFixedHeightUrl(String fixedHeightUrl) {
        this.fixedHeightUrl = fixedHeightUrl;
    }

    public Date getImportedAt() {
        return importedAt;
    }

    public void setImportedAt(Date importedAt) {
        this.importedAt = importedAt;
    }
}
