package com.example.springbootreactstream.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Blob;

@Data
@Entity
@Builder
@Table(name = "TEST_FILE")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TestFile {

    /**
     * Идентификатор
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "testFileSequence")
    @SequenceGenerator(name = "testFileSequence", sequenceName = "SEQ_TEST_FILE", allocationSize = 1)
    private Long id;

    /**
     * Данные
     */
    @Lob
    @Column(name = "bytes", columnDefinition="BLOB")
    @JsonProperty("bytes")
    private Blob bytes;

    /**
     * Имя файла
     */
    @Column(name = "name", columnDefinition="nvarchar2(255)")
    @JsonProperty("name")
    private String name;
}
