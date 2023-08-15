package com.github.explore_with_me.main.user.service;

import com.github.explore_with_me.main.user.dto.UserDto;

import java.util.List;

public interface UserService {



    void deleteUser(Long userId);

    List<UserDto> getUsersInfo(List<Long> ids, int from, int size);

    UserDto createUser(UserDto userDto);
}
