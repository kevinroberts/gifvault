package com.vinberts.gifvault.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 *
 */
@Entity
@Table(name = "GIF_FOLDER")
public class GifFolder implements Serializable {

    public GifFolder() {
    }

    public GifFolder(String id, String name, Date createdAt, Collection<GifVault> gifVaultEntries) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.gifVaultEntries = gifVaultEntries;
    }

    @Id
    private String id;

    @Column(nullable = false, length = 800, unique = true)
    private String name;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToMany(mappedBy = "folder", fetch = FetchType.EAGER)
    private Collection<GifVault> gifVaultEntries;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final GifFolder gifFolder = (GifFolder) o;

        return new EqualsBuilder()
                .append(name, gifFolder.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .toHashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Collection<GifVault> getGifVaultEntries() {
        return gifVaultEntries;
    }

    public void setGifVaultEntries(Collection<GifVault> gifVaultEntries) {
        this.gifVaultEntries = gifVaultEntries;
    }
}
