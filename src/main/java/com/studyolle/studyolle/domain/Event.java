package com.studyolle.studyolle.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Study study;

    @ManyToOne
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    private Integer limitOfEnrollments;

    @OneToMany(mappedBy = "event") //"..edBy" means "Your data change will not be stored in this entity, but in 'event' entity" -> Owner of relation is "Event", so Event will have foreign keys
    private List<Enrollment> enrollments;

    @Enumerated(EnumType.STRING) //EnumType.ORDINAL will set enum values numbers. This is very dangerous, because enum values' order can be changed. USE STRING!
    private EventType eventType;


}
