package com.motioncare.user.repository;

import com.motioncare.user.model.Role;
import com.motioncare.user.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserRepository {
    
    @Select("SELECT * FROM users WHERE username = #{username}")
    Optional<User> findByUsername(String username);
    
    
    @Select("SELECT * FROM users WHERE id = #{id}")
    Optional<User> findById(Long id);
    
    @Select("SELECT * FROM users")
    List<User> findAll();
    
    @Insert("INSERT INTO users (username, password, first_name, last_name, enabled, created_at, updated_at) " +
            "VALUES (#{username}, #{password}, #{firstName}, #{lastName}, #{enabled}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(User user);
    
    @Update("UPDATE users " +
            "SET username = #{username}, " +
            "    password = #{password}, " +
            "    first_name = #{firstName}, " +
            "    last_name = #{lastName}, " +
            "    enabled = #{enabled}, " +
            "    updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int update(User user);
    
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(Long id);
    
    @Insert("INSERT INTO user_roles (user_id, role_id) VALUES (#{userId}, #{roleId})")
    void assignRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    @Delete("DELETE FROM user_roles WHERE user_id = #{userId} AND role_id = #{roleId}")
    void removeRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    @Select("SELECT COUNT(*) > 0 FROM users WHERE username = #{username}")
    boolean existsByUsername(String username);
    
    
    // Automatic mapping handles snake_case to camelCase conversion
    @Select("SELECT r.* FROM roles r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Role> findRolesByUserId(Long userId);
}
