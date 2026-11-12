package com.example.desporto24.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "idea_extra_attachment")
@Getter
@NoArgsConstructor
@IdClass(IdeaExtraAttachment.Key.class)
public class IdeaExtraAttachment {
    @Id @Column(name = "idea_id")
    private Long ideaId;
    @Id @Column(name = "image_index")
    private Integer imageIndex;
    @Column(name = "filename", nullable = false, length = 160)
    private String filename;
    @Column(name = "mime_type", nullable = false, length = 40)
    private String mimeType;
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "content", nullable = false, columnDefinition = "bytea")
    private byte[] content;

    public IdeaExtraAttachment(Long ideaId, int imageIndex, String filename, String mimeType, byte[] content) {
        this.ideaId = ideaId;
        this.imageIndex = imageIndex;
        this.filename = filename;
        this.mimeType = mimeType;
        this.content = content;
    }

    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class Key implements java.io.Serializable {
        private Long ideaId;
        private Integer imageIndex;
        @Override public boolean equals(Object obj) {
            if (!(obj instanceof Key other)) return false;
            return java.util.Objects.equals(ideaId, other.ideaId)
                    && java.util.Objects.equals(imageIndex, other.imageIndex);
        }
        @Override public int hashCode() { return java.util.Objects.hash(ideaId, imageIndex); }
    }
}
