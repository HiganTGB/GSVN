package com.tgb.gsvnbackend.model.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@Table(name = "fandoms",schema = "product")
public class Fandom extends AbstractMappedEntity{
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fandom_id", unique = true, nullable = false, updatable = false)
    private Integer fandomId;
    @Column(name = "fandom_title")
    private String title;
}
