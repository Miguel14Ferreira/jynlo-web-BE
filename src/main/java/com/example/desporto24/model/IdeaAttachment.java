package com.example.desporto24.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Private feedback evidence; never serialized through public/admin JSON listings. */
@Entity
@Table(name = "idea_attachment")
@Getter
@Setter
@NoArgsConstructor
public class IdeaAttachment {
    @Id
    @Column(name = "idea_id")
    private Long ideaId;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "content", nullable = false, columnDefinition = "bytea")
    private byte[] content;

    public IdeaAttachment(Long ideaId, byte[] content) {
        this.ideaId = ideaId;
        this.content = content;
    }
}
