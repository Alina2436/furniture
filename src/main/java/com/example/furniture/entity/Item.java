package com.example.furniture.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('items_id_seq'")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false, precision = 9, scale = 2)
    private BigDecimal price;

    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    @Column(name = "count", nullable = false)
    private Long count;

    @Column(name = "img")
    private String img;

    @OneToOne(mappedBy = "item")
    private ItemRating itemRating;

    @ManyToMany(mappedBy = "likes")
    private Set<User> likedBy;

}