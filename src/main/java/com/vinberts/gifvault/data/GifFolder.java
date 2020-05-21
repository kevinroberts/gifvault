package com.vinberts.gifvault.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

/**
 *
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GIF_FOLDER")
public class GifFolder {

    @Id
    private String id;

    @Column(nullable = false, length = 800, unique = true)
    private String name;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToMany(mappedBy = "folder", fetch = FetchType.LAZY)
    private Collection<GifVault> gifVaultEntries;

    @Override
    public String toString() {
        if (Objects.nonNull(gifVaultEntries)) {
            return "GifFolder{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", createdAt=" + createdAt +
                    ", gifVaultEntries=" + gifVaultEntries.size() +
                    '}';
        }
        return "GifFolder{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", gifVaultEntries=[]" +
                '}';
    }
}
