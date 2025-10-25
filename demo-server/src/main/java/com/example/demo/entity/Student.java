package com.example.demo.entity;

import lombok.Data;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Id;

@Data
@Table("student")
public class Student {

    @Id
    private Long id;
    private Integer age;
    private String name;
}
