package com.motioncare.user.repository;

import com.motioncare.user.model.Role;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface RoleRepository {
    
    @Select("SELECT * FROM roles WHERE id = #{id}")
    Optional<Role> findById(Long id);
    
    @Select("SELECT * FROM roles WHERE name = #{name}")
    Optional<Role> findByName(String name);
    
    @Insert("INSERT INTO roles (name, description) VALUES (#{name}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Role role);
}
