package com.gaejangmo.apiserver.model.notice.domain;

import com.gaejangmo.apiserver.model.common.domain.BaseTimeEntity;
import com.gaejangmo.apiserver.model.notice.domain.converter.NoticeTypeConverter;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@Entity
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = NoticeTypeConverter.class)
    private NoticeType noticeType;

    @Column(nullable = false)
    private String header;

    @Column(nullable = false)
    @Lob
    private String contents;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deleted;

    @Column(nullable = false, unique = true)
    private String createdDate;

    @Builder
    public Notice(final NoticeType noticeType, final String header, final String contents) {
        this.noticeType = noticeType;
        this.header = header;
        this.contents = contents;
        this.deleted = false;
        this.createdDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public void delete() {
        this.deleted = true;
    }
}
