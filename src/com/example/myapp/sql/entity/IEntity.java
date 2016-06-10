package com.example.myapp.sql.entity;

import java.io.Serializable;

/**
 * Обязательное наличие поля  @Id()    private Integer id;
 * Created by Anama on 21.10.2014.
 */
public interface IEntity extends Serializable{

    Integer getId();
    void setId(Integer id);
}
