package com.vinberts.gifvault.data;

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
@Table(name = "GIF_VAULT")
public class GifVault {

    public GifVault() {
    }

    public GifVault(String id, GifFolder folder, String title, String storageLocation, String mp4Filename, String gifFilename, Date createdAt, Date updatedAt, GiphyGif giphyGif) {
        this.id = id;
        this.folder = folder;
        this.title = title;
        this.storageLocation = storageLocation;
        this.mp4Filename = mp4Filename;
        this.gifFilename = gifFilename;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.giphyGif = giphyGif;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GifFolder getFolder() {
        return folder;
    }

    public void setFolder(GifFolder folder) {
        this.folder = folder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getMp4Filename() {
        return mp4Filename;
    }

    public void setMp4Filename(String mp4Filename) {
        this.mp4Filename = mp4Filename;
    }

    public String getGifFilename() {
        return gifFilename;
    }

    public void setGifFilename(String gifFilename) {
        this.gifFilename = gifFilename;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public GiphyGif getGiphyGif() {
        return giphyGif;
    }

    public void setGiphyGif(GiphyGif giphyGif) {
        this.giphyGif = giphyGif;
    }
}
