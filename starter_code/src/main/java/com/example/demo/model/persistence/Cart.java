package com.example.demo.model.persistence;

import com.example.demo.exceptions.ItemNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cart")
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    @Column
    private Long id;
    
    @ManyToMany
    @JsonProperty
    @Column
    private List<Item> items;
    
    @OneToOne(mappedBy = "cart")
    @JsonProperty
    @JsonIgnore
    private User user;
    
    @Column
    @JsonProperty
    private BigDecimal total;
    
    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    private void initialiseItemsAndTotalIfRequired() {
        if (items == null && total == null) {
            items = new ArrayList<>();
            total = new BigDecimal(0);
        }
        Objects.requireNonNull(items);
        Objects.requireNonNull(total);
    }
    
    public void addItem(Item item) {
        Objects.requireNonNull(item);
        initialiseItemsAndTotalIfRequired();

        items.add(item);
        total = total.add(item.getPrice());
    }

    public void removeItem(Item item) {
        Objects.requireNonNull(item);
        initialiseItemsAndTotalIfRequired();

        if (items.remove(item)) {
            total = total.subtract(item.getPrice());
        }
        else {
            throw new ItemNotFoundException();
        }
    }

}
